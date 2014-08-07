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
import com.ciphertool.genetics.fitness.AscendingFitnessComparator;
import com.ciphertool.genetics.mocks.MockChromosome;

public class TruncationSelectionAlgorithmTest {
	private static Logger logMock;
	private static Population population;
	private static TruncationSelectionAlgorithm truncationSelectionAlgorithm;

	@BeforeClass
	public static void setUp() {
		truncationSelectionAlgorithm = new TruncationSelectionAlgorithm();

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(TruncationSelectionAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, truncationSelectionAlgorithm, logMock);
	}

	@Before
	public void resetDependencies() {
		population = new Population();
		population.setFitnessComparator(new AscendingFitnessComparator());

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(3.0);
		population.addIndividual(chromosome1);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(4.0);
		population.addIndividual(chromosome2);

		MockChromosome chromosome3 = new MockChromosome();
		chromosome3.setFitness(5.0);
		population.addIndividual(chromosome3);

		MockChromosome chromosome4 = new MockChromosome();
		chromosome4.setFitness(1.0);
		population.addIndividual(chromosome4);

		MockChromosome chromosome5 = new MockChromosome();
		chromosome5.setFitness(6.0);
		population.addIndividual(chromosome5);

		MockChromosome chromosome6 = new MockChromosome();
		chromosome6.setFitness(7.0);
		population.addIndividual(chromosome6);

		MockChromosome chromosome7 = new MockChromosome();
		chromosome7.setFitness(8.0);
		population.addIndividual(chromosome7);

		MockChromosome chromosome8 = new MockChromosome();
		chromosome8.setFitness(2.0);
		population.addIndividual(chromosome8);

		MockChromosome chromosome9 = new MockChromosome();
		chromosome9.setFitness(9.0);
		population.addIndividual(chromosome9);

		MockChromosome chromosome10 = new MockChromosome();
		chromosome10.setFitness(10.0);
		population.addIndividual(chromosome10);

		reset(logMock);
	}

	@Test
	public void testSelect() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		assertEquals(10, population.size());
		assertEquals(new Double(55.0), population.getTotalFitness());

		int numberRemoved = truncationSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		int expectedNumberRemoved = 2;
		assertEquals(expectedNumberRemoved, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(52.0), population.getTotalFitness());
		verify(logMock, times(1)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);

		// Verify that the top-most-fit Chromosomes are the survivors
		for (int i = 1; i < population.size(); i++) {
			assertEquals(new Double(i + expectedNumberRemoved), population.getIndividuals().get(
					i - 1).getFitness());
		}
	}

	@Test
	public void testSelectWithMoreThanMax() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		MockChromosome chromosome11 = new MockChromosome();
		chromosome11.setFitness(11.0);
		population.addIndividual(chromosome11);

		MockChromosome chromosome12 = new MockChromosome();
		chromosome12.setFitness(12.0);
		population.addIndividual(chromosome12);

		MockChromosome chromosome13 = new MockChromosome();
		chromosome13.setFitness(13.0);
		population.addIndividual(chromosome13);

		MockChromosome chromosome14 = new MockChromosome();
		chromosome14.setFitness(14.0);
		population.addIndividual(chromosome14);

		assertEquals(14, population.size());
		assertEquals(new Double(105.0), population.getTotalFitness());

		int numberRemoved = truncationSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		int expectedNumberRemoved = 6;
		assertEquals(expectedNumberRemoved, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(84.0), population.getTotalFitness());
		verify(logMock, times(1)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);

		// Verify that the top-most-fit Chromosomes are the survivors
		for (int i = 1; i < population.size(); i++) {
			assertEquals(new Double(i + expectedNumberRemoved), population.getIndividuals().get(
					i - 1).getFitness());
		}
	}

	@Test
	public void testSelectWithLessThanMax() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		population.removeIndividualTemporarily(9);
		population.removeIndividualTemporarily(8);
		population.removeIndividualTemporarily(7);

		assertEquals(7, population.size());
		assertEquals(new Double(34.0), population.getTotalFitness());

		int numberRemoved = truncationSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		assertEquals(0, numberRemoved);
		assertEquals(7, population.size());
		assertEquals(new Double(34.0), population.getTotalFitness());
		verify(logMock, times(1)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testSelectRoundUp() {
		int maxSurvivors = 10;

		// This should end up rounding up to 0.9
		double survivalRate = 0.85;

		assertEquals(10, population.size());
		assertEquals(new Double(55.0), population.getTotalFitness());

		int numberRemoved = truncationSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		int expectedNumberRemoved = 1;
		assertEquals(expectedNumberRemoved, numberRemoved);
		assertEquals(9, population.size());
		assertEquals(new Double(54.0), population.getTotalFitness());
		verify(logMock, times(1)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);

		// Verify that the top-most-fit Chromosomes are the survivors
		for (int i = 1; i < population.size(); i++) {
			assertEquals(new Double(i + expectedNumberRemoved), population.getIndividuals().get(
					i - 1).getFitness());
		}
	}

	@Test
	public void testSelectRoundDown() {
		int maxSurvivors = 10;

		// This should end up rounding down to 0.8
		double survivalRate = 0.849;

		assertEquals(10, population.size());
		assertEquals(new Double(55.0), population.getTotalFitness());

		int numberRemoved = truncationSelectionAlgorithm.select(population, maxSurvivors,
				survivalRate);

		int expectedNumberRemoved = 2;
		assertEquals(expectedNumberRemoved, numberRemoved);
		assertEquals(8, population.size());
		assertEquals(new Double(52.0), population.getTotalFitness());
		verify(logMock, times(1)).isDebugEnabled();
		verifyNoMoreInteractions(logMock);

		// Verify that the top-most-fit Chromosomes are the survivors
		for (int i = 1; i < population.size(); i++) {
			assertEquals(new Double(i + expectedNumberRemoved), population.getIndividuals().get(
					i - 1).getFitness());
		}
	}

	@Test
	public void testSelectWithNullPopulation() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		int numberRemoved = truncationSelectionAlgorithm.select(null, maxSurvivors, survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}

	@Test
	public void testSelectWithEmptyPopulation() {
		int maxSurvivors = 10;
		double survivalRate = 0.8;

		int numberRemoved = truncationSelectionAlgorithm.select(new Population(), maxSurvivors,
				survivalRate);

		assertEquals(0, numberRemoved);
		verify(logMock, times(1)).warn(anyString());
	}
}
