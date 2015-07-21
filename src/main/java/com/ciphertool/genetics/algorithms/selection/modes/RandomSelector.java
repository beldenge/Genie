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
package com.ciphertool.genetics.algorithms.selection.modes;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.ciphertool.genetics.entities.Chromosome;

public class RandomSelector implements Selector {
	private Logger log = Logger.getLogger(getClass());

	@Override
	public int getNextIndex(List<Chromosome> individuals, Double totalFitness) {
		if (individuals == null || individuals.isEmpty()) {
			log.warn("Attempted to select an individual from a null or empty population.  Unable to continue.");

			return -1;
		}

		int randomIndex = (int) (ThreadLocalRandom.current().nextDouble() * individuals.size());

		return randomIndex;
	}

	@Override
	public String getDisplayName() {
		return "Random";
	}
}