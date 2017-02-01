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

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.entities.Chromosome;

public class AlphaSelector implements Selector {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public synchronized void reIndex(List<Chromosome> individuals) {
		// Nothing to do
	}

	@Override
	public int getNextIndex(List<Chromosome> individuals, BigDecimal totalFitness) {
		if (individuals == null || individuals.isEmpty()) {
			log.warn("Attempted to select an individual from a null or empty population.  Unable to continue.");

			return -1;
		}

		Chromosome bestFitIndividual = null;
		Integer bestFitIndex = null;

		Chromosome currentIndividual = null;
		for (int i = 0; i < individuals.size(); i++) {
			currentIndividual = individuals.get(i);

			if (bestFitIndex == null || currentIndividual.getFitness().compareTo(bestFitIndividual.getFitness()) > 0) {
				bestFitIndividual = currentIndividual;
				bestFitIndex = i;
			}
		}

		return (bestFitIndex != null) ? bestFitIndex : -1;
	}

	@Override
	public String getDisplayName() {
		return "Alpha";
	}
}
