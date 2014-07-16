/**
 * Copyright 2013 George Belden
 * 
 * This file is part of ZodiacGenetics.
 * 
 * ZodiacGenetics is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ZodiacGenetics is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ZodiacGenetics. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.crossover;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class LowestCommonGroupCrossoverAlgorithm implements CrossoverAlgorithm {
	private FitnessEvaluator fitnessEvaluator;
	private MutationAlgorithm mutationAlgorithm;
	private boolean mutateDuringCrossover = false;

	@Override
	public List<Chromosome> crossover(Chromosome parentA, Chromosome parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<Chromosome> children = new ArrayList<Chromosome>();

		Chromosome firstChild = performCrossover(parentA, parentB);

		// The chromosome will be null if it's identical to one of its parents
		if (firstChild != null) {
			children.add(firstChild);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	/**
	 * This crossover algorithm does a conservative amount of changes since it
	 * only replaces genes that begin and end at the exact same sequence
	 * positions
	 */
	protected Chromosome performCrossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = (Chromosome) parentA.clone();

		int parentBSize = parentB.getGenes().size();
		int insertCount = 0;
		int geneOffset = 0;
		Double originalFitness = 0.0;
		List<Gene> childGeneCopies;

		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();

		crossoverProgressDto.setFirstChromosomeSequencePosition(child.getGenes().get(0).size());
		crossoverProgressDto.setSecondChromosomeSequencePosition(parentB.getGenes().get(0).size());

		int childBeginGeneIndex = 0, childEndGeneIndex = 0, parentBeginGeneIndex = 0, parentEndGeneIndex = 0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (crossoverProgressDto.getFirstChromosomeEndGeneIndex() < child.getGenes().size()
				&& crossoverProgressDto.getSecondChromosomeEndGeneIndex() < parentBSize) {
			childBeginGeneIndex = crossoverProgressDto.getFirstChromosomeBeginGeneIndex();
			childEndGeneIndex = crossoverProgressDto.getFirstChromosomeEndGeneIndex();
			parentBeginGeneIndex = crossoverProgressDto.getSecondChromosomeBeginGeneIndex();
			parentEndGeneIndex = crossoverProgressDto.getSecondChromosomeEndGeneIndex();

			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (crossoverProgressDto.getFirstChromosomeSequencePosition() == crossoverProgressDto
					.getSecondChromosomeSequencePosition()) {
				originalFitness = child.getFitness();

				/*
				 * Pull out Genes from child into a temporary List.
				 */
				childGeneCopies = new ArrayList<Gene>();
				for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
					childGeneCopies.add(child.getGenes().get(childBeginGeneIndex).clone());

					child.removeGene(childBeginGeneIndex);
				}

				/*
				 * Insert cloned parent Genes into child. insertCount works as
				 * an offset so that the Genes are inserted in the correct
				 * order.
				 */
				insertCount = 0;
				for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
					child.insertGene(childBeginGeneIndex + insertCount, parentB.getGenes().get(j)
							.clone());

					insertCount++;
				}

				double newFitness = fitnessEvaluator.evaluate(child);
				child.setFitness(newFitness);

				/*
				 * Revert to the original gene if this decreased fitness. It's
				 * ok to let non-beneficial changes progress, as long as they
				 * are not detrimental.
				 */
				if (newFitness < originalFitness) {
					/*
					 * Remove the parent Genes from the child.
					 */
					for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
						child.removeGene(childBeginGeneIndex);
					}

					/*
					 * Insert the child Gene copies back into the child.
					 */
					for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
						child.insertGene(i, childGeneCopies.remove(0));
					}

					/*
					 * Reset the fitness to what it was before the replacement.
					 */
					child.setFitness(originalFitness);

					geneOffset = 0;
				} else {
					/*
					 * Offset child gene indices by the number of Genes inserted
					 * from parentB, since the number of Genes inserted from
					 * parentB could be different than the number of Genes
					 * removed from child. The result can be either positive or
					 * negative.
					 */
					geneOffset = (parentEndGeneIndex - parentBeginGeneIndex)
							- (childEndGeneIndex - childBeginGeneIndex);
				}
			}

			LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
					parentB, geneOffset);
		}

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		// Don't return this child if it's identical to one of its parents
		if (child.equals(parentA) || child.equals(parentB)) {
			return null;
		}

		/*
		 * Child is guaranteed to have at least as good fitness as its parent
		 */
		return child;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Override
	@Required
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param mutateDuringCrossover
	 *            the mutateDuringCrossover to set
	 */
	@Override
	public void setMutateDuringCrossover(boolean mutateDuringCrossover) {
		this.mutateDuringCrossover = mutateDuringCrossover;
	}
}
