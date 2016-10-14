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

package com.ciphertool.genetics.algorithms.mutation.cipherkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.EvaluatedMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationHelper;
import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class MultipleGuaranteedFitnessMutationAlgorithm implements UniformMutationAlgorithm<KeyedChromosome<Object>>,
		EvaluatedMutationAlgorithm<KeyedChromosome<Object>> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private int maxAttempts;

	private GeneDao geneDao;

	private MutationHelper mutationHelper;

	private FitnessEvaluator fitnessEvaluator;

	@Override
	public void mutateChromosome(KeyedChromosome<Object> chromosome) {
		double originalFitness = chromosome.getFitness();
		double mutationFitness = 0.0;

		List<Object> availableKeys;
		Map<Object, Gene> originalGenes;
		int numMutations;
		int attempts = 0;
		for (; attempts < maxAttempts; attempts++) {
			/*
			 * Choose a random number of mutations constrained by the configurable max and the total number of genes
			 */
			numMutations = mutationHelper.getNumMutations(chromosome.getGenes().size());

			availableKeys = new ArrayList<Object>(chromosome.getGenes().keySet());
			originalGenes = new HashMap<Object, Gene>();
			for (int i = 0; i < numMutations; i++) {
				/*
				 * We don't want to reuse an index, so we get one from the List of indices which are still available
				 */
				int randomIndex = (int) (ThreadLocalRandom.current().nextDouble() * availableKeys.size());
				Object randomKey = availableKeys.get(randomIndex);
				originalGenes.put(randomKey, chromosome.getGenes().get(randomKey));
				availableKeys.remove(randomIndex);
			}

			for (Object key : originalGenes.keySet()) {
				// Replace that map value with a randomly generated Gene
				chromosome.replaceGene(key, geneDao.findRandomGene(chromosome));
			}

			mutationFitness = fitnessEvaluator.evaluate(chromosome);
			if (mutationFitness <= originalFitness) {
				// revert the mutations
				for (Object key : originalGenes.keySet()) {
					chromosome.replaceGene(key, originalGenes.get(key));
				}
			} else {
				chromosome.setFitness(mutationFitness);

				return;
			}
		}

		log.debug("Unable to find guaranteed better fitness via mutation after " + attempts
				+ " attempts.  Returning clone of parent.");
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
	 * @param mutationHelper
	 *            the mutationHelper to set
	 */
	@Required
	public void setMutationHelper(MutationHelper mutationHelper) {
		this.mutationHelper = mutationHelper;
	}

	@Override
	public String getDisplayName() {
		return "Multiple Guaranteed Fitness";
	}
}
