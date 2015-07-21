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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.algorithms.selection.modes.RouletteSelector;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;

public class ProbabilisticSelectionAlgorithm implements SelectionAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	private static Selector selector = new RouletteSelector();

	/*
	 * Performs selection by giving each individual a probabilistic chance of survival, weighted by its fitness.
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

		int initialPopulationSize = population.size();

		/*
		 * We must use maxIndividuals instead of the current population size in case the current population size is
		 * larger than the maximum specified.
		 */
		long numSurvivors = Math.min(Math.round(maxSurvivors * survivalRate), initialPopulationSize);
		int numberRemoved = (int) (initialPopulationSize - numSurvivors);

		if (log.isDebugEnabled()) {
			log.debug(numberRemoved + " individuals to be removed from population of size " + initialPopulationSize
					+ " and survival rate of " + survivalRate + ".");
		}

		List<Chromosome> survivors = new ArrayList<Chromosome>();
		int survivorIndex;
		for (int i = 0; i < numSurvivors; i++) {
			survivorIndex = selector.getNextIndex(population.getIndividuals(), population.getTotalFitness());

			survivors.add(population.removeIndividual(survivorIndex));
		}

		/*
		 * Reset the population by clearing it and then adding back all the survivors.
		 */
		population.clearIndividuals();

		// This is a candidate for parallelization
		int numEvaluations = 0;
		for (Chromosome survivor : survivors) {
			numEvaluations += population.addIndividual(survivor) ? 1 : 0;
		}

		if (log.isDebugEnabled()) {
			log.debug("Number of evaluations during Selection: " + numEvaluations);
		}

		return numberRemoved;
	}

	@Override
	public String getDisplayName() {
		return "Probabilistic";
	}
}
