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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class ExecutionStatisticsTest {
	@Test
	public void testConstructor() {
		Date startDateToSet = new Date();
		GeneticAlgorithmStrategy strategy = createGeneticAlgorithmStrategy();

		ExecutionStatistics executionStatistics = new ExecutionStatistics(startDateToSet, strategy);

		assertSame(startDateToSet, executionStatistics.getStartDateTime());
		assertSame(strategy.getPopulationSize(), executionStatistics.getPopulationSize());
		assertSame(strategy.getMutationRate(), executionStatistics.getMutationRate());
		assertEquals(strategy.getCrossoverAlgorithm().getClass().getSimpleName(), executionStatistics.getCrossoverAlgorithm());
		assertEquals(strategy.getFitnessEvaluator().getClass().getSimpleName(), executionStatistics.getFitnessEvaluator());
		assertEquals(strategy.getMutationAlgorithm().getClass().getSimpleName(), executionStatistics.getMutationAlgorithm());

		// Test that we don't run into NPE if the complex arguments are null
		strategy.setCrossoverAlgorithm(null);
		strategy.setFitnessEvaluator(null);
		strategy.setMutationAlgorithm(null);
		executionStatistics = new ExecutionStatistics(startDateToSet, strategy);
		assertNull(executionStatistics.getCrossoverAlgorithm());
		assertNull(executionStatistics.getFitnessEvaluator());
		assertNull(executionStatistics.getMutationAlgorithm());
	}

	@Test
	public void testSetStartDate() {
		Date startDateToSet = new Date();
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setStartDateTime(startDateToSet);

		assertSame(startDateToSet, executionStatistics.getStartDateTime());
	}

	@Test
	public void testSetEndDate() {
		Date endDateToSet = new Date();
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setEndDateTime(endDateToSet);

		assertSame(endDateToSet, executionStatistics.getEndDateTime());
	}

	@Test
	public void testSetPopulationSize() {
		Integer populationSizeToSet = 1000;
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setPopulationSize(populationSizeToSet);

		assertSame(populationSizeToSet, executionStatistics.getPopulationSize());
	}

	@Test
	public void testSetMutationRate() {
		Double mutationRateToSet = 0.05;
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setMutationRate(mutationRateToSet);

		assertSame(mutationRateToSet, executionStatistics.getMutationRate());
	}

	@Test
	public void testSetCrossoverAlgorithm() {
		String crossoverAlgorithmToSet = mock(CrossoverAlgorithm.class).getClass().getSimpleName();
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setCrossoverAlgorithm(crossoverAlgorithmToSet);

		assertSame(crossoverAlgorithmToSet, executionStatistics.getCrossoverAlgorithm());
	}

	@Test
	public void testSetFitnessEvaluator() {
		String fitnessEvaluatorToSet = mock(FitnessEvaluator.class).getClass().getSimpleName();
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setFitnessEvaluator(fitnessEvaluatorToSet);

		assertSame(fitnessEvaluatorToSet, executionStatistics.getFitnessEvaluator());
	}

	@Test
	public void testSetMutationAlgorithm() {
		String mutationAlgorithmToSet = mock(MutationAlgorithm.class).getClass().getSimpleName();
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.setMutationAlgorithm(mutationAlgorithmToSet);

		assertSame(mutationAlgorithmToSet, executionStatistics.getMutationAlgorithm());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGenerationStatisticsListUnmodifiable() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		executionStatistics.addGenerationStatistics(new GenerationStatistics());
		executionStatistics.addGenerationStatistics(new GenerationStatistics());
		executionStatistics.addGenerationStatistics(new GenerationStatistics());

		List<GenerationStatistics> generationStatisticsList = executionStatistics.getGenerationStatisticsList();
		generationStatisticsList.remove(0); // should throw exception
	}

	@Test
	public void getNullGenerationStatisticsList() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		assertNotNull(executionStatistics.getGenerationStatisticsList());
	}

	@Test
	public void addGenerationStatistics() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		assertEquals(0, executionStatistics.getGenerationStatisticsList().size());

		GenerationStatistics generationStatistics1 = new GenerationStatistics();
		executionStatistics.addGenerationStatistics(generationStatistics1);
		GenerationStatistics generationStatistics2 = new GenerationStatistics();
		executionStatistics.addGenerationStatistics(generationStatistics2);
		GenerationStatistics generationStatistics3 = new GenerationStatistics();
		executionStatistics.addGenerationStatistics(generationStatistics3);

		assertEquals(3, executionStatistics.getGenerationStatisticsList().size());
		assertSame(generationStatistics1, executionStatistics.getGenerationStatisticsList().get(0));
		assertSame(generationStatistics2, executionStatistics.getGenerationStatisticsList().get(1));
		assertSame(generationStatistics3, executionStatistics.getGenerationStatisticsList().get(2));
	}

	@Test
	public void removeGenerationStatistics() {
		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		assertEquals(0, executionStatistics.getGenerationStatisticsList().size());

		GenerationStatistics generationStatistics1 = new GenerationStatistics(null, 0);
		executionStatistics.addGenerationStatistics(generationStatistics1);
		GenerationStatistics generationStatistics2 = new GenerationStatistics(null, 1);
		executionStatistics.addGenerationStatistics(generationStatistics2);
		GenerationStatistics generationStatistics3 = new GenerationStatistics(null, 2);
		executionStatistics.addGenerationStatistics(generationStatistics3);

		assertEquals(3, executionStatistics.getGenerationStatisticsList().size());

		executionStatistics.removeGenerationStatistics(generationStatistics2);

		assertEquals(2, executionStatistics.getGenerationStatisticsList().size());
		assertSame(generationStatistics1, executionStatistics.getGenerationStatisticsList().get(0));
		assertSame(generationStatistics3, executionStatistics.getGenerationStatisticsList().get(1));
	}

	@Test
	public void testEquals() {
		Date baseStartDate = new Date();
		GeneticAlgorithmStrategy baseStrategy = createGeneticAlgorithmStrategy();

		ExecutionStatistics base = new ExecutionStatistics(baseStartDate, baseStrategy);

		ExecutionStatistics executionStatisticsEqualToBase = new ExecutionStatistics(baseStartDate, baseStrategy);
		Field idField = ReflectionUtils.findField(ExecutionStatistics.class, "id");
		ReflectionUtils.makeAccessible(idField);
		ReflectionUtils.setField(idField, executionStatisticsEqualToBase, base.getId());
		assertEquals(base, executionStatisticsEqualToBase);

		ExecutionStatistics executionStatisticsWithDifferentId = new ExecutionStatistics(baseStartDate, baseStrategy);
		ReflectionUtils.setField(idField, executionStatisticsWithDifferentId, new ObjectId());
		assertFalse(base.equals(executionStatisticsWithDifferentId));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		Date differentStartDate = cal.getTime();
		ExecutionStatistics executionStatisticsWithDifferentStartDate = new ExecutionStatistics(differentStartDate,
				baseStrategy);
		assertFalse(base.equals(executionStatisticsWithDifferentStartDate));

		ExecutionStatistics executionStatisticsWithDifferentPopulationSize = new ExecutionStatistics(baseStartDate,
				baseStrategy);
		executionStatisticsWithDifferentPopulationSize.setPopulationSize(999);
		assertFalse(base.equals(executionStatisticsWithDifferentPopulationSize));

		ExecutionStatistics executionStatisticsWithDifferentMutationRate = new ExecutionStatistics(baseStartDate,
				baseStrategy);
		executionStatisticsWithDifferentMutationRate.setMutationRate(1.0);
		assertFalse(base.equals(executionStatisticsWithDifferentMutationRate));

		ExecutionStatistics executionStatisticsWithDifferentCrossoverAlgorithm = new ExecutionStatistics(baseStartDate,
				baseStrategy);
		executionStatisticsWithDifferentCrossoverAlgorithm.setCrossoverAlgorithm("differentCrossoverAlgorithm");
		assertFalse(base.equals(executionStatisticsWithDifferentCrossoverAlgorithm));

		ExecutionStatistics executionStatisticsWithDifferentFitnessEvaluator = new ExecutionStatistics(baseStartDate,
				baseStrategy);
		executionStatisticsWithDifferentFitnessEvaluator.setFitnessEvaluator("differentFitnessEvaluator");
		assertFalse(base.equals(executionStatisticsWithDifferentFitnessEvaluator));

		ExecutionStatistics executionStatisticsWithDifferentMutationAlgorithm = new ExecutionStatistics(baseStartDate,
				baseStrategy);
		executionStatisticsWithDifferentMutationAlgorithm.setMutationAlgorithm("differentMutationAlgorithm");
		assertFalse(base.equals(executionStatisticsWithDifferentMutationAlgorithm));

		ExecutionStatistics executionStatisticsWithNullPropertiesA = new ExecutionStatistics();
		ReflectionUtils.setField(idField, executionStatisticsWithNullPropertiesA, null);
		ExecutionStatistics executionStatisticsWithNullPropertiesB = new ExecutionStatistics();
		ReflectionUtils.setField(idField, executionStatisticsWithNullPropertiesB, null);
		assertEquals(executionStatisticsWithNullPropertiesA, executionStatisticsWithNullPropertiesB);
	}

	@SuppressWarnings("rawtypes")
	private static GeneticAlgorithmStrategy createGeneticAlgorithmStrategy() {
		Integer populationSizeToSet = 1000;
		Double mutationRateToSet = 0.05;
		CrossoverAlgorithm crossoverAlgorithmToSet = mock(CrossoverAlgorithm.class);
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		GeneticAlgorithmStrategy strategy = new GeneticAlgorithmStrategy();
		strategy.setPopulationSize(populationSizeToSet);
		strategy.setMutationRate(mutationRateToSet);
		strategy.setCrossoverAlgorithm(crossoverAlgorithmToSet);
		strategy.setFitnessEvaluator(fitnessEvaluatorToSet);
		strategy.setMutationAlgorithm(mutationAlgorithmToSet);

		return strategy;
	}
}
