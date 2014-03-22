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

package com.ciphertool.genetics.algorithms.crossover;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CrossoverProgressDtoTest {
	private static final int AMOUNT_TO_ADVANCE = 5;

	@Test
	public void testConstructor() {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		assertEquals(0, crossoverProgressDto.getChildGeneIndex());
		assertEquals(0, crossoverProgressDto.getChildSequencePosition());
		assertEquals(0, crossoverProgressDto.getParentGeneIndex());
		assertEquals(0, crossoverProgressDto.getParentSequencePosition());
	}

	@Test
	public void testAdvanceChildSequencePositionBy() {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		assertEquals(0, crossoverProgressDto.getChildSequencePosition());

		crossoverProgressDto.advanceChildSequencePositionBy(AMOUNT_TO_ADVANCE);

		assertEquals(AMOUNT_TO_ADVANCE, crossoverProgressDto.getChildSequencePosition());
	}

	@Test
	public void testAdvanceParentSequencePositionBy() {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		assertEquals(0, crossoverProgressDto.getParentSequencePosition());

		crossoverProgressDto.advanceParentSequencePositionBy(AMOUNT_TO_ADVANCE);

		assertEquals(AMOUNT_TO_ADVANCE, crossoverProgressDto.getParentSequencePosition());
	}

	@Test
	public void testAdvanceChildGeneIndexBy() {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		assertEquals(0, crossoverProgressDto.getChildGeneIndex());

		crossoverProgressDto.advanceChildGeneIndexBy(AMOUNT_TO_ADVANCE);

		assertEquals(AMOUNT_TO_ADVANCE, crossoverProgressDto.getChildGeneIndex());
	}

	@Test
	public void testAdvanceParentGeneIndexBy() {
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		assertEquals(0, crossoverProgressDto.getParentGeneIndex());

		crossoverProgressDto.advanceParentGeneIndexBy(AMOUNT_TO_ADVANCE);

		assertEquals(AMOUNT_TO_ADVANCE, crossoverProgressDto.getParentGeneIndex());
	}
}
