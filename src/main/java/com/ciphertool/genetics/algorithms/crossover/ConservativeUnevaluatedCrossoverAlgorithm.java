/**
 * Copyright 2015 George Belden
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

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class ConservativeUnevaluatedCrossoverAlgorithm implements CrossoverAlgorithm<KeylessChromosome> {
	private MutationAlgorithm<KeylessChromosome> mutationAlgorithm;
	private boolean mutateDuringCrossover = false;

	@Override
	public List<KeylessChromosome> crossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<KeylessChromosome> children = new ArrayList<KeylessChromosome>();

		KeylessChromosome firstChild = performCrossover(parentA, parentB);

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
	protected KeylessChromosome performCrossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		KeylessChromosome child = (KeylessChromosome) parentA.clone();

		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (crossoverProgressDto.getFirstChromosomeGeneIndex() < child.getGenes().size()
				&& crossoverProgressDto.getSecondChromosomeGeneIndex() < parentB.getGenes().size()) {
			/*
			 * Replace from parentB. We are extra careful here since genes won't
			 * match exactly with sequence position.
			 */
			if (crossoverProgressDto.getFirstChromosomeSequencePosition() == crossoverProgressDto
					.getSecondChromosomeSequencePosition()
					&& ((VariableLengthGene) child.getGenes().get(crossoverProgressDto.getFirstChromosomeGeneIndex())).size() == ((VariableLengthGene) parentB
							.getGenes().get(crossoverProgressDto.getSecondChromosomeGeneIndex())).size()) {
				child.replaceGene(crossoverProgressDto.getFirstChromosomeGeneIndex(), parentB.getGenes().get(
						crossoverProgressDto.getSecondChromosomeGeneIndex()).clone());
			}

			ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
					parentB);
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
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm<KeylessChromosome> mutationAlgorithm) {
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
