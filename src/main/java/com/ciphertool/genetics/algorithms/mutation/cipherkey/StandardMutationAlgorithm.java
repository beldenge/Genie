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
import com.ciphertool.genetics.entities.KeyedChromosome;

public class StandardMutationAlgorithm implements UniformMutationAlgorithm<KeyedChromosome<Object>> {
	private Double mutationRate;

	private GeneDao geneDao;

	@Override
	public void mutateChromosome(KeyedChromosome<Object> chromosome) {
		if (mutationRate == null) {
			throw new IllegalStateException("The mutationRate cannot be null.");
		}

		Set<Object> keys = chromosome.getGenes().keySet();

		for (Object key : keys) {
			if (ThreadLocalRandom.current().nextDouble() <= mutationRate) {
				// Replace that map value with a randomly generated Gene
				chromosome.replaceGene(key, geneDao.findRandomGene(chromosome));
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

	@Override
	public String getDisplayName() {
		return "Standard";
	}
}
