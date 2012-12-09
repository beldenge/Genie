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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public class ConservativeMutationAlgorithm implements MutationAlgorithm {
	private static Logger log = Logger.getLogger(ConservativeMutationAlgorithm.class);
	private GeneListDao geneListDao;

	@Override
	public void mutateChromosome(Chromosome chromosome) {
		mutateRandomGene(chromosome);
	}

	/**
	 * Performs a genetic mutation of a random Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 */
	private void mutateRandomGene(Chromosome chromosome) {
		int randomIndex = (int) (Math.random() * chromosome.getGenes().size());

		mutateGene(chromosome, randomIndex);
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

		/*
		 * Loop just in case the value of the new Gene is the same as the
		 * existing value, since that would defeat the purpose of the mutation.
		 */
		Gene oldGene = chromosome.getGenes().get(index);
		Gene newGene = null;
		do {
			newGene = geneListDao.findRandomGeneOfLength(chromosome, oldGene.getSequences().get(0)
					.getSequenceId(), oldGene.size());
		} while (chromosome.getGenes().get(index).equals(newGene));

		chromosome.replaceGene(index, newGene);
	}

	/**
	 * @param geneListDao
	 *            the geneListDao to set
	 */
	@Required
	public void setGeneListDao(GeneListDao geneListDao) {
		this.geneListDao = geneListDao;
	}
}