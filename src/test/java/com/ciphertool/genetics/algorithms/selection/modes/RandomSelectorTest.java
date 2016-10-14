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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class RandomSelectorTest {
	private static RandomSelector randomSelector;
	private static Logger logMock;

	@BeforeClass
	public static void setUp() {
		randomSelector = new RandomSelector();

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(RandomSelector.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, randomSelector, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
	}

	@Test
	public void testGetNextIndex() {
		List<Chromosome> individuals = new ArrayList<Chromosome>();

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(2.0);
		individuals.add(chromosome1);

		Double bestFitness = 3.0;
		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
		chromosome2.setFitness(bestFitness);
		individuals.add(chromosome2);

		MockKeylessChromosome chromosome3 = new MockKeylessChromosome();
		chromosome3.setFitness(1.0);
		individuals.add(chromosome3);

		int selectedIndex = randomSelector.getNextIndex(individuals, null);

		assertTrue(selectedIndex > -1);
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testGetNextIndexWithNullPopulation() {
		int selectedIndex = randomSelector.getNextIndex(null, 6.0);

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetNextIndexWithEmptyPopulation() {
		int selectedIndex = randomSelector.getNextIndex(new ArrayList<Chromosome>(), 6.0);

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}
}
