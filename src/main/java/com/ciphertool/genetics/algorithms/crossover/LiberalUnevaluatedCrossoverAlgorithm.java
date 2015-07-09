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

package com.ciphertool.genetics.algorithms.crossover;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.util.Coin;
import com.ciphertool.genetics.util.KeylessChromosomeHelper;

public class LiberalUnevaluatedCrossoverAlgorithm implements CrossoverAlgorithm<KeylessChromosome> {
	private GeneDao geneDao;
	private KeylessChromosomeHelper keylessChromosomeHelper;
	private MutationAlgorithm<KeylessChromosome> mutationAlgorithm;
	private boolean mutateDuringCrossover = false;
	private Coin coin;

	@Override
	public List<KeylessChromosome> crossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<KeylessChromosome> children = new ArrayList<KeylessChromosome>();

		KeylessChromosome firstChild = performCrossover(parentA, parentB);

		// The chromosome will be null if it's identical to one of its parents
		if (firstChild != null) {
			children.add(firstChild);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	/**
	 * This crossover algorithm does a liberal amount of changes since it replaces genes regardless of their begin and
	 * end sequence positions
	 */
	protected KeylessChromosome performCrossover(KeylessChromosome parentA, KeylessChromosome parentB) {
		KeylessChromosome child = (KeylessChromosome) parentA.clone();

		int childGeneIndex = 0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an IndexOutOfBoundsException
		 */
		while (childGeneIndex < child.getGenes().size() && childGeneIndex < parentB.getGenes().size()) {
			attemptToReplaceGeneInChild(childGeneIndex, child, parentB);

			childGeneIndex++;
		}

		/*
		 * Trim the Chromosome in case it ends with too many sequences due to the nature of this algorithm
		 */
		keylessChromosomeHelper.resizeChromosome(child);

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		// Don't return this child if it's identical to one of its parents
		if (child.equals(parentA) || child.equals(parentB)) {
			return null;
		}

		/*
		 * Child is guaranteed to have at least as good fitness as its parent
		 */
		return child;
	}

	protected void attemptToReplaceGeneInChild(int childGeneIndex, KeylessChromosome child, KeylessChromosome parent) {
		/*
		 * Flip a coin to see if the current Gene should be replaced
		 */
		if (coin.flip()) {
			child.replaceGene(childGeneIndex, parent.getGenes().get(childGeneIndex).clone());

			while (child.actualSize() < child.targetSize()) {
				child.addGene(geneDao.findRandomGene(child));
			}
		}
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

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm<KeylessChromosome> mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param mutateDuringCrossover
	 *            the mutateDuringCrossover to set
	 */
	@Override
	public void setMutateDuringCrossover(boolean mutateDuringCrossover) {
		this.mutateDuringCrossover = mutateDuringCrossover;
	}

	/**
	 * @param coin
	 *            the coin to set
	 */
	@Required
	public void setCoin(Coin coin) {
		this.coin = coin;
	}
}
