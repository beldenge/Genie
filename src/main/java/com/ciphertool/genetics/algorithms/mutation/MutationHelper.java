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

package com.ciphertool.genetics.algorithms.mutation;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Required;

public class MutationHelper {
	private int		maxMutations;
	private double	mutationCountFactor;

	public int getNumMutations(int numGenes) {
		int max = Math.min(maxMutations, numGenes);

		for (int i = 1; i <= max; i++) {
			if (ThreadLocalRandom.current().nextDouble() < mutationCountFactor) {
				return i;
			}
		}

		return 1;
	}

	/**
	 * @param maxMutations
	 *            the maxMutations to set
	 */
	@Required
	public void setMaxMutations(int maxMutations) {
		this.maxMutations = maxMutations;
	}

	/**
	 * @param mutationCountFactor
	 *            the mutationCountFactor to set
	 */
	@Required
	public void setMutationCountFactor(double mutationCountFactor) {
		this.mutationCountFactor = mutationCountFactor;
	}
}
