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

package com.ciphertool.genetics.fitness;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class AscendingFitnessComparatorTest {
	@Test
	public void testCompare() {
		MockKeylessChromosome higherFitness = new MockKeylessChromosome();
		higherFitness.setFitness(2.0);

		MockKeylessChromosome lowerFitness = new MockKeylessChromosome();
		lowerFitness.setFitness(1.0);

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();

		int result = ascendingFitnessComparator.compare(higherFitness, lowerFitness);
		assertEquals(1, result);

		result = ascendingFitnessComparator.compare(lowerFitness, higherFitness);
		assertEquals(-1, result);
	}

	@Test
	public void testCompareEqual() {
		MockKeylessChromosome mockA = new MockKeylessChromosome();
		mockA.setFitness(3.0);

		MockKeylessChromosome mockB = new MockKeylessChromosome();
		mockB.setFitness(3.0);

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();
		int result = ascendingFitnessComparator.compare(mockA, mockB);
		assertEquals(0, result);
	}
}
