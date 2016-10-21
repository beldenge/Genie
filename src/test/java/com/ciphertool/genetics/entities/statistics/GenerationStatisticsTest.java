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

package com.ciphertool.genetics.entities.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

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
		Field executionStatisticsIdField = ReflectionUtils.findField(ExecutionStatistics.class, "id");
		ReflectionUtils.makeAccessible(executionStatisticsIdField);
		Field generationStatisticsIdField = ReflectionUtils.findField(GenerationStatistics.class, "id");
		ReflectionUtils.makeAccessible(generationStatisticsIdField);

		ExecutionStatistics baseExecutionStatistics = new ExecutionStatistics();
		ReflectionUtils.setField(executionStatisticsIdField, baseExecutionStatistics, new ObjectId());

		int baseGeneration = 1;
		long baseExecutionTime = 999;
		double baseBestFitness = 99.9;
		double baseAverageFitness = 49.9;
		Double baseKnownSolutionProximity = 9.9;

		ObjectId baseId = new ObjectId("1234567890abcdef12345678");
		GenerationStatistics base = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, base, baseId);
		base.setExecutionStatistics(baseExecutionStatistics);
		base.setGeneration(baseGeneration);
		base.setExecutionTime(baseExecutionTime);
		base.setBestFitness(baseBestFitness);
		base.setAverageFitness(baseAverageFitness);
		base.setKnownSolutionProximity(baseKnownSolutionProximity);

		GenerationStatistics generationStatisticsEqualToBase = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsEqualToBase, baseId);
		generationStatisticsEqualToBase.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsEqualToBase.setGeneration(baseGeneration);
		generationStatisticsEqualToBase.setExecutionTime(baseExecutionTime);
		generationStatisticsEqualToBase.setBestFitness(baseBestFitness);
		generationStatisticsEqualToBase.setAverageFitness(baseAverageFitness);
		generationStatisticsEqualToBase.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertEquals(base, generationStatisticsEqualToBase);

		GenerationStatistics generationStatisticsWithDifferentId = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentId, new ObjectId());
		generationStatisticsWithDifferentId.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentId.setGeneration(baseGeneration);
		generationStatisticsWithDifferentId.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentId.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentId.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentId.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentId));

		GenerationStatistics generationStatisticsWithDifferentExecutionStatistics = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentExecutionStatistics, baseId);
		ExecutionStatistics differentExecutionStatistics = new ExecutionStatistics();
		generationStatisticsWithDifferentExecutionStatistics.setExecutionStatistics(differentExecutionStatistics);
		generationStatisticsWithDifferentExecutionStatistics.setGeneration(baseGeneration);
		generationStatisticsWithDifferentExecutionStatistics.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentExecutionStatistics.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentExecutionStatistics.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentExecutionStatistics.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentExecutionStatistics));

		GenerationStatistics generationStatisticsWithDifferentGeneration = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentGeneration, baseId);
		generationStatisticsWithDifferentGeneration.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentGeneration.setGeneration(2);
		generationStatisticsWithDifferentGeneration.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentGeneration.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentGeneration.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentGeneration.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentGeneration));

		GenerationStatistics generationStatisticsWithDifferentExecutionTime = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentExecutionTime, baseId);
		generationStatisticsWithDifferentExecutionTime.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentExecutionTime.setGeneration(baseGeneration);
		generationStatisticsWithDifferentExecutionTime.setExecutionTime(111);
		generationStatisticsWithDifferentExecutionTime.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentExecutionTime.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentExecutionTime.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentExecutionTime));

		GenerationStatistics generationStatisticsWithDifferentBestFitness = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentBestFitness, baseId);
		generationStatisticsWithDifferentBestFitness.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentBestFitness.setGeneration(baseGeneration);
		generationStatisticsWithDifferentBestFitness.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentBestFitness.setBestFitness(199.9);
		generationStatisticsWithDifferentBestFitness.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentBestFitness.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentBestFitness));

		GenerationStatistics generationStatisticsWithDifferentAverageFitness = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentAverageFitness, baseId);
		generationStatisticsWithDifferentAverageFitness.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentAverageFitness.setGeneration(baseGeneration);
		generationStatisticsWithDifferentAverageFitness.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentAverageFitness.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentAverageFitness.setAverageFitness(149.9);
		generationStatisticsWithDifferentAverageFitness.setKnownSolutionProximity(baseKnownSolutionProximity);
		assertFalse(base.equals(generationStatisticsWithDifferentAverageFitness));

		GenerationStatistics generationStatisticsWithDifferentKnownSolutionProximity = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithDifferentKnownSolutionProximity, baseId);
		generationStatisticsWithDifferentKnownSolutionProximity.setExecutionStatistics(baseExecutionStatistics);
		generationStatisticsWithDifferentKnownSolutionProximity.setGeneration(baseGeneration);
		generationStatisticsWithDifferentKnownSolutionProximity.setExecutionTime(baseExecutionTime);
		generationStatisticsWithDifferentKnownSolutionProximity.setBestFitness(baseBestFitness);
		generationStatisticsWithDifferentKnownSolutionProximity.setAverageFitness(baseAverageFitness);
		generationStatisticsWithDifferentKnownSolutionProximity.setKnownSolutionProximity(109.9);
		assertFalse(base.equals(generationStatisticsWithDifferentKnownSolutionProximity));

		GenerationStatistics generationStatisticsWithNullPropertiesA = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithNullPropertiesA, null);
		GenerationStatistics generationStatisticsWithNullPropertiesB = new GenerationStatistics();
		ReflectionUtils.setField(generationStatisticsIdField, generationStatisticsWithNullPropertiesB, null);
		assertEquals(generationStatisticsWithNullPropertiesA, generationStatisticsWithNullPropertiesB);
	}
}
