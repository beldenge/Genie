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
package com.ciphertool.genetics.algorithms.selection.modes;

import java.util.List;

import org.apache.log4j.Logger;

import com.ciphertool.genetics.entities.Chromosome;

public class RouletteSelector implements Selector {
	private Logger log = Logger.getLogger(getClass());

	@Override
	public int getNextIndex(List<Chromosome> individuals, Double totalFitness) {
		long randomIndex = (int) (Math.random() * totalFitness);

		int winningIndex = -1;
		Chromosome nextIndividual = null;

		for (int i = 0; i < individuals.size(); i++) {
			nextIndividual = individuals.get(i);
			if (nextIndividual.getFitness() == null) {
				log.warn("Attempted to spin roulette wheel but an individual was found with a null fitness value.  Please make a call to evaluateFitness() before attempting to spin the roulette wheel. "
						+ nextIndividual);
			}

			randomIndex -= nextIndividual.getFitness();

			/*
			 * If we have subtracted everything from randomIndex, then the ball
			 * has stopped rolling.
			 */
			if (randomIndex <= 0) {
				winningIndex = i;

				break;
			}
		}

		return winningIndex;
	}
}