/**
 * Copyright 2015 George Belden
 * 
 * This file is part of Genie.
 * 
 * Genie is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Genie is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Genie. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.crossover.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.crossover.EvaluatedCrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Ancestry;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.util.Coin;

public class EqualOpportunityGuaranteedSwapCrossoverAlgorithm implements
		EvaluatedCrossoverAlgorithm<KeyedChromosome<Object>> {
	private Logger										log						= LoggerFactory.getLogger(getClass());

	private int											maxAttempts;

	private FitnessEvaluator							fitnessEvaluator;
	private MutationAlgorithm<KeyedChromosome<Object>>	mutationAlgorithm;
	private boolean										mutateDuringCrossover	= false;
	private int											maxGenerations;

	private Coin										coin;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<KeyedChromosome<Object>> children = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		for (KeyedChromosome<Object> child : children) {
			child.setAncestry(new Ancestry(parentA.getId(), parentB.getId(), parentA.getAncestry(),
					parentB.getAncestry(), maxGenerations));
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	@SuppressWarnings("unchecked")
	protected List<KeyedChromosome<Object>> performCrossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		KeyedChromosome<Object> childA;
		KeyedChromosome<Object> childB;
		double originalFitnessA = parentA.getFitness();
		double originalFitnessB = parentB.getFitness();

		int attempts = 0;

		do {
			attempts++;
			childA = (KeyedChromosome<Object>) parentA.clone();
			childB = (KeyedChromosome<Object>) parentB.clone();

			for (Object key : parentA.getGenes().keySet()) {
				if (coin.flip()) {
					childA.replaceGene(key, parentB.getGenes().get(key).clone());
					childB.replaceGene(key, parentA.getGenes().get(key).clone());
				}
			}

			if (attempts >= maxAttempts) {
				// revert crossover
				childA = (KeyedChromosome<Object>) parentA.clone();
				childB = (KeyedChromosome<Object>) parentB.clone();

				log.debug("Unable to find guaranteed better fitness via crossover after " + maxAttempts
						+ " attempts.  Returning clones of parents.");

				break;
			}
		} while (fitnessEvaluator.evaluate(childA) < originalFitnessA
				&& fitnessEvaluator.evaluate(childB) < originalFitnessB);

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(childA);
			mutationAlgorithm.mutateChromosome(childB);
		}

		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>();
		children.add(childA);
		children.add(childB);

		return children;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm<KeyedChromosome<Object>> mutationAlgorithm) {
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

	@Override
	public String getDisplayName() {
		return "Equal Opportunity Guaranteed Swap";
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	@Required
	public void setMaxGenerations(int maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Required
	@Override
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	@Override
	public int numberOfOffspring() {
		return 2;
	}

	/**
	 * @param maxAttempts
	 *            the maxAttempts to set
	 */
	@Required
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
}
