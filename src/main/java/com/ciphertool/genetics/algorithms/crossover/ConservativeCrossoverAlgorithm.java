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

public class ConservativeCrossoverAlgorithm implements CrossoverAlgorithm {
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

		Chromosome child = performCrossover(parentA, parentB);

		// The Chromosome will be null if it's identical to one of its parents
		if (child != null) {
			children.add(child);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	/**
	 * This crossover algorithm does a conservative amount of changes since it
	 * only replaces genes that begin and end at the exact same sequence
	 * positions.
	 */
	protected Chromosome performCrossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = (Chromosome) parentA.clone();

		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (crossoverProgressDto.getChildGeneIndex() < child.getGenes().size()
				&& crossoverProgressDto.getParentGeneIndex() < parentB.getGenes().size()) {
			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (crossoverProgressDto.getChildSequencePosition() == crossoverProgressDto
					.getParentSequencePosition()
					&& child.getGenes().get(crossoverProgressDto.getChildGeneIndex()).size() == parentB
							.getGenes().get(crossoverProgressDto.getParentGeneIndex()).size()) {
				attemptToReplaceGeneInChild(crossoverProgressDto, child, parentB);
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

	protected void attemptToReplaceGeneInChild(CrossoverProgressDto crossoverProgressDto,
			Chromosome child, Chromosome parentB) {
		Gene geneCopy = child.getGenes().get(crossoverProgressDto.getChildGeneIndex()).clone();

		double originalFitness = child.getFitness();

		child.replaceGene(crossoverProgressDto.getChildGeneIndex(), parentB.getGenes().get(
				crossoverProgressDto.getParentGeneIndex()).clone());

		double newFitness = fitnessEvaluator.evaluate(child);
		child.setFitness(newFitness);

		/*
		 * Revert to the original gene if this decreased fitness. It's ok to let
		 * non-beneficial changes progress, as long as they are not detrimental.
		 */
		if (newFitness < originalFitness) {
			child.replaceGene(crossoverProgressDto.getChildGeneIndex(), geneCopy);

			/*
			 * Reset the fitness to what it was before the replacement.
			 */
			child.setFitness(originalFitness);
		}
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
