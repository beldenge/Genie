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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class AlphaSelectorTest {
	private static AlphaSelector alphaSelector;
	private static Logger logMock;

	@BeforeClass
	public static void setUp() {
		alphaSelector = new AlphaSelector();

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(AlphaSelector.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, alphaSelector, logMock);
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

		int selectedIndex = alphaSelector.getNextIndex(individuals, null);

		assertEquals(1, selectedIndex);
		assertEquals(bestFitness, individuals.get(selectedIndex).getFitness());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testGetNextIndexWithNullPopulation() {
		int selectedIndex = alphaSelector.getNextIndex(null, 6.0);

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetNextIndexWithEmptyPopulation() {
		int selectedIndex = alphaSelector.getNextIndex(new ArrayList<Chromosome>(), 6.0);

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}
}
