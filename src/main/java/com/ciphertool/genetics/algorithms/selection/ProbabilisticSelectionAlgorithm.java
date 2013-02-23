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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.entities.Chromosome;

public class ProbabilisticSelectionAlgorithm implements SelectionAlgorithm {
	private Logger log = Logger.getLogger(getClass());

	/*
	 * Performs selection by giving each individual a probabilistic chance of
	 * survival, weighted by its fitness.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm#select
	 * (com.ciphertool.genetics.Population, int, double)
	 */
	@Override
	public void select(Population population, int maxIndividuals, double survivalRate) {
		int initialPopulationSize = population.size();

		long numSurvivors = Math.round(maxIndividuals * survivalRate);

		if (log.isDebugEnabled()) {
			log.debug((initialPopulationSize - numSurvivors)
					+ " individuals to be removed from population of size " + initialPopulationSize
					+ " and survival rate of " + survivalRate + ".");
		}

		List<Chromosome> survivors = new ArrayList<Chromosome>();
		int survivorIndex;
		for (int i = 0; i < numSurvivors; i++) {
			survivorIndex = population.selectIndex();
			survivors.add(population.getIndividuals().get(survivorIndex));
			population.removeIndividual(survivorIndex);
		}

		/*
		 * Reset the population by clearing it and then adding back all the
		 * survivors.
		 * 
		 * TODO this is a candidate for parallelization
		 */
		population.clearIndividuals();
		for (Chromosome survivor : survivors) {
			population.addIndividual(survivor);
		}
	}
}
