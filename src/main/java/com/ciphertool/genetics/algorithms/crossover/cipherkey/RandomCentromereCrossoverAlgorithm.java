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

package com.ciphertool.genetics.algorithms.crossover.cipherkey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.KeyedChromosome;

public class RandomCentromereCrossoverAlgorithm implements CrossoverAlgorithm<KeyedChromosome<Object>> {
	private MutationAlgorithm<KeyedChromosome<Object>> mutationAlgorithm;
	private boolean mutateDuringCrossover = false;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>();

		KeyedChromosome<Object> child = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		if (child != null) {
			children.add(child);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	@SuppressWarnings("unchecked")
	protected KeyedChromosome<Object> performCrossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		Random generator = new Random();
		Set<Object> availableKeys = parentA.getGenes().keySet();
		Object[] keys = availableKeys.toArray();

		// Get a random map key
		int randomIndex = generator.nextInt(keys.length);

		// Replace all the Genes from the map key to the end of the array
		KeyedChromosome<Object> child = (KeyedChromosome<Object>) parentA.clone();
		for (int i = 0; i <= randomIndex; i++) {
			Object nextKey = (Object) keys[i];

			if (null == parentB.getGenes().get(nextKey)) {
				throw new IllegalStateException("Expected second parent to have a Gene with key " + nextKey
						+ ", but no such key was found.  Cannot continue.");
			}

			child.replaceGene(nextKey, parentB.getGenes().get(nextKey).clone());
		}

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		return child;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm<KeyedChromosome<Object>> mutationAlgorithm) {
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
}
