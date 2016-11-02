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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.population.StandardPopulation;

public class TruncationSelectionAlgorithm implements SelectionAlgorithm {
	private Logger log = LoggerFactory.getLogger(getClass());

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
	public int select(StandardPopulation population, int maxSurvivors, double survivalRate) {
		if (population == null || population.getIndividuals().isEmpty()) {
			log.warn("Attempted to perform selection on null or empty population.  Cannot continue.");

			return 0;
		}

		population.sortIndividuals();

		int initialPopulationSize = population.size();

		int survivorIndex = initialPopulationSize - (int) Math.min(Math.round(maxSurvivors
				* survivalRate), initialPopulationSize);

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

	@Override
	public String getDisplayName() {
		return "Truncation";
	}
}
