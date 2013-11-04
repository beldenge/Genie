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

package com.ciphertool.genetics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.AscendingFitnessComparator;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockBreeder;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockFitnessEvaluator;

public class PopulationTest {
	private static ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

	@BeforeClass
	public static void setUp() {
		taskExecutor.setCorePoolSize(1);
		taskExecutor.setMaxPoolSize(1);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setKeepAliveSeconds(1);
		taskExecutor.setAllowCoreThreadTimeOut(true);
		taskExecutor.initialize();

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIndividualsUnmodifiable() {
		Population population = new Population();
		population.addIndividual(mock(Chromosome.class));
		population.addIndividual(mock(Chromosome.class));
		population.addIndividual(mock(Chromosome.class));

		List<Chromosome> individuals = population.getIndividuals();
		individuals.remove(0); // should throw exception
	}

	@Test
	public void getNullIndividuals() {
		Population population = new Population();
		assertNotNull(population.getIndividuals());
	}

	@Test
	public void testSetGeneticStructure() {
		Population population = new Population();

		MockBreeder mockBreeder = new MockBreeder();
		population.setBreeder(mockBreeder);

		Object geneticStructure = new Object();
		population.setGeneticStructure(geneticStructure);

		Field breederField = ReflectionUtils.findField(Population.class, "breeder");
		ReflectionUtils.makeAccessible(breederField);
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField,
				population);

		Field geneticStructureField = ReflectionUtils.findField(MockBreeder.class,
				"geneticStructure");
		ReflectionUtils.makeAccessible(geneticStructureField);
		Object geneticStructureFromObject = ReflectionUtils.getField(geneticStructureField,
				breederFromObject);

		assertSame(geneticStructure, geneticStructureFromObject);
	}

	@Test
	public void testSetBreeder() {
		Population population = new Population();

		MockBreeder mockBreeder = new MockBreeder();
		population.setBreeder(mockBreeder);

		Field breederField = ReflectionUtils.findField(Population.class, "breeder");
		ReflectionUtils.makeAccessible(breederField);
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField,
				population);

		assertSame(mockBreeder, breederFromObject);
	}

	@Test
	public void testSetFitnessEvaluator() {
		Population population = new Population();

		MockFitnessEvaluator mockFitnessEvaluator = new MockFitnessEvaluator();
		population.setFitnessEvaluator(mockFitnessEvaluator);

		Field fitnessEvaluatorField = ReflectionUtils.findField(Population.class,
				"fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		MockFitnessEvaluator fitnessEvaluatorFromObject = (MockFitnessEvaluator) ReflectionUtils
				.getField(fitnessEvaluatorField, population);

		assertSame(mockFitnessEvaluator, fitnessEvaluatorFromObject);
	}

	@Test
	public void testSetFitnessComparator() {
		Population population = new Population();

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();
		population.setFitnessComparator(ascendingFitnessComparator);

		Field fitnessComparatorField = ReflectionUtils.findField(Population.class,
				"fitnessComparator");
		ReflectionUtils.makeAccessible(fitnessComparatorField);
		AscendingFitnessComparator fitnessComparatorFromObject = (AscendingFitnessComparator) ReflectionUtils
				.getField(fitnessComparatorField, population);

		assertSame(ascendingFitnessComparator, fitnessComparatorFromObject);
	}

	@Test
	public void testSetTaskExecutor() {
		Population population = new Population();

		TaskExecutor taskExecutor = mock(TaskExecutor.class);
		population.setTaskExecutor(taskExecutor);

		Field taskExecutorField = ReflectionUtils.findField(Population.class, "taskExecutor");
		ReflectionUtils.makeAccessible(taskExecutorField);
		TaskExecutor taskExecutorFromObject = (TaskExecutor) ReflectionUtils.getField(
				taskExecutorField, population);

		assertSame(taskExecutor, taskExecutorFromObject);
	}

	@Test
	public void testSetSelector() {
		Population population = new Population();

		Selector selector = mock(Selector.class);
		population.setSelector(selector);

		Field selectorField = ReflectionUtils.findField(Population.class, "selector");
		ReflectionUtils.makeAccessible(selectorField);
		Selector selectorFromObject = (Selector) ReflectionUtils
				.getField(selectorField, population);

		assertSame(selector, selectorFromObject);
	}

	@Test
	public void testSetLifespan() {
		Population population = new Population();

		int lifespanToSet = 25;
		population.setLifespan(lifespanToSet);

		Field lifespanField = ReflectionUtils.findField(Population.class, "lifespan");
		ReflectionUtils.makeAccessible(lifespanField);
		int lifespanFromObject = (int) ReflectionUtils.getField(lifespanField, population);

		assertSame(lifespanToSet, lifespanFromObject);
	}

	@Test
	public void testSetKnownSolutionFitnessEvaluator() {
		Population population = new Population();

		MockFitnessEvaluator mockKnownSolutionFitnessEvaluator = new MockFitnessEvaluator();
		population.setKnownSolutionFitnessEvaluator(mockKnownSolutionFitnessEvaluator);

		Field knownSolutionFitnessEvaluatorField = ReflectionUtils.findField(Population.class,
				"knownSolutionFitnessEvaluator");
		ReflectionUtils.makeAccessible(knownSolutionFitnessEvaluatorField);
		MockFitnessEvaluator knownSolutionFitnessEvaluatorFromObject = (MockFitnessEvaluator) ReflectionUtils
				.getField(knownSolutionFitnessEvaluatorField, population);

		assertSame(mockKnownSolutionFitnessEvaluator, knownSolutionFitnessEvaluatorFromObject);
	}

	@Test
	public void testSetCompareToKnownSolution() {
		Population population = new Population();

		Boolean compareToKnownSolution = true;
		population.setCompareToKnownSolution(compareToKnownSolution);

		Field compareToKnownSolutionField = ReflectionUtils.findField(Population.class,
				"compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(
				compareToKnownSolutionField, population);

		assertSame(compareToKnownSolution, compareToKnownSolutionFromObject);
	}

	@Test
	public void testSetCompareToKnownSolutionDefault() {
		Population population = new Population();

		Field compareToKnownSolutionField = ReflectionUtils.findField(Population.class,
				"compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(
				compareToKnownSolutionField, population);

		assertEquals(false, compareToKnownSolutionFromObject);
	}

	@Test
	public void testGeneratorTask() {
		Population population = new Population();
		Population.GeneratorTask generatorTask = population.new GeneratorTask();

		MockChromosome chromosomeToReturn = new MockChromosome();
		Breeder mockBreeder = mock(Breeder.class);
		when(mockBreeder.breed()).thenReturn(chromosomeToReturn);
		population.setBreeder(mockBreeder);

		Chromosome chromosomeReturned = null;
		try {
			chromosomeReturned = generatorTask.call();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertSame(chromosomeToReturn, chromosomeReturned);
	}

	@Test
	public void testBreed() {
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);
		assertEquals(0, population.size());

		int expectedPopulationSize = 10;
		List<MockChromosome> individuals = new ArrayList<MockChromosome>();
		for (int i = 0; i < expectedPopulationSize; i++) {
			individuals.add(new MockChromosome());
		}

		Breeder breederMock = mock(Breeder.class);
		when(breederMock.breed()).thenReturn(individuals.get(0), individuals.get(1),
				individuals.get(2), individuals.get(3), individuals.get(4), individuals.get(5),
				individuals.get(6), individuals.get(7), individuals.get(8), individuals.get(9));
		population.setBreeder(breederMock);

		population.breed(expectedPopulationSize);

		assertEquals(expectedPopulationSize, population.size());

		for (int i = 0; i < expectedPopulationSize; i++) {
			assertSame(individuals.get(i), population.getIndividuals().get(i));
		}
	}

	@Test
	public void testEvaluatorTask() {
		Population population = new Population();
		MockChromosome chromosomeToEvaluate = new MockChromosome();

		FitnessEvaluator mockEvaluator = mock(FitnessEvaluator.class);
		Double fitnessToReturn = new Double(101.0);
		when(mockEvaluator.evaluate(same(chromosomeToEvaluate))).thenReturn(fitnessToReturn);

		Population.EvaluatorTask evaluatorTask = population.new EvaluatorTask(chromosomeToEvaluate);
		population.setFitnessEvaluator(mockEvaluator);

		Double fitnessReturned = null;
		try {
			fitnessReturned = evaluatorTask.call();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertSame(fitnessToReturn, fitnessReturned);
	}

	@Test
	public void testDoConcurrentFitnessEvaluations() {
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		MockChromosome chromosomeEvaluationNeeded1 = new MockChromosome();
		chromosomeEvaluationNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNeeded2 = new MockChromosome();
		chromosomeEvaluationNeeded2.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNotNeeded1 = new MockChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockChromosome chromosomeEvaluationNotNeeded2 = new MockChromosome();
		chromosomeEvaluationNotNeeded2.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNotNeeded2);

		assertTrue(chromosomeEvaluationNeeded1.isEvaluationNeeded());
		assertTrue(chromosomeEvaluationNeeded2.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded1.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded2.isEvaluationNeeded());

		population.doConcurrentFitnessEvaluations();

		for (Chromosome individual : population.getIndividuals()) {
			assertFalse(individual.isEvaluationNeeded());
		}

		// Only two of the individuals needed to be evaluated
		verify(fitnessEvaluatorSpy, times(2)).evaluate(any(Chromosome.class));
	}
}
