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

package com.ciphertool.genetics.algorithms.selection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SelectionAlgorithmTypeTest {

	@Test
	public void testTypes() {
		assertEquals(3, SelectionAlgorithmType.values().length);

		assertEquals(ProbabilisticSelectionAlgorithm.class, SelectionAlgorithmType.PROBABILISTIC
				.getType());
		assertEquals(TournamentSelectionAlgorithm.class, SelectionAlgorithmType.TOURNAMENT
				.getType());
		assertEquals(TruncationSelectionAlgorithm.class, SelectionAlgorithmType.TRUNCATION
				.getType());
	}
}
