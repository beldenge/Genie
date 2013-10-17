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

package com.ciphertool.genetics.algorithms.mutation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public class GroupMutationAlgorithm implements MutationAlgorithm {
	private static Logger log = Logger.getLogger(GroupMutationAlgorithm.class);

	private static final int MAX_GENES_PER_GROUP = 5;

	/*
	 * This is set to avoid infinite loops in case we cannot find another
	 * contiguous group to mutate
	 */
	private static final int MAX_ATTEMPTS_PER_MUTATION = 10;

	private GeneListDao geneListDao;
	private Integer maxMutationsPerChromosome;

	@Override
	public void mutateChromosome(Chromosome chromosome) {
		if (maxMutationsPerChromosome == null) {
			throw new IllegalStateException("The maxMutationsPerChromosome cannot be null.");
		}

		/*
		 * Choose a random number of mutations constrained by the configurable
		 * max and the total number of genes
		 */
		int numMutations = (int) (Math.random() * Math.min(maxMutationsPerChromosome, chromosome
				.getGenes().size())) + 1;

		Map<Integer, Integer> geneIndices = new HashMap<Integer, Integer>();
		for (int i = 0; i < numMutations; i++) {
			// Keep track of the mutated indices by passing the map by reference
			mutateRandomGeneGroup(chromosome, geneIndices);
		}
	}

	/**
	 * Performs a genetic mutation of a random Gene group of the supplied
	 * Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 */
	private void mutateRandomGeneGroup(Chromosome chromosome, Map<Integer, Integer> geneIndices) {
		/*
		 * Choose a random number of genes constrained by the static max and the
		 * total number of genes
		 */
		int numGenesToMutate = (int) (Math.random() * Math.min(MAX_GENES_PER_GROUP, chromosome
				.getGenes().size())) + 1;

		int randomIndex;

		// We don't want to reuse an index, so loop until we find a new one
		int attempts = 0;
		do {
			randomIndex = (int) (Math.random() * chromosome.getGenes().size());

			attempts++;

			if (attempts >= MAX_ATTEMPTS_PER_MUTATION) {
				if (log.isTraceEnabled()) {
					log.trace("Exceeded "
							+ MAX_ATTEMPTS_PER_MUTATION
							+ " attempts at finding a gene group to mutate.  Returning without performing mutation.");
				}

				return;
			}
		} while (exceedsChromosomeSize(chromosome, randomIndex + numGenesToMutate)
				|| overlapsPreviousMutation(geneIndices, randomIndex, randomIndex
						+ numGenesToMutate));

		mutateGeneGroup(chromosome, randomIndex, numGenesToMutate);

		geneIndices.put(randomIndex, randomIndex + numGenesToMutate - 1);
	}

	/**
	 * Checks whether the length of the Chromosome is being exceeded by a
	 * proposed end indice.
	 * 
	 * @param chromosome
	 *            the Chromosome
	 * @param proposedEndIndex
	 *            the proposed end index
	 * @return whether the proposed index exceeds the chromosome size
	 */
	private static boolean exceedsChromosomeSize(Chromosome chromosome, int proposedEndIndex) {
		/*
		 * proposedEndIndex is zero-indexed, so we need to subtract 1 from the
		 * gene list size.
		 */
		if (proposedEndIndex > chromosome.getGenes().size() - 1) {
			return true;
		}

		return false;
	}

	/**
	 * Checks whether the proposed indices overlap a group which has already
	 * been mutated so as not to re-mutate them.
	 * 
	 * @param geneIndices
	 *            the Map of indices for groups already mutated previously
	 * @param proposedBeginIndex
	 *            the beginning index of a previously mutated group
	 * @param proposedEndIndex
	 *            the number of genes to mutate
	 * @return whether the proposed indices overlap a group already mutated
	 *         previously
	 */
	private static boolean overlapsPreviousMutation(Map<Integer, Integer> geneIndices,
			Integer proposedBeginIndex, Integer proposedEndIndex) {
		Integer nextEndIndex = null;

		for (Integer nextBeginIndex : geneIndices.keySet()) {
			nextEndIndex = geneIndices.get(nextBeginIndex);

			// If the proposed begin index is anywhere within the previous group
			if (proposedBeginIndex >= nextBeginIndex && proposedBeginIndex <= nextEndIndex) {
				return true;
			}

			// If the proposed end index is anywhere within the previous group
			if (proposedEndIndex >= nextBeginIndex && proposedEndIndex <= nextEndIndex) {
				return true;
			}

			// If the proposed indices completely encompass the previous group
			if (proposedBeginIndex <= nextBeginIndex && proposedEndIndex >= nextEndIndex) {
				return true;
			}

			/*
			 * Covers the case where one of the proposed indices is valid, but
			 * the other exactly matches one from the previous group
			 */
			if (proposedBeginIndex == nextBeginIndex || proposedBeginIndex == nextEndIndex
					|| proposedEndIndex == nextBeginIndex || proposedEndIndex == nextEndIndex) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Performs a genetic mutation of a specific Gene group of the supplied
	 * Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param beginIndex
	 *            the starting index of the Gene group to mutate
	 */
	private void mutateGeneGroup(Chromosome chromosome, int beginIndex, int numGenes) {
		if ((beginIndex + numGenes) > chromosome.getGenes().size() - 1) {
			log.info("Attempted to mutate a Gene group in Chromosome with an end index of "
					+ (beginIndex + numGenes - 1) + " (zero-indexed), but the size is only "
					+ chromosome.getGenes().size() + ".  Cannot continue.");

			return;
		}

		int beginningSequenceIndex = chromosome.getGenes().get(beginIndex).getSequences().get(0)
				.getSequenceId();

		// Remove the old genes
		List<Gene> genesRemoved = removeGenes(chromosome, beginIndex, numGenes);

		int sequencesRemoved = 0;

		// Count the number of sequences from all the removed genes
		for (Gene removedGene : genesRemoved) {
			sequencesRemoved += removedGene.size();
		}

		// Insert new random genes
		boolean successfullyMutated = insertRandomGenes(chromosome, beginIndex,
				beginningSequenceIndex, sequencesRemoved);

		if (!successfullyMutated) {
			/*
			 * In certain edge cases, the geneListDao may be unable to return a
			 * Gene
			 */
			revertGenes(chromosome, genesRemoved, beginIndex);
		}
	}

	private static List<Gene> removeGenes(Chromosome chromosome, int beginIndex, int numGenes) {
		List<Gene> genesRemoved = new ArrayList<Gene>();

		for (int i = 0; i < numGenes; i++) {
			Gene removedGene = chromosome.getGenes().get(beginIndex).clone();

			chromosome.removeGene(beginIndex);

			genesRemoved.add(removedGene);
		}

		return genesRemoved;
	}

	private boolean insertRandomGenes(Chromosome chromosome, int beginGeneIndex,
			int beginningSequenceIndex, int sequencesRemoved) {
		int sequencesAdded = 0;

		List<Gene> genesToAdd = new ArrayList<Gene>();

		do {
			Gene geneToAdd = geneListDao.findRandomGene(chromosome, beginningSequenceIndex
					+ sequencesAdded);

			if (geneToAdd.size() > (sequencesRemoved - sequencesAdded)) {
				geneToAdd = geneListDao.findRandomGeneOfLength(chromosome, beginningSequenceIndex
						+ sequencesAdded, sequencesRemoved - sequencesAdded);
			}

			if (geneToAdd == null) {
				return false;
			}

			genesToAdd.add(geneToAdd);

			sequencesAdded += geneToAdd.size();
		} while (sequencesAdded < sequencesRemoved);

		for (Gene geneToAdd : genesToAdd) {
			/*
			 * We will keep adding them to the beginning of the group, which is
			 * fine because they are random genes anyway
			 */
			chromosome.insertGene(beginGeneIndex, geneToAdd);
		}

		return true;
	}

	/**
	 * Reverts a group mutation
	 * 
	 * @param chromosome
	 *            the chromosome to revert
	 * @param genesRemoved
	 *            the genes to re-add
	 * @param beginIndex
	 *            the index to start adding from
	 */
	private static void revertGenes(Chromosome chromosome, List<Gene> genesRemoved, int beginIndex) {
		/*
		 * Insert all of the removed genes back in their proper position.
		 */
		for (int i = 0; i < genesRemoved.size(); i++) {
			chromosome.insertGene(beginIndex + i, genesRemoved.get(i));
		}
	}

	/**
	 * @param geneListDao
	 *            the geneListDao to set
	 */
	@Required
	public void setGeneListDao(GeneListDao geneListDao) {
		this.geneListDao = geneListDao;
	}

	@Override
	public void setMaxMutationsPerChromosome(Integer maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}
}