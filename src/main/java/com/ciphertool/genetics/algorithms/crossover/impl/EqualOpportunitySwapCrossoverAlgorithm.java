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

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.entities.Ancestry;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.util.Coin;

public class EqualOpportunitySwapCrossoverAlgorithm implements CrossoverAlgorithm<KeyedChromosome<Object>> {
	private int		maxGenerations;

	private Coin	coin;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		List<KeyedChromosome<Object>> children = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		for (KeyedChromosome<Object> child : children) {
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
	protected List<KeyedChromosome<Object>> performCrossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		KeyedChromosome<Object> childA = (KeyedChromosome<Object>) parentA.clone();
		KeyedChromosome<Object> childB = (KeyedChromosome<Object>) parentB.clone();

		for (Object key : parentA.getGenes().keySet()) {
			if (coin.flip()) {
				childA.replaceGene(key, parentB.getGenes().get(key).clone());
				childB.replaceGene(key, parentA.getGenes().get(key).clone());
			}
		}

		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>(2);
		children.add(childA);
		children.add(childB);

		return children;
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
		return "Equal Opportunity Swap";
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
		return 2;
	}
}
