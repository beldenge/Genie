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

package com.ciphertool.genetics.algorithms.crossover;

import java.util.List;

import com.ciphertool.genetics.Selectable;
import com.ciphertool.genetics.entities.Chromosome;

public interface CrossoverAlgorithm<T extends Chromosome> extends Selectable {

	/**
	 * Performs crossover to a List of children by cloning one or both of the parents and then selectively replacing
	 * Genes from the other parent.
	 * 
	 * @param parentA
	 *            the first parent
	 * @param parentB
	 *            the second parent
	 * @return the List of children Chromosomes produced from the crossover
	 */
	public List<T> crossover(T parentA, T parentB);

	/**
	 * @return the number of offspring this CrossoverAlgorithm will generate
	 */
	public int numberOfOffspring();
}
