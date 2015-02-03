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

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.mocks.MockChromosome;

public class ProbabilisticSelectionAlgorithmTest {
	private static Logger logMock;
	private static Population population;
	private static ProbabilisticSelectionAlgorithm probabilisticSelectionAlgorithm;

	@BeforeClass
	public static void setUp() {
		probabilisticSelectionAlgorithm = new ProbabilisticSelectionAlgorithm();

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(ProbabilisticSelectionAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, probabilisticSelectionAlgorithm, logMock);
	}

	@Before
	public void resetDependencies() {
		population = new Population();

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(5.0);
		population.addIndividual(chromosome2);

		MockChromosome chromosome3 = new MockChromosome();
		chromosome3.setFitness(5.0);
		population.addIndividual(chromosome3);

		MockChromosome chromosome4 = new MockChromosome();
		chromosome4.setFitness(5.0);
		population.addIndividual(chromosome4);

		MockChromosome chromosome5 = new MockChromosome();
		chromosome5.setFitness(5.0);
		population.addIndividual(chromosome5);

		MockChromosome chromosome6 = new MockChromosome();
		chromosome6.setFitness(5.0);
		population.addIndividual(chromosome6);

		MockChromosome chromosome7 = new MockChromosome();
		chromosome7.setFitness(5.0);
		population.addIndividual(chromosome7);

		MockChromosome chromosome8 = new MockChromosome();
		chromosome8.setFitness(5.0);
		population.addIndividual(chromosome8);

		MockChromosome chromosome9 = new MockChromosome();
		chromosome9.setFitness(5.0);
		population.addIndividual(chromosome9);

		MockChromosome chromosome10 = new MockChromosome();
		chromosome10.setFitness(5.0);
		population.addIndividual(chromosome10);

		reset(logMock);
	}

	@Test
	public void testSelect() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		assertEquals(10, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());

		int numberRemoved = probabilisticSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

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

		MockChromosome chromosome11 = new MockChromosome();
		chromosome11.setFitness(5.0);
		population.addIndividual(chromosome11);

		MockChromosome chromosome12 = new MockChromosome();
		chromosome12.setFitness(5.0);
		population.addIndividual(chromosome12);

		MockChromosome chromosome13 = new MockChromosome();
		chromosome13.setFitness(5.0);
		population.addIndividual(chromosome13);

		MockChromosome chromosome14 = new MockChromosome();
		chromosome14.setFitness(5.0);
		population.addIndividual(chromosome14);

		assertEquals(14, population.size());
		assertEquals(new Double(70.0), population.getTotalFitness());

		int numberRemoved = probabilisticSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		assertEquals(6, numberRemoved);
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

		int numberRemoved = probabilisticSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

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

		int numberRemoved = probabilisticSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

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

		int numberRemoved = probabilisticSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

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

		int numberRemoved = probabilisticSelectionAlgorithm
				.select(null, maxSurvivors, survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testSelectWithEmptyPopulation() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		int numberRemoved = probabilisticSelectionAlgorithm.select(new Population(), maxSurvivors,
				survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}
}
