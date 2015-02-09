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

import com.ciphertool.genetics.dao.SequenceDao;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.Sequence;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class SingleSequenceMutationAlgorithm implements MutationAlgorithm<KeylessChromosome> {
	private static Logger log = Logger.getLogger(SingleSequenceMutationAlgorithm.class);
	private SequenceDao sequenceDao;
	private Integer maxMutationsPerChromosome;

	private static final int MAX_FIND_ATTEMPTS = 1000;

	@Override
	public void mutateChromosome(KeylessChromosome chromosome) {
		if (maxMutationsPerChromosome == null) {
			throw new IllegalStateException("The maxMutationsPerChromosome cannot be null.");
		}

		/*
		 * Choose a random number of mutations constrained by the configurable
		 * max and the total number of genes
		 */
		int numMutations = (int) (Math.random() * Math.min(maxMutationsPerChromosome, chromosome
				.getGenes().size())) + 1;

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
	 * @return the index mutated, or null if none was mutated
	 */
	protected void mutateRandomGene(KeylessChromosome chromosome, List<Integer> availableIndices) {
		if (availableIndices == null || availableIndices.isEmpty()) {
			log.warn("List of available indices is null or empty.  Unable to find a Gene to mutate.  Returning null.");

			return;
		}

		/*
		 * We don't want to reuse an index, so we get one from the List of
		 * indices which are still available
		 */
		int randomIndex = availableIndices.get((int) (Math.random() * availableIndices.size()));

		mutateGene(chromosome, randomIndex);

		availableIndices.remove(availableIndices.indexOf(randomIndex));
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
					+ " (zero-indexed), but the size is only " + chromosome.getGenes().size()
					+ ".  Cannot continue.");

			return;
		}

		mutateRandomSequence((VariableLengthGene) chromosome.getGenes().get(index));
	}

	/**
	 * Performs a genetic mutation of a random Sequence of the supplied Gene
	 * 
	 * @param gene
	 *            the Gene to mutate
	 */
	protected void mutateRandomSequence(VariableLengthGene gene) {
		int randomIndex = (int) (Math.random() * gene.size());

		mutateSequence(gene, randomIndex);
	}

	/**
	 * Performs a genetic mutation of a specific Sequence of the supplied Gene
	 * 
	 * @param gene
	 *            the Gene to mutate
	 * @param index
	 *            the index of the Sequence to mutate
	 */
	protected void mutateSequence(VariableLengthGene gene, int index) {
		if (index > gene.size() - 1) {
			log.info("Attempted to mutate a sequence in Gene with index of " + index
					+ " (zero-indexed), but the size is only " + gene.size()
					+ ".  Cannot continue.");

			return;
		}

		Sequence oldSequence = gene.getSequences().get(index);
		Sequence newSequence = null;

		int ciphertextIndex = oldSequence.getSequenceId();
		int attempts = 0;

		/*
		 * Loop just in case the value of the new Sequence is the same as the
		 * existing value, since that would defeat the purpose of the mutation.
		 */
		do {
			newSequence = sequenceDao.findRandomSequence(gene, ciphertextIndex);

			attempts++;

			if (attempts >= MAX_FIND_ATTEMPTS) {
				if (log.isDebugEnabled()) {
					log.debug("Unable to find a different value for Sequence " + oldSequence
							+ " after " + attempts + " attempts.  Breaking out of the loop.");
				}

				return;
			}
		} while (newSequence == null || oldSequence.getValue().equals(newSequence.getValue()));

		gene.replaceSequence(index, newSequence);
	}

	/**
	 * @param sequenceDao
	 *            the sequenceDao to set
	 */
	@Required
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	@Override
	public void setMaxMutationsPerChromosome(Integer maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}
}
