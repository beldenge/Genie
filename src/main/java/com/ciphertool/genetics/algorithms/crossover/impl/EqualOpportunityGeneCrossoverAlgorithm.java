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
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.util.Coin;

public class EqualOpportunityGeneCrossoverAlgorithm implements CrossoverAlgorithm<KeyedChromosome<Object>> {
	private int		maxGenerations;

	private Coin	coin;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>(1);

		KeyedChromosome<Object> child = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		if (child != null) {
			children.add(child);
			// child.setAncestry(new Ancestry(parentA.getId(), parentB.getId(), parentA.getAncestry(),
			// parentB.getAncestry(), maxGenerations));
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	@SuppressWarnings("unchecked")
	protected KeyedChromosome<Object> performCrossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		KeyedChromosome<Object> child = (KeyedChromosome<Object>) parentA.clone();

		Object key;

		for (Map.Entry<Object, Gene> entry : parentA.getGenes().entrySet()) {
			key = entry.getKey();

			if (coin.flip()) {
				if (!child.getGenes().get(key).equals(parentB.getGenes().get(key))) {
					child.replaceGene(key, parentB.getGenes().get(key).clone());
				}
			}
		}

		return child;
	}

	/**
	 * @param coin
	 *            the coin to set
	 */
	@Required
	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	@Override
	public String getDisplayName() {
		return "Equal Opportunity";
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
