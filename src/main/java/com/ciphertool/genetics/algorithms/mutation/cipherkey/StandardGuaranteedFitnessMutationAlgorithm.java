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

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class StandardGuaranteedFitnessMutationAlgorithm implements UniformMutationAlgorithm<KeyedChromosome<Object>> {
	private static final int MAX_ATTEMPTS = 100;

	private Double mutationRate;

	private GeneDao geneDao;

	private FitnessEvaluator fitnessEvaluator;

	@Override
	public void mutateChromosome(KeyedChromosome<Object> chromosome) {
		if (mutationRate == null) {
			throw new IllegalStateException("The mutationRate cannot be null.");
		}

		Set<Object> keys = chromosome.getGenes().keySet();

		for (Object key : keys) {
			if (ThreadLocalRandom.current().nextDouble() <= mutationRate) {
				int attempts = 0;

				Gene originalGene = chromosome.getGenes().get(key);
				double originalFitness = chromosome.getFitness();

				do {
					attempts++;

					// Replace that map value with a randomly generated Gene
					chromosome.replaceGene(key, geneDao.findRandomGene(chromosome));

					if (attempts >= MAX_ATTEMPTS) {
						// Revert the mutation
						chromosome.replaceGene(key, originalGene);

						break;
					}

					// Test if the replacement is better, otherwise continue looping
				} while (fitnessEvaluator.evaluate(chromosome) < originalFitness);
			}
		}
	}

	@Override
	public void setMutationRate(Double mutationRate) {
		this.mutationRate = mutationRate;
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
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	@Override
	public String getDisplayName() {
		return "Standard Guaranteed Fitness";
	}
}
