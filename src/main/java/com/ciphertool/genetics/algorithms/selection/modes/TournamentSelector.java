/**
 * Copyright 2013 George Belden
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

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.DescendingFitnessComparator;
import com.ciphertool.genetics.fitness.FitnessComparator;

public class TournamentSelector implements Selector {
	private Double selectionAccuracy;
	private static FitnessComparator fitnessComparator = new DescendingFitnessComparator();

	@Override
	public int getNextIndex(List<Chromosome> individuals, Double totalFitness) {
		Collections.sort(individuals, fitnessComparator);

		for (int i = 0; i < individuals.size(); i++) {
			if (Math.random() <= selectionAccuracy) {
				return i;
			}
		}

		// return the least fit individual since it won the tournament
		return individuals.size() - 1;
	}

	/**
	 * @param selectionAccuracy
	 *            the selectionAccuracy to set
	 */
	@Required
	public void setSelectionAccuracy(Double selectionAccuracy) {
		if (selectionAccuracy < 0.0 || selectionAccuracy > 1.0) {
			throw new IllegalArgumentException(
					"Tried to set a selectionAccuracy of "
							+ selectionAccuracy
							+ ", but TournamentSelector requires a selectionAccuracy between 0.0 and 1.0 inclusive.");
		}

		this.selectionAccuracy = selectionAccuracy;
	}
}
