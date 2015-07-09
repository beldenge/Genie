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

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;
import com.ciphertool.genetics.util.Coin;

public class LowestCommonGroupUnevaluatedCrossoverAlgorithm implements CrossoverAlgorithm<KeylessChromosome> {
	private MutationAlgorithm<KeylessChromosome> mutationAlgorithm;
	private boolean mutateDuringCrossover = false;
	private Coin coin;

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
	 * This crossover algorithm does a conservative amount of changes since it only replaces genes that begin and end at
	 * the exact same sequence positions
	 */
	protected KeylessChromosome performCrossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		KeylessChromosome child = (KeylessChromosome) parentA.clone();

		int parentBSize = parentB.getGenes().size();
		int geneOffset = 0;

		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();

		crossoverProgressDto.setFirstChromosomeSequencePosition(((VariableLengthGene) child.getGenes().get(0)).size());
		crossoverProgressDto.setSecondChromosomeSequencePosition(((VariableLengthGene) parentB.getGenes().get(0))
				.size());

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an IndexOutOfBoundsException
		 */
		while (crossoverProgressDto.getFirstChromosomeEndGeneIndex() < child.getGenes().size()
				&& crossoverProgressDto.getSecondChromosomeEndGeneIndex() < parentBSize) {
			if (crossoverProgressDto.getFirstChromosomeSequencePosition() == crossoverProgressDto
					.getSecondChromosomeSequencePosition()) {
				attemptToReplaceGeneGroupInChild(crossoverProgressDto, child, parentB);
			}

			LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parentB, geneOffset);
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
	 * Replace Gene group in child from parentB. We are extra careful here since genes won't match exactly with sequence
	 * position.
	 * 
	 * @param crossoverProgressDto
	 *            the LowestCommonGroupCrossoverProgressDto
	 * @param child
	 *            the child Chromosome
	 * @param parentB
	 *            the parent Chromosome
	 * @return the geneOffset
	 */
	protected int attemptToReplaceGeneGroupInChild(LowestCommonGroupCrossoverProgressDto crossoverProgressDto,
			KeylessChromosome child, KeylessChromosome parentB) {
		int childBeginGeneIndex = crossoverProgressDto.getFirstChromosomeBeginGeneIndex();
		int childEndGeneIndex = crossoverProgressDto.getFirstChromosomeEndGeneIndex();
		int parentBeginGeneIndex = crossoverProgressDto.getSecondChromosomeBeginGeneIndex();
		int parentEndGeneIndex = crossoverProgressDto.getSecondChromosomeEndGeneIndex();

		/*
		 * Flip a coin to see whether we replace the group of words
		 */
		if (coin.flip()) {
			/*
			 * Remove Genes from cloned child.
			 */
			for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
				child.removeGene(childBeginGeneIndex);
			}

			/*
			 * Insert cloned parent Genes into child. insertCount works as an offset so that the Genes are inserted in
			 * the correct order.
			 */
			int insertCount = 0;
			for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
				child.insertGene(childBeginGeneIndex + insertCount, parentB.getGenes().get(j).clone());

				insertCount++;
			}

			/*
			 * Offset child gene indices by the number of Genes inserted from parentB, since the number of Genes
			 * inserted from parentB could be different than the number of Genes removed from child. The result can be
			 * either positive or negative.
			 */
			return (parentEndGeneIndex - parentBeginGeneIndex) - (childEndGeneIndex - childBeginGeneIndex);
		} else {
			return 0;
		}
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

	/**
	 * @param coin
	 *            the coin to set
	 */
	@Required
	public void setCoin(Coin coin) {
		this.coin = coin;
	}
}
