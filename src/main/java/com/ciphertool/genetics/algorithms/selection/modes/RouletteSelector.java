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
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.entities.Chromosome;

public class RouletteSelector implements Selector {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public int getNextIndex(List<Chromosome> individuals, Double totalFitness) {
		if (individuals == null || individuals.isEmpty()) {
			log.warn("Attempted to select an individual from a null or empty population.  Unable to continue.");

			return -1;
		}

		if (totalFitness == null) {
			log.warn("This Selector implementation requires a non-null total fitness.  Unable to continue.");

			return -1;
		}

		double randomIndex = totalFitness - (ThreadLocalRandom.current().nextDouble() * totalFitness);

		int winningIndex = -1;
		Chromosome nextIndividual = null;

		for (int i = 0; i < individuals.size(); i++) {
			nextIndividual = individuals.get(i);
			if (nextIndividual.getFitness() == null) {
				log.warn("Attempted to spin roulette wheel but an individual was found with a null fitness value.  Please make a call to evaluateFitness() before attempting to spin the roulette wheel. "
						+ nextIndividual);

				continue;
			}

			randomIndex -= nextIndividual.getFitness();

			/*
			 * If we have subtracted everything from randomIndex, then the ball has stopped rolling.
			 */
			if (randomIndex <= 0) {
				winningIndex = i;

				break;
			}
		}

		return winningIndex;
	}

	@Override
	public String getDisplayName() {
		return "Roulette";
	}
}
