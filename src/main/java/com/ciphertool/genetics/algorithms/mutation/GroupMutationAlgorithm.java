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

package com.ciphertool.genetics.algorithms.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.VariableLengthGeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class GroupMutationAlgorithm implements NonUniformMutationAlgorithm<KeylessChromosome> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private static int MAX_GENES_PER_GROUP = 5;

	private VariableLengthGeneDao geneDao;
	private Integer maxMutationsPerChromosome;

	@Override
	public void mutateChromosome(KeylessChromosome chromosome) {
		if (maxMutationsPerChromosome == null) {
			throw new IllegalStateException("The maxMutationsPerChromosome cannot be null.");
		}

		/*
		 * Choose a random number of mutations constrained by the configurable max and the total number of genes
		 */
		int numMutations = (int) (ThreadLocalRandom.current().nextDouble() * Math.min(maxMutationsPerChromosome,
				chromosome.getGenes().size())) + 1;

		List<Integer> availableIndices = new ArrayList<Integer>();
		for (int i = 0; i < chromosome.getGenes().size(); i++) {
			availableIndices.add(i);
		}

		int maxGenesToMutate;
		for (int i = 0; i < numMutations; i++) {
			/*
			 * Choose a random number of genes constrained by the static max and the total number of genes
			 */
			maxGenesToMutate = (int) (ThreadLocalRandom.current().nextDouble() * Math.min(MAX_GENES_PER_GROUP,
					chromosome.getGenes().size())) + 1;

			// Keep track of the mutated indices
			mutateRandomGeneGroup(chromosome, availableIndices, maxGenesToMutate);
		}
	}

	/**
	 * Performs a genetic mutation of a random Gene group of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param availableIndices
	 *            the List of available indices to mutate
	 */
	protected void mutateRandomGeneGroup(KeylessChromosome chromosome, List<Integer> availableIndices,
			int maxGenesToMutate) {

		if (availableIndices == null || availableIndices.isEmpty()) {
			log.warn("List of available indices is null or empty.  Unable to find a Gene to mutate.  Returning null.");

			return;
		}

		/*
		 * We don't want to reuse an index, so we get one from the List of indices which are still available
		 */
		int randomAvailableIndex = (int) (ThreadLocalRandom.current().nextDouble() * availableIndices.size());

		Integer beginIndex = availableIndices.get(randomAvailableIndex);

		// We start by counting the current Gene
		Integer numGenesToMutate = 1;

		/*
		 * If the maxGenesToMutate is more than one, then try to find some more by iterating forwards.
		 */
		if (numGenesToMutate < maxGenesToMutate) {
			int indicesAddedRight = addRightIndices(availableIndices, randomAvailableIndex, maxGenesToMutate
					- numGenesToMutate);
			numGenesToMutate += indicesAddedRight;
		}

		/*
		 * If we still have not been able to find enough indices to reach the maxGenesToMutate, then try to find more by
		 * iterating backwards.
		 */
		if (numGenesToMutate < maxGenesToMutate) {
			int indicesAddedLeft = addLeftIndices(availableIndices, randomAvailableIndex, maxGenesToMutate
					- numGenesToMutate);
			beginIndex -= indicesAddedLeft;
			numGenesToMutate += indicesAddedLeft;
		}

		int numGenesInserted = mutateGeneGroup(chromosome, beginIndex, numGenesToMutate);

		updateAvailableIndices(availableIndices, availableIndices.indexOf(beginIndex), numGenesToMutate,
				numGenesInserted);
	}

	/**
	 * Add indices to be mutated until we reach the maxGenesToMutate, find another gap, or we finally hit the end of the
	 * List.
	 * 
	 * @param availableIndices
	 *            the List of availableIndices to mutate
	 * @param randomAvailableIndex
	 *            the chosen index into the availableIndices List
	 * @param maxGenesToMutate
	 *            the maximum number of Genes to mutate
	 */
	protected static int addRightIndices(List<Integer> availableIndices, int randomAvailableIndex, int maxGenesToMutate) {

		int indicesAdded = 0;

		int indicesLeftToCheck = availableIndices.size() - randomAvailableIndex;
		for (int i = 0; i < indicesLeftToCheck; i++) {
			// Break out of the loop if we've reached the end of the List
			if (randomAvailableIndex + i + 1 >= availableIndices.size()) {
				break;
			}

			/*
			 * Break out of the loop if the next index comes after a gap, because we don't want to mutate Genes that
			 * have already been mutated as part of another group.
			 */
			if (availableIndices.get(randomAvailableIndex + i + 1) - 1 > availableIndices.get(randomAvailableIndex + i)) {
				break;
			}

			// Break out of the loop if we've added up to the maximum
			if (indicesAdded >= maxGenesToMutate) {
				break;
			}

			indicesAdded++;
		}

		return indicesAdded;
	}

	/**
	 * Add indices to be mutated until we find another gap, we hit the beginning of the List, or we finally find enough
	 * indices.
	 * 
	 * @param availableIndices
	 *            the List of availableIndices to mutate
	 * @param randomAvailableIndex
	 *            the chosen index into the availableIndices List
	 * @param maxGenesToMutate
	 *            the maximum number of Genes to mutate
	 */
	protected static int addLeftIndices(List<Integer> availableIndices, int randomAvailableIndex, int maxGenesToMutate) {

		int indicesAdded = 0;

		int indicesLeftToCheck = randomAvailableIndex + 1;
		for (int i = 0; i < indicesLeftToCheck; i++) {
			// Break out of the loop if we've reached the beginning of the List
			if (randomAvailableIndex - i <= 0) {
				break;
			}

			/*
			 * Break out of the loop if the next index comes after a gap, because we don't want to mutate Genes that
			 * have already been mutated as part of another group.
			 */
			if (availableIndices.get(randomAvailableIndex - i - 1) + 1 < availableIndices.get(randomAvailableIndex - i)) {
				break;
			}

			// Break out of the loop if we've added up to the maximum
			if (indicesAdded >= maxGenesToMutate) {
				break;
			}

			indicesAdded++;
		}

		return indicesAdded;
	}

	/**
	 * We need to update the List of available indices by removing the mutated indices and incrementing or decrementing
	 * the remaining indices based on the difference between the number of Genes inserted and removed.
	 * 
	 * @param availableIndices
	 *            the List of availableIndices to mutate
	 * @param beginIndex
	 *            the index to begin at
	 * @param numGenesRemoved
	 *            the number of Genes removed
	 * @param numGenesInserted
	 *            the number of Genes inserted
	 */
	protected static void updateAvailableIndices(List<Integer> availableIndices, int beginIndex, int numGenesRemoved,
			int numGenesInserted) {

		int differenceBetweenInsertedAndRemoved = numGenesInserted - numGenesRemoved;

		// Remove indices that have already been mutated
		for (int i = beginIndex; i < (beginIndex + numGenesRemoved); i++) {
			availableIndices.remove(beginIndex);
		}

		// Modify the remaining indices to the right of the mutated group
		int originalIndex;
		for (int i = beginIndex; i < availableIndices.size(); i++) {
			originalIndex = availableIndices.remove(i);
			availableIndices.add(i, originalIndex + differenceBetweenInsertedAndRemoved);
		}
	}

	/**
	 * Performs a genetic mutation of a specific Gene group of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param beginIndex
	 *            the starting index of the Gene group to mutate
	 * @param numGenes
	 *            the number of Genes to mutate
	 */
	protected int mutateGeneGroup(KeylessChromosome chromosome, int beginIndex, int numGenes) {
		if (numGenes <= 0) {
			// Nothing to do
			return 0;
		}

		if (beginIndex < 0 || beginIndex >= chromosome.getGenes().size()) {
			throw new IllegalArgumentException("Unable to mutate Gene group from Chromosome starting at index "
					+ beginIndex + ", as this index is out of bounds.  Expecting an index in the range [0-"
					+ (chromosome.getGenes().size() - 1) + "].");
		}

		if (numGenes > chromosome.getGenes().size() - beginIndex) {
			throw new IllegalArgumentException("Unable to mutate " + numGenes + " Genes at beginIndex " + beginIndex
					+ " because there are only " + (chromosome.getGenes().size() - beginIndex)
					+ " Genes available to mutate at this index.");
		}

		// Remove the old genes
		int sequencesToRemove = countSequencesToRemove(chromosome, beginIndex, numGenes);

		// Insert new random genes
		int numInsertedGenes = insertRandomGenes(chromosome, beginIndex, sequencesToRemove);

		if (numInsertedGenes != 0) {
			removeGenes(chromosome, beginIndex + numInsertedGenes, numGenes);
		}

		return numInsertedGenes;
	}

	/**
	 * Remove a number of Genes equal to numGenes and starting at beginIndex.
	 * 
	 * @param chromosome
	 *            the Chromosome to remove Genes from
	 * @param beginIndex
	 *            the index to start removing Genes at
	 * @param numGenes
	 *            the number of Genes to remove
	 * @return the List of Genes removed
	 */
	protected static List<Gene> removeGenes(KeylessChromosome chromosome, int beginIndex, int numGenes) {
		List<Gene> genesRemoved = new ArrayList<Gene>();

		if (numGenes <= 0) {
			// Nothing to do
			return genesRemoved;
		}

		if (beginIndex < 0 || beginIndex >= chromosome.getGenes().size()) {
			throw new IllegalArgumentException("Unable to remove Genes from Chromosome starting at index " + beginIndex
					+ ", as this index is out of bounds.  Expecting an index in the range [0-"
					+ (chromosome.getGenes().size() - 1) + "].");
		}

		if (numGenes > chromosome.getGenes().size() - beginIndex) {
			throw new IllegalArgumentException("Unable to remove " + numGenes + " Genes at beginIndex " + beginIndex
					+ " because there are only " + (chromosome.getGenes().size() - beginIndex)
					+ " Genes to remove at this index.");
		}

		for (int i = 0; i < numGenes; i++) {
			genesRemoved.add(chromosome.removeGene(beginIndex));
		}

		return genesRemoved;
	}

	/**
	 * Insert a sufficient number of random Genes to make up for the Sequences removed.
	 * 
	 * @param chromosome
	 *            to Chromosome to insert Genes into
	 * @param beginGeneIndex
	 *            the Gene index to start adding from
	 * @param sequencesRemoved
	 *            the number of Sequences removed
	 * @return whether the insertion was successful
	 */
	protected int insertRandomGenes(KeylessChromosome chromosome, int beginGeneIndex, int sequencesRemoved) {
		int sequencesAdded = 0;

		List<Gene> genesToAdd = new ArrayList<Gene>();

		while (sequencesAdded < sequencesRemoved) {
			Gene geneToAdd = geneDao.findRandomGene(chromosome);

			if (geneToAdd != null && ((VariableLengthGene) geneToAdd).size() > (sequencesRemoved - sequencesAdded)) {
				geneToAdd = geneDao.findRandomGeneOfLength(chromosome, sequencesRemoved - sequencesAdded);
			}

			if (geneToAdd == null) {
				return 0;
			}

			genesToAdd.add(geneToAdd);

			sequencesAdded += ((VariableLengthGene) geneToAdd).size();
		}

		for (int i = 0; i < genesToAdd.size(); i++) {
			/*
			 * It doesn't matter what order we add the Genes in, since they are random, but it should generally perform
			 * better if we add them towards the end. This way less Sequence indices need to be updated.
			 */
			chromosome.insertGene(beginGeneIndex + i, genesToAdd.get(i));
		}

		return genesToAdd.size();
	}

	protected static int countSequencesToRemove(KeylessChromosome chromosome, int beginIndex, int numGenes) {
		if (numGenes <= 0) {
			// Nothing to do
			return 0;
		}

		if (beginIndex < 0 || beginIndex >= chromosome.getGenes().size()) {
			throw new IllegalArgumentException("Unable to count Genes from Chromosome starting at index " + beginIndex
					+ ", as this index is out of bounds.  Expecting an index in the range [0-"
					+ (chromosome.getGenes().size() - 1) + "].");
		}

		if (numGenes > chromosome.getGenes().size() - beginIndex) {
			throw new IllegalArgumentException("Unable to count " + numGenes + " Genes at beginIndex " + beginIndex
					+ " because there are only " + (chromosome.getGenes().size() - beginIndex)
					+ " Genes to remove at this index.");
		}

		int sequenceCount = 0;

		for (int i = 0; i < numGenes; i++) {
			sequenceCount += ((VariableLengthGene) chromosome.getGenes().get(beginIndex + i)).size();
		}

		return sequenceCount;
	}

	/**
	 * @param geneDao
	 *            the geneDao to set
	 */
	@Required
	public void setGeneDao(VariableLengthGeneDao geneDao) {
		this.geneDao = geneDao;
	}

	@Override
	public void setMaxMutationsPerChromosome(Integer maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}

	@Override
	public String getDisplayName() {
		return "Group";
	}
}