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
package com.ciphertool.genetics.algorithms.selection;

import org.apache.log4j.Logger;

import com.ciphertool.genetics.Population;

public class TruncationSelectionAlgorithm implements SelectionAlgorithm {
	private Logger log = Logger.getLogger(getClass());

	/*
	 * Performs selection by simply dropping all of the individuals that fall below the survival rate, based on fitness.
	 * So only the most fit individuals will survive.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm#select (com.ciphertool.genetics.Population,
	 * int, double)
	 */
	@Override
	public int select(Population population, int maxSurvivors, double survivalRate) {
		if (population == null || population.getIndividuals().isEmpty()) {
			log.warn("Attempted to perform selection on null or empty population.  Cannot continue.");

			return 0;
		}

		population.sortIndividuals();

		int initialPopulationSize = population.size();

		int survivorIndex = initialPopulationSize
				- (int) Math.min(Math.round(maxSurvivors * survivalRate), initialPopulationSize);

		if (log.isDebugEnabled()) {
			log.debug(survivorIndex + " individuals to be removed from population of size " + population.size()
					+ " and survival rate of " + survivalRate + ".");
		}

		for (int i = survivorIndex - 1; i >= 0; i--) {
			/*
			 * The List is sorted in ascending order in terms of fitness, where higher fitness value may or may not be
			 * preferred depending on the comparator used.
			 */
			population.removeIndividual(i);
		}

		return survivorIndex;
	}
}
