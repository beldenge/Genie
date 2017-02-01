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

package com.ciphertool.genetics.fitness;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.ciphertool.genetics.mocks.MockKeyedChromosome;

public class AscendingFitnessComparatorTest {
	@Test
	public void testCompare() {
		MockKeyedChromosome higherFitness = new MockKeyedChromosome();
		higherFitness.setFitness(BigDecimal.valueOf(2.0));

		MockKeyedChromosome lowerFitness = new MockKeyedChromosome();
		lowerFitness.setFitness(BigDecimal.valueOf(1.0));

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();

		int result = ascendingFitnessComparator.compare(higherFitness, lowerFitness);
		assertEquals(1, result);

		result = ascendingFitnessComparator.compare(lowerFitness, higherFitness);
		assertEquals(-1, result);
	}

	@Test
	public void testCompareEqual() {
		MockKeyedChromosome mockA = new MockKeyedChromosome();
		mockA.setFitness(BigDecimal.valueOf(3.0));

		MockKeyedChromosome mockB = new MockKeyedChromosome();
		mockB.setFitness(BigDecimal.valueOf(3.0));

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();
		int result = ascendingFitnessComparator.compare(mockA, mockB);
		assertEquals(0, result);
	}
}
