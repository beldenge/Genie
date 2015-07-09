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

package com.ciphertool.genetics.algorithms.mutation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.util.KeylessChromosomeHelper;

public class LiberalMutationAlgorithm implements NonUniformMutationAlgorithm<KeylessChromosome> {
	private static Logger log = Logger.getLogger(LiberalMutationAlgorithm.class);
	private GeneDao geneDao;
	private KeylessChromosomeHelper keylessChromosomeHelper;
	private Integer maxMutationsPerChromosome;

	private static final int MAX_FIND_ATTEMPTS = 1000;

	@Override
	public void mutateChromosome(KeylessChromosome chromosome) {
		if (maxMutationsPerChromosome == null) {
			throw new IllegalStateException("The maxMutationsPerChromosome cannot be null.");
		}

		/*
		 * Choose a random number of mutations constrained by the configurable max and the total number of genes
		 */
		int numMutations = (int) (Math.random() * Math.min(maxMutationsPerChromosome, chromosome.getGenes().size())) + 1;

		List<Integer> availableIndices = new ArrayList<Integer>();
		for (int i = 0; i < chromosome.getGenes().size(); i++) {
			availableIndices.add(i);
		}

		for (int i = 0; i < numMutations; i++) {
			// Keep track of the mutated indices
			mutateRandomGene(chromosome, availableIndices);
		}
	}

	/**
	 * Performs a genetic mutation of a random Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param availableIndices
	 *            the List of available indices to mutate
	 */
	protected void mutateRandomGene(KeylessChromosome chromosome, List<Integer> availableIndices) {
		if (availableIndices == null || availableIndices.isEmpty()) {
			log.warn("List of available indices is null or empty.  Unable to find a Gene to mutate.  Returning null.");

			return;
		}

		/*
		 * We don't want to reuse an index, so we get one from the List of indices which are still available
		 */
		int randomIndex = availableIndices.get((int) (Math.random() * availableIndices.size()));

		mutateGene(chromosome, randomIndex);

		int numGenesOriginally = chromosome.getGenes().size();

		keylessChromosomeHelper.resizeChromosome(chromosome);

		int numGenesAfterResize = chromosome.getGenes().size();
		int difference = numGenesOriginally - numGenesAfterResize;

		availableIndices.remove(availableIndices.indexOf(randomIndex));

		/*
		 * If there are less Genes now due to the resizing, then remove those from the end of the availableIndices List
		 * as well.
		 */
		for (int i = 0; i < difference; i++) {
			availableIndices.remove(availableIndices.size() - 1);
		}
	}

	/**
	 * Performs a genetic mutation of a specific Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param index
	 *            the index of the Gene to mutate
	 */
	protected void mutateGene(KeylessChromosome chromosome, int index) {
		if (index > chromosome.getGenes().size() - 1) {
			log.info("Attempted to mutate a Gene in Chromosome with index of " + index
					+ " (zero-indexed), but the size is only " + chromosome.getGenes().size() + ".  Cannot continue.");

			return;
		}

		/*
		 * Loop just in case the value of the new Gene is the same as the existing value, since that would defeat the
		 * purpose of the mutation.
		 */
		Gene oldGene = chromosome.getGenes().get(index);
		Gene newGene = null;
		int attempts = 0;
		do {
			newGene = geneDao.findRandomGene(chromosome);

			attempts++;

			if (attempts >= MAX_FIND_ATTEMPTS) {
				if (log.isDebugEnabled()) {
					log.debug("Unable to find a different value for Gene " + oldGene + " after " + attempts
							+ " attempts.  Breaking out of the loop.");
				}

				return;
			}
		} while (newGene == null || oldGene.equals(newGene));

		chromosome.replaceGene(index, newGene);
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
	 * @param keylessChromosomeHelper
	 *            the keylessChromosomeHelper to set
	 */
	@Required
	public void setChromosomeHelper(KeylessChromosomeHelper keylessChromosomeHelper) {
		this.keylessChromosomeHelper = keylessChromosomeHelper;
	}

	@Override
	public void setMaxMutationsPerChromosome(Integer maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}
}
