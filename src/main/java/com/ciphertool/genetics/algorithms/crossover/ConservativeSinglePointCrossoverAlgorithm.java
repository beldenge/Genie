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

import org.apache.log4j.Logger;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;
import com.ciphertool.genetics.util.RandomListElementSelector;

public class ConservativeSinglePointCrossoverAlgorithm implements CrossoverAlgorithm<KeylessChromosome> {
	private static Logger log = Logger.getLogger(ConservativeSinglePointCrossoverAlgorithm.class);
	private MutationAlgorithm<KeylessChromosome> mutationAlgorithm;
	private boolean mutateDuringCrossover = false;
	private RandomListElementSelector randomListElementSelector;

	/**
	 * This crossover algorithm finds all the points where both parent
	 * Chromosomes can safely be split in half without splitting a Gene, and
	 * then picks one of those at random as the centromere for crossover.
	 * 
	 * @see com.ciphertool.genetics.algorithms.crossover.zodiacengine.genetic.CrossoverAlgorithm#crossover(com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome,
	 *      com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome)
	 */
	@Override
	public List<KeylessChromosome> crossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<Integer> potentialCentromeres = findPotentialCentromeres(parentA, parentB);

		if (potentialCentromeres == null || potentialCentromeres.isEmpty()) {
			log.info("Unable to find any potential centromeres for the chosen Chromosomes.  Returning empty List.");

			return new ArrayList<KeylessChromosome>();
		}

		/*
		 * Casting to int will truncate the number, giving us an index we can
		 * safely use against lists.
		 */
		int centromere = potentialCentromeres.get(randomListElementSelector
				.selectRandomListElement(potentialCentromeres));

		List<KeylessChromosome> children = new ArrayList<KeylessChromosome>();

		KeylessChromosome firstChild = performCrossover(parentA, parentB, centromere);
		// The chromosome will be null if it's identical to one of its parents
		if (firstChild != null) {
			children.add(firstChild);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	protected KeylessChromosome performCrossover(KeylessChromosome parentA, KeylessChromosome parentB, int centromere) {
		KeylessChromosome child = (KeylessChromosome) parentA.clone();

		int childBeginGeneIndex;
		int parentBeginGeneIndex;

		try {
			childBeginGeneIndex = findGeneBeginningAtCentromere(child, centromere);
			parentBeginGeneIndex = findGeneBeginningAtCentromere(parentB, centromere);
		} catch (IllegalStateException ise) {
			log.error(ise.getMessage());

			return null;
		}

		/*
		 * Remove Genes from cloned child.
		 */
		int originalSize = child.getGenes().size();
		for (int i = childBeginGeneIndex; i < originalSize; i++) {
			child.removeGene(childBeginGeneIndex);
		}

		/*
		 * Insert cloned parent Genes into child. insertCount works as an offset
		 * so that the Genes are inserted in the correct order.
		 */
		for (int j = parentBeginGeneIndex; j < parentB.getGenes().size(); j++) {
			child.addGene(parentB.getGenes().get(j).clone());
		}

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		// Don't return this child if it's identical to one of its parents
		if (child.equals(parentA) || child.equals(parentB)) {
			return null;
		}

		return child;
	}

	/**
	 * Finds the index of the Gene beginning at the Sequence specified by
	 * centromere.
	 * 
	 * @param chromosome
	 *            the Chromosome to check
	 * @param centromere
	 *            the centromere to find
	 * @return the Gene index where the centromere begins
	 */
	protected static int findGeneBeginningAtCentromere(KeylessChromosome chromosome, int centromere) {
		int geneIndex = 0;
		int nextSequenceIndex = 0;

		do {
			/*
			 * We want to advance the geneIndex before checking the sequenceId,
			 * since a centromere cannot exist at the first Gene (at index zero)
			 * anyway.
			 */
			geneIndex++;

			if (geneIndex >= chromosome.getGenes().size()) {
				throw new IllegalStateException("Attempted to find Gene beginning at centromere "
						+ centromere
						+ " but no Gene was found.  This is indicative of a bad centromere.");
			}

			nextSequenceIndex = ((VariableLengthGene) chromosome.getGenes().get(geneIndex)).getSequences().get(0)
					.getSequenceId();
		} while (nextSequenceIndex != centromere);

		return geneIndex;
	}

	/*
	 * Build a list of potential centromeres to use later for actual crossover.
	 * This should be all the Sequences which fall at the end of a Gene, so that
	 * the crossover will not happen in the middle of a Gene.
	 * 
	 * Make sure we don't exceed parentB's index, or else we will get an
	 * IndexOutOfBoundsException
	 */
	protected static List<Integer> findPotentialCentromeres(KeylessChromosome mom, KeylessChromosome dad) {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		List<Integer> potentialCentromeres = new ArrayList<Integer>();

		while (crossoverProgressDto.getFirstChromosomeGeneIndex() < mom.getGenes().size()
				&& crossoverProgressDto.getSecondChromosomeGeneIndex() < dad.getGenes().size()) {
			/*
			 * Advance the indexes depending on which Gene's sequence is
			 * greater. We advance them before checking whether it is a
			 * potential centromere because a centromere cannot exist at the
			 * first Gene (at index zero) anyway.
			 */
			ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, mom, dad);

			int momGeneEndSequence = crossoverProgressDto.getFirstChromosomeSequencePosition();

			if ((momGeneEndSequence == crossoverProgressDto.getSecondChromosomeSequencePosition())
					&& (momGeneEndSequence < mom.targetSize() - 1)) {
				/*
				 * Add to the potentialCentromeres list if the end of each Gene
				 * has the same Sequence index AND this is not the last Sequence
				 * of the Chromosome.
				 */
				potentialCentromeres.add(momGeneEndSequence);
			}
		}

		return potentialCentromeres;
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
	 * @param randomListElementSelector
	 *            the randomListElementSelector to set
	 */
	public void setRandomListElementSelector(RandomListElementSelector randomListElementSelector) {
		this.randomListElementSelector = randomListElementSelector;
	}
}
