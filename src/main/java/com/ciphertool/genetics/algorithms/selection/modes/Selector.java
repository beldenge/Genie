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
package com.ciphertool.genetics.algorithms.selection.modes;

import java.util.List;

import com.ciphertool.genetics.Selectable;
import com.ciphertool.genetics.entities.Chromosome;

/**
 * This class serves as the mode of selecting a fit individual from the population. It is essentially a helper class
 * used by other algorithms, most notably any SelectionAlgorithm implementation.
 * 
 * @author george
 */
public interface Selector extends Selectable {

	/**
	 * @param individuals
	 *            the List of individuals to select from
	 * @param totalFitness
	 *            the total fitness of the population of individuals
	 * @return the indice of the chosen individual within the population
	 */
	public int getNextIndex(List<Chromosome> individuals, Double totalFitness);
}
