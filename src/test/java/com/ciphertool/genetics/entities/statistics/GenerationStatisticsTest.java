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

package com.ciphertool.genetics.entities.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class GenerationStatisticsTest {
	private static final double TOLERANCE = 0.00001;

	@Test
	public void testConstructor() {
		ExecutionStatistics executionStatisticsToSet = new ExecutionStatistics();
		int generationToSet = 1;

		GenerationStatistics generationStatistics = new GenerationStatistics(executionStatisticsToSet, generationToSet);

		assertSame(executionStatisticsToSet, generationStatistics.getExecutionStatistics());
		assertEquals(generationToSet, generationStatistics.getGeneration());
	}

	@Test
	public void testSetId() {
		Integer idToSet = 123;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setId(idToSet);

		assertSame(idToSet, generationStatistics.getId());
	}

	@Test
	public void testSetExecutionStatistics() {
		ExecutionStatistics executionStatisticsToSet = new ExecutionStatistics();
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setExecutionStatistics(executionStatisticsToSet);

		assertSame(executionStatisticsToSet, generationStatistics.getExecutionStatistics());
	}

	@Test
	public void testSetGeneration() {
		int generationToSet = 1;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setGeneration(generationToSet);

		assertEquals(generationToSet, generationStatistics.getGeneration());
	}

	@Test
	public void testSetExecutionTime() {
		long executionTimeToSet = 999;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setExecutionTime(executionTimeToSet);

		assertEquals(executionTimeToSet, generationStatistics.getExecutionTime());
	}

	@Test
	public void testSetBestFitness() {
		double bestFitnessToSet = 99.9;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setBestFitness(bestFitnessToSet);

		assertEquals(bestFitnessToSet, generationStatistics.getBestFitness(), TOLERANCE);
	}

	@Test
	public void testSetAverageFitness() {
		double averageFitnessToSet = 49.9;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setAverageFitness(averageFitnessToSet);

		assertEquals(averageFitnessToSet, generationStatistics.getAverageFitness(), TOLERANCE);
	}

	@Test
	public void testSetKnownSolutionProximity() {
		Double knownSolutionProximityToSet = 9.9;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setKnownSolutionProximity(knownSolutionProximityToSet);

		assertSame(knownSolutionProximityToSet, generationStatistics.getKnownSolutionProximity());
	}

	@Test
	public void testSetNumberOfMutations() {
		int numberOfMutationsToSet = 5;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setNumberOfMutations(numberOfMutationsToSet);

		assertEquals(numberOfMutationsToSet, generationStatistics.getNumberOfMutations());
	}

	@Test
	public void testSetNumberOfCrossovers() {
		int numberOfCrossoversToSet = 10;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setNumberOfCrossovers(numberOfCrossoversToSet);

		assertEquals(numberOfCrossoversToSet, generationStatistics.getNumberOfCrossovers());
	}

	@Test
	public void testSetNumberRandomlyGenerated() {
		int numberRandomlyGeneratedToSet = 15;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setNumberRandomlyGenerated(numberRandomlyGeneratedToSet);

		assertEquals(numberRandomlyGeneratedToSet, generationStatistics.getNumberRandomlyGenerated());
	}

	@Test
	public void testSetNumberSelectedOut() {
		int numberSelectedOutToSet = 20;
		GenerationStatistics generationStatistics = new GenerationStatistics();
		generationStatistics.setNumberSelectedOut(numberSelectedOutToSet);

		assertEquals(numberSelectedOutToSet, generationStatistics.getNumberSelectedOut());
	}

	@Test
	public void testEquals() {
		Integer baseId = 123;
		ExecutionStatistics baseExecutionStatistics = new ExecutionStatistics();
		baseExecutionStatistics.setId(1);
		int baseGeneration = 1;
		long baseExecutionTime = 999;
		double baseBestFitness = 99.9;
		double baseAverageFitness = 49.9;
		Double baseKnownSolutionProximity = 9.9;

		GenerationStatistics base = new GenerationStatistics();
		base.setId(baseId);
		base.setExecutionStatistics(baseExecutionStatistics);
		base.setGeneration(baseGeneration);
		base.setExecutionTime(baseExecutionTime);
		base.setBestFitness(baseBestFitness);
		base.setAverageFitness(baseAverageFitness);
		base.setKnownSolutionProximity(baseKnownSolutionProximity);

		GenerationStatistics generationStatisticsEqualToBase = new GenerationStatistics();
		generationStatisticsEqualToBase.setId(baseId);
		generationStatisticsEqualToBase.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsEqualToBase.setGeneration(baseGeneration);
		generationStatisticsEqualToBase.setExecutionTime(baseExecutionTime);
		generationStatisticsEqualToBase.setBestFitness(baseBestFitness);
		generationStatisticsEqualToBase.setAverageFitness(baseAverageFitness);
		generationStatisticsEqualToBase.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertEquals(base, generationStatisticsEqualToBase);

		GenerationStatistics generationStatisticsWithDifferentId = new GenerationStatistics();
		generationStatisticsWithDifferentId.setId(54321);
		generationStatisticsWithDifferentId.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentId.setGeneration(baseGeneration);
		generationStatisticsWithDifferentId.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentId.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentId.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentId.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentId));

		GenerationStatistics generationStatisticsWithDifferentExecutionStatistics = new GenerationStatistics();
		generationStatisticsWithDifferentExecutionStatistics.setId(baseId);
		ExecutionStatistics differentExecutionStatistics = new ExecutionStatistics();
		differentExecutionStatistics.setId(2);
		generationStatisticsWithDifferentExecutionStatistics.setExecutionStatistics(differentExecutionStatistics);
		generationStatisticsWithDifferentExecutionStatistics.setGeneration(baseGeneration);
		generationStatisticsWithDifferentExecutionStatistics.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentExecutionStatistics.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentExecutionStatistics.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentExecutionStatistics.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentExecutionStatistics));

		GenerationStatistics generationStatisticsWithDifferentGeneration = new GenerationStatistics();
		generationStatisticsWithDifferentGeneration.setId(baseId);
		generationStatisticsWithDifferentGeneration.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentGeneration.setGeneration(2);
		generationStatisticsWithDifferentGeneration.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentGeneration.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentGeneration.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentGeneration.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentGeneration));

		GenerationStatistics generationStatisticsWithDifferentExecutionTime = new GenerationStatistics();
		generationStatisticsWithDifferentExecutionTime.setId(baseId);
		generationStatisticsWithDifferentExecutionTime.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentExecutionTime.setGeneration(baseGeneration);
		generationStatisticsWithDifferentExecutionTime.setExecutionTime(111);
		generationStatisticsWithDifferentExecutionTime.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentExecutionTime.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentExecutionTime.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentExecutionTime));

		GenerationStatistics generationStatisticsWithDifferentBestFitness = new GenerationStatistics();
		generationStatisticsWithDifferentBestFitness.setId(baseId);
		generationStatisticsWithDifferentBestFitness.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentBestFitness.setGeneration(baseGeneration);
		generationStatisticsWithDifferentBestFitness.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentBestFitness.setBestFitness(199.9);
		generationStatisticsWithDifferentBestFitness.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentBestFitness.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentBestFitness));

		GenerationStatistics generationStatisticsWithDifferentAverageFitness = new GenerationStatistics();
		generationStatisticsWithDifferentAverageFitness.setId(baseId);
		generationStatisticsWithDifferentAverageFitness.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentAverageFitness.setGeneration(baseGeneration);
		generationStatisticsWithDifferentAverageFitness.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentAverageFitness.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentAverageFitness.setAverageFitness(149.9);
		generationStatisticsWithDifferentAverageFitness.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentAverageFitness));

		GenerationStatistics generationStatisticsWithDifferentKnownSolutionProximity = new GenerationStatistics();
		generationStatisticsWithDifferentKnownSolutionProximity.setId(baseId);
		generationStatisticsWithDifferentKnownSolutionProximity.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentKnownSolutionProximity.setGeneration(baseGeneration);
		generationStatisticsWithDifferentKnownSolutionProximity.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentKnownSolutionProximity.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentKnownSolutionProximity.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentKnownSolutionProximity.setKnownSolutionProximity(109.9);
		assertFalse(base.equals(generationStatisticsWithDifferentKnownSolutionProximity));

		GenerationStatistics generationStatisticsWithNullPropertiesA = new GenerationStatistics();
		GenerationStatistics generationStatisticsWithNullPropertiesB = new GenerationStatistics();
		assertEquals(generationStatisticsWithNullPropertiesA, generationStatisticsWithNullPropertiesB);
	}
}
