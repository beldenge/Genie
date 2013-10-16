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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.util.fitness.FitnessEvaluator;

public class ConservativeCentromereCrossoverAlgorithm implements CrossoverAlgorithm {
	Logger log = Logger.getLogger(getClass());
	private MutationAlgorithm mutationAlgorithm;
	private boolean mutateDuringCrossover;

	/**
	 * This crossover algorithm finds all the points where both parent
	 * Chromosomes can safely be split in half without splitting a Gene, and
	 * then picks one of those at random as the centromere for crossover.
	 * 
	 * @see com.ciphertool.genetics.algorithms.crossover.zodiacengine.genetic.CrossoverAlgorithm#crossover(com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome,
	 *      com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome)
	 */
	@Override
	public List<Chromosome> crossover(Chromosome parentA, Chromosome parentB) {
		List<Integer> potentialCentromeres = findPotentialCentromeres(parentA, parentB);

		/*
		 * Casting to int will truncate the number, giving us an index we can
		 * safely use against lists.
		 */
		int centromere = potentialCentromeres.get((int) (Math.random() * potentialCentromeres
				.size()));

		List<Chromosome> children = new ArrayList<Chromosome>();

		Chromosome firstChild = performCrossover(parentA, parentB, centromere);
		// The chromosome will be null if it's identical to one of its parents
		if (firstChild != null) {
			children.add(firstChild);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	private Chromosome performCrossover(Chromosome parentA, Chromosome parentB, int centromere) {
		Chromosome child = (Chromosome) parentA.clone();

		int childBeginGeneIndex = findGeneBeginningAtCentromere(child, centromere);
		int parentBeginGeneIndex = findGeneBeginningAtCentromere(parentB, centromere);

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
	private static int findGeneBeginningAtCentromere(Chromosome chromosome, int centromere) {
		int geneIndex = 0;
		int nextSequenceIndex = 0;

		do {
			/*
			 * We want to advance the geneIndex before checking the sequenceId,
			 * since a centromere cannot exist at the first Gene (at index zero)
			 * anyway.
			 */
			geneIndex++;

			nextSequenceIndex = chromosome.getGenes().get(geneIndex).getSequences().get(0)
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
	private static List<Integer> findPotentialCentromeres(Chromosome mom, Chromosome dad) {
		int momGeneIndex = 0;
		int dadGeneIndex = 0;
		int momGeneEndSequence = 0;
		int dadGeneEndSequence = 0;
		Gene currentMomGene = null;
		Gene currentDadGene = null;

		List<Integer> potentialCentromeres = new ArrayList<Integer>();

		while (momGeneIndex < mom.getGenes().size() && dadGeneIndex < dad.getGenes().size()) {
			currentMomGene = mom.getGenes().get(momGeneIndex);
			currentDadGene = dad.getGenes().get(dadGeneIndex);

			/*
			 * Advance the indexes depending on which Gene's sequence is
			 * greater. We advance them before checking whether it is a
			 * potential centromere because a centromere cannot exist at the
			 * first Gene (at index zero) anyway.
			 */
			if (momGeneEndSequence == dadGeneEndSequence) {
				momGeneEndSequence += currentMomGene.size();
				dadGeneEndSequence += currentDadGene.size();

				momGeneIndex++;
				dadGeneIndex++;
			} else if (momGeneEndSequence > dadGeneEndSequence) {
				dadGeneEndSequence += currentDadGene.size();
				dadGeneIndex++;
			} else {
				momGeneEndSequence += currentMomGene.size();
				momGeneIndex++;
			}

			if ((momGeneEndSequence == dadGeneEndSequence)
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
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Override
	@Required
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
