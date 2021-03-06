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

package com.ciphertool.genetics.algorithms.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.entities.Ancestry;
import com.ciphertool.genetics.entities.KeyedChromosome;

public class RandomSinglePointCrossoverAlgorithm implements CrossoverAlgorithm<KeyedChromosome<Object>> {
	private int maxGenerations;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>(1);

		KeyedChromosome<Object> child = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		if (child != null) {
			children.add(child);

			if (maxGenerations > 0) {
				child.setAncestry(new Ancestry(parentA.getId(), parentB.getId(), parentA.getAncestry(),
						parentB.getAncestry(), maxGenerations));
			}

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

		return child;
	}

	@Override
	public String getDisplayName() {
		return "Random Single Point";
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	@Required
	public void setMaxGenerations(int maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	@Override
	public int numberOfOffspring() {
		return 1;
	}
}
