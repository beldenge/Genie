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

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockKeyedChromosome;

public class TournamentSelectorTest {
	private static TournamentSelector	tournamentSelector;
	private static Logger				logMock;

	@BeforeClass
	public static void setUp() {
		tournamentSelector = new TournamentSelector();
		tournamentSelector.setSelectionAccuracy(0.9);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(TournamentSelector.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, tournamentSelector, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
	}

	@Test
	public void testSetSelectionAccuracy() {
		Double selectionAccuracyToSet = 0.9;

		TournamentSelector tournamentSelector = new TournamentSelector();
		tournamentSelector.setSelectionAccuracy(selectionAccuracyToSet);

		Field selectionAccuracyField = ReflectionUtils.findField(TournamentSelector.class, "selectionAccuracy");
		ReflectionUtils.makeAccessible(selectionAccuracyField);

		assertEquals(selectionAccuracyToSet, ReflectionUtils.getField(selectionAccuracyField, tournamentSelector));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSelectionAccuracyInvalidNegative() {
		Double selectionAccuracyToSet = -0.9;

		TournamentSelector tournamentSelector = new TournamentSelector();
		tournamentSelector.setSelectionAccuracy(selectionAccuracyToSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSelectionAccuracyInvalidPositive() {
		Double selectionAccuracyToSet = 1.9;

		TournamentSelector tournamentSelector = new TournamentSelector();
		tournamentSelector.setSelectionAccuracy(selectionAccuracyToSet);
	}

	@Test
	public void testGetNextIndex() {
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

		int selectedIndex = tournamentSelector.getNextIndex(individuals, BigDecimal.valueOf(6.0));

		assertTrue(selectedIndex > -1);
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testGetNextIndexWithNullPopulation() {
		int selectedIndex = tournamentSelector.getNextIndex(null, BigDecimal.valueOf(6.0));

		assertEquals(-1, selectedIndex);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetNextIndexWithEmptyPopulation() {
		int selectedIndex = tournamentSelector.getNextIndex(new ArrayList<Chromosome>(), BigDecimal.valueOf(6.0));

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

		int selectedIndex = tournamentSelector.getNextIndex(individuals, null);

		assertTrue(selectedIndex > -1);
		verifyZeroInteractions(logMock);
	}
}
