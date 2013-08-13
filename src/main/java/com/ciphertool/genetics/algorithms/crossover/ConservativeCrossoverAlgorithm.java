/**
 * Copyright 2012 George Belden
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
import com.ciphertool.genetics.util.FitnessEvaluator;

public class ConservativeCrossoverAlgorithm implements CrossoverAlgorithm {
	private FitnessEvaluator fitnessEvaluator;
	private MutationAlgorithm mutationAlgorithm;
	private boolean mutateDuringCrossover;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.genetics.algorithms.CrossoverAlgorithm#crossover(com.
	 * ciphertool.genetics.entities.Chromosome,
	 * com.ciphertool.genetics.entities.Chromosome)
	 */
	@Override
	public List<Chromosome> crossover(Chromosome parentA, Chromosome parentB) {
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
	public Chromosome performCrossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = (Chromosome) parentA.clone();

		int childSequencePosition = 0;
		int parentSequencePosition = 0;
		int childGeneIndex = 0;
		int parentGeneIndex = 0;
		Gene geneCopy = null;
		Double originalFitness = 0.0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childGeneIndex < child.getGenes().size()
				&& childGeneIndex < parentB.getGenes().size()) {
			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (childSequencePosition == parentSequencePosition) {
				if (child.getGenes().get(childGeneIndex).size() == parentB.getGenes().get(
						parentGeneIndex).size()) {
					geneCopy = child.getGenes().get(childGeneIndex).clone();

					originalFitness = child.getFitness();

					child.replaceGene(childGeneIndex, parentB.getGenes().get(parentGeneIndex)
							.clone());

					fitnessEvaluator.evaluate(child);

					/*
					 * Revert to the original gene if this decreased fitness.
					 * It's ok to let non-beneficial changes progress, as long
					 * as they are not detrimental.
					 */
					if (child.getFitness() < originalFitness) {
						child.replaceGene(childGeneIndex, geneCopy);

						/*
						 * Reset the fitness to what it was before the
						 * replacement.
						 */
						child.setFitness(originalFitness);
					}
				}

				childSequencePosition += child.getGenes().get(childGeneIndex).size();
				parentSequencePosition += parentB.getGenes().get(parentGeneIndex).size();

				childGeneIndex++;
				parentGeneIndex++;
			} else if (childSequencePosition > parentSequencePosition) {
				parentSequencePosition += parentB.getGenes().get(parentGeneIndex).size();
				parentGeneIndex++;
			} else {
				childSequencePosition += child.getGenes().get(childGeneIndex).size();
				childGeneIndex++;
			}
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
	@Required
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
