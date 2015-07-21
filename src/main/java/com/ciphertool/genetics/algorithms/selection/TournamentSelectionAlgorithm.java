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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.algorithms.selection.modes.RandomSelector;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;

public class TournamentSelectionAlgorithm implements SelectionAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	private static Selector randomSelector = new RandomSelector();
	private Selector groupSelector;
	private Integer groupSize;

	/*
	 * Performs selection by creating groups of Chromosomes and running tournaments upon each of them.
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
		long numSurvivors = Math.min(Math.round(maxSurvivors * survivalRate), population.size());
		int numberRemoved = (int) (initialPopulationSize - numSurvivors);

		if (log.isDebugEnabled()) {
			log.debug(numberRemoved + " individuals to be removed from population of size " + initialPopulationSize
					+ " and survival rate of " + survivalRate + ".");
		}

		List<Chromosome> survivors = new ArrayList<Chromosome>();
		int randomIndex;
		int survivorIndex;
		Map<Chromosome, Integer> group;
		List<Chromosome> randomIndividuals;
		for (int i = 0; i < numSurvivors; i++) {
			group = new HashMap<Chromosome, Integer>();
			for (int j = 0; j < groupSize; j++) {
				randomIndex = randomSelector.getNextIndex(population.getIndividuals(), null);
				group.put(population.getIndividuals().get(randomIndex), randomIndex);
			}

			/*
			 * We have to relate the index from the tournament group back to the index of the overall population
			 */
			randomIndividuals = new ArrayList<Chromosome>();
			randomIndividuals.addAll(group.keySet());
			Integer tournamentIndex = groupSelector.getNextIndex(randomIndividuals,
					getGroupTotalFitness(randomIndividuals));
			survivorIndex = group.get(randomIndividuals.get(tournamentIndex));

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

	/**
	 * This method is needed for Selector modes which require the total fitness of the List of individuals in order to
	 * select from them.
	 * 
	 * @param group
	 *            the group of Chromosomes
	 * @return the total fitness of the group
	 */
	protected static Double getGroupTotalFitness(List<Chromosome> group) {
		Double groupTotalFitness = 0.0;

		for (Chromosome individual : group) {
			groupTotalFitness += individual.getFitness();
		}

		return groupTotalFitness;
	}

	/**
	 * @param groupSize
	 *            the groupSize to set
	 */
	@Required
	public void setGroupSize(Integer groupSize) {
		this.groupSize = groupSize;
	}

	/**
	 * @param groupSelector
	 *            the groupSelector to set
	 */
	@Required
	public void setGroupSelector(Selector groupSelector) {
		this.groupSelector = groupSelector;
	}

	@Override
	public String getDisplayName() {
		return "Tournament";
	}
}
