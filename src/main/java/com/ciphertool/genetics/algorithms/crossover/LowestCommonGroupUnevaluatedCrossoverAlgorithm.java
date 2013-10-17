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
import com.ciphertool.genetics.util.fitness.FitnessEvaluator;

public class LowestCommonGroupUnevaluatedCrossoverAlgorithm implements CrossoverAlgorithm {
	private MutationAlgorithm mutationAlgorithm;
	private boolean mutateDuringCrossover;

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

		int parentBSize = parentB.getGenes().size();
		int childSequencePosition = 0;
		int parentSequencePosition = 0;
		int childBeginGeneIndex = 0;
		int childEndGeneIndex = 0;
		int parentBeginGeneIndex = 0;
		int parentEndGeneIndex = 0;
		int insertCount = 0;
		int geneOffset = 0;

		childSequencePosition += child.getGenes().get(childBeginGeneIndex).size();
		parentSequencePosition += parentB.getGenes().get(parentBeginGeneIndex).size();

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childEndGeneIndex < child.getGenes().size() && parentEndGeneIndex < parentBSize) {
			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (childSequencePosition == parentSequencePosition) {
				/*
				 * Flip a coin to see whether we replace the group of words
				 */
				if (((int) (Math.random() * 2)) == 1) {
					/*
					 * Remove Genes from cloned child.
					 */
					for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
						child.removeGene(childBeginGeneIndex);
					}

					/*
					 * Insert cloned parent Genes into child. insertCount works
					 * as an offset so that the Genes are inserted in the
					 * correct order.
					 */
					insertCount = 0;
					for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
						child.insertGene(childBeginGeneIndex + insertCount, parentB.getGenes().get(
								j).clone());

						insertCount++;
					}

					/*
					 * Offset child gene indices by the number of Genes inserted
					 * from parentB, since the number of Genes inserted from
					 * parentB could be different than the number of Genes
					 * removed from child. The result can be either positive or
					 * negative.
					 */
					geneOffset = (parentEndGeneIndex - parentBeginGeneIndex)
							- (childEndGeneIndex - childBeginGeneIndex);
				} else {
					geneOffset = 0;
				}

				childEndGeneIndex += geneOffset + 1;
				parentEndGeneIndex++;

				childBeginGeneIndex = childEndGeneIndex;
				parentBeginGeneIndex = parentEndGeneIndex;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (childEndGeneIndex < child.getGenes().size()) {
					childSequencePosition += child.getGenes().get(childBeginGeneIndex).size();
				}

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (parentEndGeneIndex < parentBSize) {
					parentSequencePosition += parentB.getGenes().get(parentBeginGeneIndex).size();
				}
			} else if (childSequencePosition > parentSequencePosition) {
				parentEndGeneIndex++;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (parentEndGeneIndex < parentBSize) {
					parentSequencePosition += parentB.getGenes().get(parentEndGeneIndex).size();
				}
			} else { // (childSequencePosition < parentSequencePosition)
				childEndGeneIndex++;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (childEndGeneIndex < child.getGenes().size()) {
					childSequencePosition += child.getGenes().get(childEndGeneIndex).size();
				}
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
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		/*
		 * fitnessEvaluator is required by other crossover algorithms, so this
		 * is just to satisfy the interface.
		 */
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
