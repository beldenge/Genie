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
import com.ciphertool.genetics.StandardPopulation;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class ConcurrentMultigenerationalGeneticAlgorithmTest {
	private static final double				DEFAULT_FITNESS_VALUE	= 100.0;

	private static ThreadPoolTaskExecutor	taskExecutor			= new ThreadPoolTaskExecutor();

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

		ConcurrentMultigenerationalGeneticAlgorithm concurrentMultigenerationalGeneticAlgorithm = new ConcurrentMultigenerationalGeneticAlgorithm();
		concurrentMultigenerationalGeneticAlgorithm.setTaskExecutor(taskExecutorToSet);

		Field taskExecutorField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "taskExecutor");
		ReflectionUtils.makeAccessible(taskExecutorField);
		TaskExecutor taskExecutorFromObject = (TaskExecutor) ReflectionUtils.getField(taskExecutorField, concurrentMultigenerationalGeneticAlgorithm);

		assertSame(taskExecutorToSet, taskExecutorFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCrossoverTask() {
		Chromosome mom = new MockKeylessChromosome();
		Chromosome dad = new MockKeylessChromosome();

		ConcurrentMultigenerationalGeneticAlgorithm concurrentMultigenerationalGeneticAlgorithm = new ConcurrentMultigenerationalGeneticAlgorithm();
		ConcurrentMultigenerationalGeneticAlgorithm.CrossoverTask generatorTask = concurrentMultigenerationalGeneticAlgorithm.new CrossoverTask(
				mom, dad);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentMultigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(same(mom), same(dad))).thenReturn(Arrays.asList(chromosomeToReturn));

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testCrossover() {
		ConcurrentMultigenerationalGeneticAlgorithm concurrentMultigenerationalGeneticAlgorithm = new ConcurrentMultigenerationalGeneticAlgorithm();
		concurrentMultigenerationalGeneticAlgorithm.setTaskExecutor(taskExecutor);

		StandardPopulation population = new StandardPopulation();
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Selector selectorMock = mock(Selector.class);
		population.setSelector(selectorMock);
		when(selectorMock.getNextIndex(anyListOf(Chromosome.class), any(Double.class))).thenReturn(0, 1, 2, 3, 4);

		int initialPopulationSize = 50;

		for (int i = 0; i < initialPopulationSize; i++) {
			population.addIndividual(new MockKeylessChromosome());
		}

		concurrentMultigenerationalGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentMultigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class))).thenReturn(Arrays.asList(chromosomeToReturn));

		GeneticAlgorithmStrategy strategy = new GeneticAlgorithmStrategy();
		strategy.setCrossoverRate(0.1);

		Field strategyField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, concurrentMultigenerationalGeneticAlgorithm, strategy);

		Field ineligibleForReproductionField = ReflectionUtils.findField(StandardPopulation.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = concurrentMultigenerationalGeneticAlgorithm.crossover(initialPopulationSize);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);

		/*
		 * The population size should be reduced by the number of parents used during crossover.
		 */
		assertEquals(40, population.size());

		assertEquals(5, childrenProduced);

		// There should be 10 ineligible parents, along with the 5 children
		assertEquals(15, ineligibleForReproductionFromObject.size());

		verify(selectorMock, times(10)).getNextIndex(anyListOf(Chromosome.class), any(Double.class));

		verify(crossoverAlgorithmMock, times(5)).crossover(any(Chromosome.class), any(Chromosome.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testCrossover_SmallPopulation() {
		ConcurrentMultigenerationalGeneticAlgorithm concurrentMultigenerationalGeneticAlgorithm = new ConcurrentMultigenerationalGeneticAlgorithm();

		StandardPopulation population = new StandardPopulation();

		Chromosome chromosome = new MockKeylessChromosome();
		population.addIndividual(chromosome);
		concurrentMultigenerationalGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentMultigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Field ineligibleForReproductionField = ReflectionUtils.findField(StandardPopulation.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = concurrentMultigenerationalGeneticAlgorithm.crossover(10);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);

		assertEquals(1, population.size());

		assertEquals(0, ineligibleForReproductionFromObject.size());

		assertEquals(0, childrenProduced);

		verifyZeroInteractions(crossoverAlgorithmMock);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testDoConcurrentCrossovers() {
		ConcurrentMultigenerationalGeneticAlgorithm concurrentMultigenerationalGeneticAlgorithm = new ConcurrentMultigenerationalGeneticAlgorithm();
		concurrentMultigenerationalGeneticAlgorithm.setTaskExecutor(taskExecutor);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(ConcurrentMultigenerationalGeneticAlgorithm.class, "crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, concurrentMultigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class))).thenReturn(Arrays.asList(chromosomeToReturn));

		long pairsToCrossover = 5;
		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		for (int i = 0; i < 5; i++) {
			moms.add(new MockKeylessChromosome());
			dads.add(new MockKeylessChromosome());
		}

		List<Chromosome> childrenReturned = concurrentMultigenerationalGeneticAlgorithm.doConcurrentCrossovers(pairsToCrossover, moms, dads);

		assertEquals(5, childrenReturned.size());
		for (Chromosome child : childrenReturned) {
			assertSame(chromosomeToReturn, child);
		}

		verify(crossoverAlgorithmMock, times(5)).crossover(any(Chromosome.class), any(Chromosome.class));
	}
}
