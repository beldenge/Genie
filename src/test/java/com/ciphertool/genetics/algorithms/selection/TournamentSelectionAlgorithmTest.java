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

package com.ciphertool.genetics.algorithms.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.StandardPopulation;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.algorithms.selection.modes.TournamentSelector;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class TournamentSelectionAlgorithmTest {
	private static Logger logMock;
	private static StandardPopulation population;
	private static TournamentSelectionAlgorithm tournamentSelectionAlgorithm;

	@BeforeClass
	public static void setUp() {
		tournamentSelectionAlgorithm = new TournamentSelectionAlgorithm();
		tournamentSelectionAlgorithm.setGroupSize(3);
		TournamentSelector tournamentSelector = new TournamentSelector();
		tournamentSelector.setSelectionAccuracy(0.9);
		tournamentSelectionAlgorithm.setGroupSelector(tournamentSelector);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(TournamentSelectionAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, tournamentSelectionAlgorithm, logMock);
	}

	@Before
	public void resetDependencies() {
		population = new StandardPopulation();

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
		chromosome2.setFitness(5.0);
		population.addIndividual(chromosome2);

		MockKeylessChromosome chromosome3 = new MockKeylessChromosome();
		chromosome3.setFitness(5.0);
		population.addIndividual(chromosome3);

		MockKeylessChromosome chromosome4 = new MockKeylessChromosome();
		chromosome4.setFitness(5.0);
		population.addIndividual(chromosome4);

		MockKeylessChromosome chromosome5 = new MockKeylessChromosome();
		chromosome5.setFitness(5.0);
		population.addIndividual(chromosome5);

		MockKeylessChromosome chromosome6 = new MockKeylessChromosome();
		chromosome6.setFitness(5.0);
		population.addIndividual(chromosome6);

		MockKeylessChromosome chromosome7 = new MockKeylessChromosome();
		chromosome7.setFitness(5.0);
		population.addIndividual(chromosome7);

		MockKeylessChromosome chromosome8 = new MockKeylessChromosome();
		chromosome8.setFitness(5.0);
		population.addIndividual(chromosome8);

		MockKeylessChromosome chromosome9 = new MockKeylessChromosome();
		chromosome9.setFitness(5.0);
		population.addIndividual(chromosome9);

		MockKeylessChromosome chromosome10 = new MockKeylessChromosome();
		chromosome10.setFitness(5.0);
		population.addIndividual(chromosome10);

		reset(logMock);
	}

	@Test
	public void testSetGroupSize() {
		Integer groupSizeToSet = 3;

		TournamentSelectionAlgorithm tournamentSelectionAlgorithm = new TournamentSelectionAlgorithm();
		tournamentSelectionAlgorithm.setGroupSize(groupSizeToSet);

		Field groupSizeField = ReflectionUtils.findField(TournamentSelectionAlgorithm.class, "groupSize");
		ReflectionUtils.makeAccessible(groupSizeField);
		Integer groupSizeFromObject = (Integer) ReflectionUtils.getField(groupSizeField, tournamentSelectionAlgorithm);

		assertSame(groupSizeToSet, groupSizeFromObject);
	}

	@Test
	public void testSetGroupSelector() {
		TournamentSelector groupSelectorToSet = new TournamentSelector();

		TournamentSelectionAlgorithm tournamentSelectionAlgorithm = new TournamentSelectionAlgorithm();
		tournamentSelectionAlgorithm.setGroupSelector(groupSelectorToSet);

		Field groupSelectorField = ReflectionUtils.findField(TournamentSelectionAlgorithm.class, "groupSelector");
		ReflectionUtils.makeAccessible(groupSelectorField);
		Selector groupSelectorFromObject = (Selector) ReflectionUtils.getField(groupSelectorField,
				tournamentSelectionAlgorithm);

		assertSame(groupSelectorToSet, groupSelectorFromObject);
	}

	@Test
	public void testSelect() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		assertEquals(10, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());

		int numberRemoved = tournamentSelectionAlgorithm.select(population, maxSurvivors, survivalRate);

		assertEquals(2, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(40.0), population.getTotalFitness());
		verify(logMock, times(2)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectWithMoreThanMax() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		MockKeylessChromosome chromosome11 = new MockKeylessChromosome();
		chromosome11.setFitness(5.0);
		population.addIndividual(chromosome11);

		MockKeylessChromosome chromosome12 = new MockKeylessChromosome();
		chromosome12.setFitness(5.0);
		population.addIndividual(chromosome12);

		assertEquals(12, population.size());
		assertEquals(new Double(60.0), population.getTotalFitness());

		int numberRemoved = tournamentSelectionAlgorithm.select(population, maxSurvivors, survivalRate);

		assertEquals(4, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(40.0), population.getTotalFitness());
		verify(logMock, times(2)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectWithLessThanMax() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		population.removeIndividual(9);
		population.removeIndividual(8);
		population.removeIndividual(7);

		assertEquals(7, population.size());
		assertEquals(new Double(35.0), population.getTotalFitness());

		int numberRemoved = tournamentSelectionAlgorithm.select(population, maxSurvivors, survivalRate);

		assertEquals(0, numberRemoved);
		assertEquals(7, population.size());
		assertEquals(new Double(35.0), population.getTotalFitness());
		verify(logMock, times(2)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectRoundUp() {
		int maxSurvivors = 10;

		// This should end up rounding up to 0.9
		double survivalRate = 0.85;

		assertEquals(10, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());

		int numberRemoved = tournamentSelectionAlgorithm.select(population, maxSurvivors, survivalRate);

		assertEquals(1, numberRemoved);
		assertEquals(9, population.size());
		assertEquals(new Double(45.0), population.getTotalFitness());
		verify(logMock, times(2)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectRoundDown() {
		int maxSurvivors = 10;

		// This should end up rounding down to 0.8
		double survivalRate = 0.849;

		assertEquals(10, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());

		int numberRemoved = tournamentSelectionAlgorithm.select(population, maxSurvivors, survivalRate);

		assertEquals(2, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(40.0), population.getTotalFitness());
		verify(logMock, times(2)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectWithNullPopulation() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		int numberRemoved = tournamentSelectionAlgorithm.select(null, maxSurvivors, survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testSelectWithEmptyPopulation() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		int numberRemoved = tournamentSelectionAlgorithm.select(new StandardPopulation(), maxSurvivors, survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testGetGroupTotalFitness() {
		Double groupTotalFitness = TournamentSelectionAlgorithm.getGroupTotalFitness(population.getIndividuals());

		assertEquals(new Double(50.0), groupTotalFitness);
	}
}
