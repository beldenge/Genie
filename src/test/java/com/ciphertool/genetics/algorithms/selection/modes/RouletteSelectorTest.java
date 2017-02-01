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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.selection.BinaryRouletteNode;
import com.ciphertool.genetics.algorithms.selection.BinaryRouletteTree;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockKeyedChromosome;

public class RouletteSelectorTest {
	private static RouletteSelector	rouletteSelector;
	private static Logger			logMock;

	@BeforeClass
	public static void setUp() {
		rouletteSelector = new RouletteSelector();

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(RouletteSelector.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, rouletteSelector, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
	}

	@Test
	public void testGetNextIndex() {
		BinaryRouletteTree binaryRouletteTree = new BinaryRouletteTree();
		binaryRouletteTree.insert(new BinaryRouletteNode(0, BigDecimal.valueOf(7.0)));

		Field rouletteWheelField = ReflectionUtils.findField(RouletteSelector.class, "rouletteWheel");
		ReflectionUtils.makeAccessible(rouletteWheelField);
		ReflectionUtils.setField(rouletteWheelField, rouletteSelector, binaryRouletteTree);

		List<Chromosome> individuals = new ArrayList<Chromosome>();

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(BigDecimal.valueOf(2.0));
		individuals.add(chromosome1);

		BigDecimal bestFitness = BigDecimal.valueOf(3.0);
		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
		chromosome2.setFitness(bestFitness);
		individuals.add(chromosome2);

		MockKeyedChromosome chromosome3 = new MockKeyedChromosome();
		chromosome3.setFitness(BigDecimal.valueOf(1.0));
		individuals.add(chromosome3);

		int selectedIndex = rouletteSelector.getNextIndex(individuals, BigDecimal.valueOf(6.0));

		assertTrue(selectedIndex > -1);
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testGetNextIndexWithNullPopulation() {
		int selectedIndex = rouletteSelector.getNextIndex(null, BigDecimal.valueOf(6.0));

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetNextIndexWithEmptyPopulation() {
		int selectedIndex = rouletteSelector.getNextIndex(new ArrayList<Chromosome>(), BigDecimal.valueOf(6.0));

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetNextIndexWithNullTotalFitness() {
		List<Chromosome> individuals = new ArrayList<Chromosome>();

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(BigDecimal.valueOf(2.0));
		individuals.add(chromosome1);

		BigDecimal bestFitness = BigDecimal.valueOf(3.0);
		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
		chromosome2.setFitness(bestFitness);
		individuals.add(chromosome2);

		MockKeyedChromosome chromosome3 = new MockKeyedChromosome();
		chromosome3.setFitness(BigDecimal.valueOf(1.0));
		individuals.add(chromosome3);

		int selectedIndex = rouletteSelector.getNextIndex(individuals, null);

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}
}
