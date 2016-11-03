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
package com.ciphertool.genetics.algorithms.selection;

import com.ciphertool.genetics.Selectable;
import com.ciphertool.genetics.population.StandardPopulation;

public interface SelectionAlgorithm extends Selectable {
	/**
	 * Performs natural selection against the supplied Population, modifying it by reference.
	 * 
	 * @param population
	 *            the population to select upon
	 * @param maxSurvivors
	 *            the maximum number of individuals allowed in the population
	 * @param survivalRate
	 *            the percentage of individuals to survive
	 * @return the number of individuals selected out (i.e. did not survive)
	 */
	public int select(StandardPopulation population, int maxSurvivors, double survivalRate);
}
