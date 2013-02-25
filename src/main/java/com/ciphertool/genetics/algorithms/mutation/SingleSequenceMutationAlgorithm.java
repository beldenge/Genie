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

package com.ciphertool.genetics.algorithms.mutation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.SequenceDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.Sequence;

public class SingleSequenceMutationAlgorithm implements MutationAlgorithm {
	private static Logger log = Logger.getLogger(SingleSequenceMutationAlgorithm.class);
	private SequenceDao sequenceDao;
	private int maxMutationsPerChromosome;

	@Override
	public void mutateChromosome(Chromosome chromosome) {
		/*
		 * Choose a random number of mutations constrained by the configurable
		 * max and the total number of genes
		 */
		int numMutations = (int) (Math.random() * Math.min(maxMutationsPerChromosome, chromosome
				.getGenes().size())) + 1;

		List<Integer> geneIndices = new ArrayList<Integer>();
		for (int i = 0; i < numMutations; i++) {
			// Keep track of the mutated indices
			geneIndices.add(mutateRandomGene(chromosome, geneIndices));
		}
	}

	/**
	 * Performs a genetic mutation of a random Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 */
	private int mutateRandomGene(Chromosome chromosome, List<Integer> geneIndices) {
		int randomIndex;

		// We don't want to reuse an index, so loop until we find a new one
		do {
			randomIndex = (int) (Math.random() * chromosome.getGenes().size());
		} while (geneIndices.contains(randomIndex));

		mutateGene(chromosome, randomIndex);

		return randomIndex;
	}

	/**
	 * Performs a genetic mutation of a specific Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param index
	 *            the index of the Gene to mutate
	 */
	private void mutateGene(Chromosome chromosome, int index) {
		if (index > chromosome.getGenes().size() - 1) {
			log.info("Attempted to mutate a Gene in Chromosome with index of " + index
					+ " (zero-indexed), but the size is only " + chromosome.getGenes().size()
					+ ".  Cannot continue.");

			return;
		}

		mutateRandomSequence(chromosome.getGenes().get(index));
	}

	/**
	 * Performs a genetic mutation of a random Sequence of the supplied Gene
	 * 
	 * @param gene
	 *            the Gene to mutate
	 */
	private void mutateRandomSequence(Gene gene) {
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
	private void mutateSequence(Gene gene, int index) {
		if (index > gene.size() - 1) {
			log.info("Attempted to mutate a sequence in Gene with index of " + index
					+ " (zero-indexed), but the size is only " + gene.size()
					+ ".  Cannot continue.");

			return;
		}

		/*
		 * Loop just in case the value of the new Sequence is the same as the
		 * existing value, since that would defeat the purpose of the mutation.
		 */
		Sequence oldSequence = gene.getSequences().get(index);
		Sequence newSequence = null;

		int ciphertextIndex = oldSequence.getSequenceId();
		do {
			newSequence = sequenceDao.findRandomSequence(gene, ciphertextIndex);
		} while (oldSequence.getValue().equals(newSequence.getValue()));

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

	/**
	 * @param maxMutationsPerChromosome
	 *            the maxMutationsPerChromosome to set
	 */
	@Required
	public void setMaxMutationsPerChromosome(int maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}
}
