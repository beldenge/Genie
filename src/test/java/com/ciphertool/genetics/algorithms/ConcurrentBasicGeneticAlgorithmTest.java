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

package com.ciphertool.genetics.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockChromosome;

public class ConcurrentBasicGeneticAlgorithmTest {
	private static final double DEFAULT_FITNESS_VALUE = 100.0;

	private static ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

	@BeforeClass
	public static void setUp() {
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setKeepAliveSeconds(1);
		taskExecutor.setAllowCoreThreadTimeOut(true);
		taskExecutor.initialize();
	}

	@Test
	public void testSetTaskExecutor() {
		TaskExecutor taskExecutorToSet = mock(TaskExecutor.class);

		ConcurrentBasicGeneticAlgorithm concurrentBasicGeneticAlgorithm = new ConcurrentBasicGeneticAlgorithm();
		concurrentBasicGeneticAlgorithm.setTaskExecutor(taskExecutorToSet);

		Field taskExecutorField = ReflectionUtils.findField(ConcurrentBasicGeneticAlgorithm.class,
				"taskExecutor");
		ReflectionUtils.makeAccessible(taskExecutorField);
		TaskExecutor taskExecutorFromObject = (TaskExecutor) ReflectionUtils.getField(
				taskExecutorField, concurrentBasicGeneticAlgorithm);

		assertSame(taskExecutorToSet, taskExecutorFromObject);
	}

	@Test
	public void testCrossoverTask() {
		Chromosome mom = new MockChromosome();
		Chromosome dad = new MockChromosome();

		ConcurrentBasicGeneticAlgorithm concurrentBasicGeneticAlgorithm = new ConcurrentBasicGeneticAlgorithm();
		ConcurrentBasicGeneticAlgorithm.CrossoverTask generatorTask = concurrentBasicGeneticAlgorithm.new CrossoverTask(
				mom, dad);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(
				ConcurrentBasicGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentBasicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockChromosome();
		when(crossoverAlgorithmMock.crossover(same(mom), same(dad))).thenReturn(
				Arrays.asList(chromosomeToReturn));

		List<Chromosome> chromosomesReturned = null;
		try {
			chromosomesReturned = generatorTask.call();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals(1, chromosomesReturned.size());
		assertSame(chromosomeToReturn, chromosomesReturned.get(0));
		verify(crossoverAlgorithmMock, times(1)).crossover(same(mom), same(dad));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCrossover() {
		ConcurrentBasicGeneticAlgorithm concurrentBasicGeneticAlgorithm = new ConcurrentBasicGeneticAlgorithm();
		concurrentBasicGeneticAlgorithm.setTaskExecutor(taskExecutor);

		Population population = new Population();
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class)))
				.thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Selector selectorMock = mock(Selector.class);
		population.setSelector(selectorMock);
		when(selectorMock.getNextIndex(anyListOf(Chromosome.class), any(Double.class))).thenReturn(
				0, 1, 2, 3, 4);

		int initialPopulationSize = 50;

		for (int i = 0; i < initialPopulationSize; i++) {
			population.addIndividual(new MockChromosome());
		}

		concurrentBasicGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(
				ConcurrentBasicGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentBasicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class)))
				.thenReturn(Arrays.asList(chromosomeToReturn));

		GeneticAlgorithmStrategy strategy = new GeneticAlgorithmStrategy();
		strategy.setCrossoverRate(0.1);

		Field strategyField = ReflectionUtils.findField(ConcurrentBasicGeneticAlgorithm.class,
				"strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, concurrentBasicGeneticAlgorithm, strategy);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = concurrentBasicGeneticAlgorithm.crossover(initialPopulationSize);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		/*
		 * The population size should be reduced by the number of parents used
		 * during crossover.
		 */
		assertEquals(40, population.size());

		assertEquals(5, childrenProduced);

		// There should be 10 ineligible parents, along with the 5 children
		assertEquals(15, ineligibleForReproductionFromObject.size());

		verify(selectorMock, times(10))
				.getNextIndex(anyListOf(Chromosome.class), any(Double.class));

		verify(crossoverAlgorithmMock, times(5)).crossover(any(Chromosome.class),
				any(Chromosome.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCrossover_SmallPopulation() {
		ConcurrentBasicGeneticAlgorithm concurrentBasicGeneticAlgorithm = new ConcurrentBasicGeneticAlgorithm();

		Population population = new Population();

		Chromosome chromosome = new MockChromosome();
		population.addIndividual(chromosome);
		concurrentBasicGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(
				ConcurrentBasicGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentBasicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = concurrentBasicGeneticAlgorithm.crossover(10);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(1, population.size());

		assertEquals(0, ineligibleForReproductionFromObject.size());

		assertEquals(0, childrenProduced);

		verifyZeroInteractions(crossoverAlgorithmMock);
	}

	@Test
	public void testDoConcurrentCrossovers() {
		ConcurrentBasicGeneticAlgorithm concurrentBasicGeneticAlgorithm = new ConcurrentBasicGeneticAlgorithm();
		concurrentBasicGeneticAlgorithm.setTaskExecutor(taskExecutor);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(
				ConcurrentBasicGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentBasicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class)))
				.thenReturn(Arrays.asList(chromosomeToReturn));

		long pairsToCrossover = 5;
		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		for (int i = 0; i < 5; i++) {
			moms.add(new MockChromosome());
			dads.add(new MockChromosome());
		}

		List<Chromosome> childrenReturned = concurrentBasicGeneticAlgorithm.doConcurrentCrossovers(
				pairsToCrossover, moms, dads);

		assertEquals(5, childrenReturned.size());
		for (Chromosome child : childrenReturned) {
			assertSame(chromosomeToReturn, child);
		}

		verify(crossoverAlgorithmMock, times(5)).crossover(any(Chromosome.class),
				any(Chromosome.class));
	}
}
