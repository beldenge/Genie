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

package com.ciphertool.genetics.algorithms.mutation.cipherkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.EvaluatedMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class MultipleGuaranteedFitnessMutationAlgorithm implements UniformMutationAlgorithm<KeyedChromosome<Object>>,
		EvaluatedMutationAlgorithm<KeyedChromosome<Object>> {
	private int maxAttempts = 100;
	private int maxMutations = 10;

	private GeneDao geneDao;

	private FitnessEvaluator fitnessEvaluator;

	@Override
	public void mutateChromosome(KeyedChromosome<Object> chromosome) {
		/*
		 * Choose a random number of mutations constrained by the configurable max and the total number of genes
		 */
		int numMutations = (int) (ThreadLocalRandom.current().nextDouble() * Math.min(maxMutations, chromosome
				.getGenes().size())) + 1;

		double originalFitness = chromosome.getFitness();

		List<Object> availableKeys = new ArrayList<Object>(chromosome.getGenes().keySet());
		Map<Object, Gene> originalGenes = new HashMap<Object, Gene>();
		for (int i = 0; i < numMutations; i++) {
			/*
			 * We don't want to reuse an index, so we get one from the List of indices which are still available
			 */
			int randomIndex = (int) (ThreadLocalRandom.current().nextDouble() * availableKeys.size());
			Object randomKey = availableKeys.get(randomIndex);
			originalGenes.put(randomKey, chromosome.getGenes().get(randomKey));
			availableKeys.remove(randomIndex);
		}

		int attempts = 0;
		do {
			attempts++;

			for (Object key : originalGenes.keySet()) {
				// Replace that map value with a randomly generated Gene
				chromosome.replaceGene(key, geneDao.findRandomGene(chromosome));
			}

			if (attempts >= maxAttempts) {
				// revert the mutations
				for (Object key : originalGenes.keySet()) {
					chromosome.replaceGene(key, originalGenes.get(key));
				}

				break;
			}
		} while (fitnessEvaluator.evaluate(chromosome) < originalFitness);
	}

	@Override
	public void setMutationRate(Double mutationRate) {
		// Not used
	}

	/**
	 * @param geneDao
	 *            the geneDao to set
	 */
	@Required
	public void setGeneDao(GeneDao geneDao) {
		this.geneDao = geneDao;
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

	/**
	 * @param maxAttempts
	 *            the maxAttempts to set
	 */
	@Required
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/**
	 * @param maxMutations
	 *            the maxMutations to set
	 */
	@Required
	public void setMaxMutations(int maxMutations) {
		this.maxMutations = maxMutations;
	}

	@Override
	public String getDisplayName() {
		return "Multiple Guaranteed Fitness";
	}
}
