/**
 * Copyright 2012 George Belden
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
package com.ciphertool.genetics.algorithms.selection;

import com.ciphertool.genetics.Population;

public interface SelectionAlgorithm {
	/**
	 * Performs natural selection against the supplied Population, modifying it
	 * by reference.
	 * 
	 * @param population
	 *            the population to select upon
	 * @param maxIndividuals
	 *            the maximum number of individuals allowed in the population
	 * @param survivalRate
	 *            the percentage of individuals to survive
	 */
	public void select(Population population, int maxIndividuals, double survivalRate);
}
