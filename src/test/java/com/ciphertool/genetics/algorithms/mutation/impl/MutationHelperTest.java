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

package com.ciphertool.genetics.algorithms.mutation.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ciphertool.genetics.algorithms.mutation.MutationHelper;

public class MutationHelperTest {
	@Test
	public void testGetNumMutations() {
		int maxMutations = 50;
		double mutationCountFactor = 0.1;
		int size = 50;
		int attempts = 500;

		MutationHelper mutationHelper = new MutationHelper();
		mutationHelper.setMaxMutations(maxMutations);
		mutationHelper.setMutationCountFactor(mutationCountFactor);

		Map<Integer, Integer> ints = new HashMap<Integer, Integer>();
		for (int i = 0; i < attempts; i++) {
			int numMutations = mutationHelper.getNumMutations(size);

			if (!ints.containsKey(numMutations)) {
				ints.put(numMutations, 0);
			}

			ints.put(numMutations, ints.get(numMutations) + 1);

			assertTrue(numMutations >= 1 && numMutations <= maxMutations);
		}
	}
}
