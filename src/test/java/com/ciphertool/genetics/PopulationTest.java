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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.AscendingFitnessComparator;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockBreeder;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockFitnessEvaluator;

public class PopulationTest {
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

	@Test
	public void testEvaluateFitness() {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		MockChromosome chromosomeEvaluationNeeded1 = new MockChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNeeded2 = new MockChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNotNeeded1 = new MockChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockChromosome chromosomeEvaluationNotNeeded2 = new MockChromosome();
		chromosomeEvaluationNotNeeded2.setFitness(100.1);
		population.addIndividual(chromosomeEvaluationNotNeeded2);

		assertTrue(chromosomeEvaluationNeeded1.isEvaluationNeeded());
		assertTrue(chromosomeEvaluationNeeded2.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded1.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded2.isEvaluationNeeded());

		population.evaluateFitness(generationStatistics);

		for (Chromosome individual : population.getIndividuals()) {
			assertFalse(individual.isEvaluationNeeded());
		}

		// Only two of the individuals needed to be evaluated
		verify(fitnessEvaluatorSpy, times(2)).evaluate(any(Chromosome.class));

		/*
		 * The MockFitnessEvaluator always returns 100.0, so the total is (100.0
		 * x 2) + 5.0 + 100.1, since two individuals are re-evaluated
		 */
		Double expectedTotalFitness = new Double(305.1);

		assertEquals(expectedTotalFitness, population.getTotalFitness());
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(
				generationStatistics.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
	}

	@Test
	public void testEvaluateFitnessCompareToKnownSolution() {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		Population population = new Population();
		population.setTaskExecutor(taskExecutor);
		population.setCompareToKnownSolution(true);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		MockFitnessEvaluator knownSolutionFitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator knownSolutionFitnessEvaluatorSpy = spy(knownSolutionFitnessEvaluatorMock);
		population.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorSpy);

		MockChromosome chromosomeEvaluationNeeded1 = new MockChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNeeded2 = new MockChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockChromosome chromosomeEvaluationNotNeeded1 = new MockChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockChromosome chromosomeEvaluationNotNeeded2 = new MockChromosome();
		chromosomeEvaluationNotNeeded2.setFitness(100.1);
		population.addIndividual(chromosomeEvaluationNotNeeded2);

		assertTrue(chromosomeEvaluationNeeded1.isEvaluationNeeded());
		assertTrue(chromosomeEvaluationNeeded2.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded1.isEvaluationNeeded());
		assertFalse(chromosomeEvaluationNotNeeded2.isEvaluationNeeded());

		population.evaluateFitness(generationStatistics);

		for (Chromosome individual : population.getIndividuals()) {
			assertFalse(individual.isEvaluationNeeded());
		}

		// Only two of the individuals needed to be evaluated
		verify(fitnessEvaluatorSpy, times(2)).evaluate(any(Chromosome.class));

		/*
		 * The MockFitnessEvaluator always returns 100.0, so the total is (100.0
		 * x 2) + 5.0 + 100.1, since two individuals are re-evaluated
		 */
		Double expectedTotalFitness = new Double(305.1);

		assertEquals(expectedTotalFitness, population.getTotalFitness());
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(
				generationStatistics.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
		assertEquals(new Double(100.0), generationStatistics.getKnownSolutionProximity());
	}

	@Test
	public void testIncreaseAge() {
		Population population = new Population();
		population.setLifespan(5);

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockChromosome chromosome3 = new MockChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockChromosome chromosome4 = new MockChromosome();
		chromosome4.setAge(4);
		population.addIndividual(chromosome4);

		population.increaseAge();

		assertEquals(2, population.size());
		assertEquals(1, chromosome1.getAge());
		assertEquals(5, chromosome4.getAge());
	}

	@Test
	public void testIncreaseAgeIndefinitely() {
		Population population = new Population();
		population.setLifespan(-1);

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockChromosome chromosome3 = new MockChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockChromosome chromosome4 = new MockChromosome();
		chromosome4.setAge(4);
		population.addIndividual(chromosome4);

		population.increaseAge();

		assertEquals(4, population.size());
		assertEquals(1, chromosome1.getAge());
		assertEquals(51, chromosome2.getAge());
		assertEquals(6, chromosome3.getAge());
		assertEquals(5, chromosome4.getAge());
	}

	@Test
	public void testSelectIndex() {
		Population population = new Population();

		int indexToReturn = 7;

		Selector selector = mock(Selector.class);
		when(selector.getNextIndex(anyListOf(Chromosome.class), anyDouble())).thenReturn(
				indexToReturn);
		population.setSelector(selector);

		assertEquals(indexToReturn, population.selectIndex());
		verify(selector, times(1)).getNextIndex(anyListOf(Chromosome.class), anyDouble());
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
	public void testAddIndividual() {
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockChromosome chromosomeEvaluationNeeded = new MockChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividual(chromosomeEvaluationNeeded);

		// Validate
		fitnessSum += chromosomeEvaluationNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verify(fitnessEvaluatorSpy, times(1)).evaluate(same(chromosomeEvaluationNeeded));
		assertEquals(1, population.size());
		assertSame(chromosomeEvaluationNeeded, population.getIndividuals().get(0));

		// Add a chromosome that doesn't need evaluation
		MockChromosome chromosomeEvaluationNotNeeded = new MockChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded);

		// Validate
		fitnessSum += chromosomeEvaluationNotNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verifyNoMoreInteractions(fitnessEvaluatorSpy);
		assertEquals(2, population.size());
		assertSame(chromosomeEvaluationNotNeeded, population.getIndividuals().get(1));
	}

	@Test
	public void testRemoveIndividual() {
		Population population = new Population();

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(5.0);
		population.addIndividual(chromosome2);

		Double fitnessSum = new Double(10.0);
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(2, population.size());

		fitnessSum -= population.removeIndividual(1).getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(1, population.size());
		assertSame(chromosome1, population.getIndividuals().get(0));

		fitnessSum -= population.removeIndividual(0).getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Try to remove an individual that doesn't exist
		assertNull(population.removeIndividual(0));
	}

	@Test
	public void testClearIndividuals() {
		Population population = new Population();

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(5.0);
		population.addIndividual(chromosome2);

		assertEquals(new Double(10.0), population.getTotalFitness());
		assertEquals(2, population.size());

		population.clearIndividuals();

		assertEquals(new Double(0.0), population.getTotalFitness());
		assertEquals(0, population.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddIndividualAsIneligible() {
		Population population = new Population();

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockChromosome chromosomeEvaluationNeeded = new MockChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorSpy);
		assertEquals(0, population.size());

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);
		assertEquals(1, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNeeded, ineligibleForReproductionFromObject.get(0));

		// Add a chromosome that doesn't need evaluation
		MockChromosome chromosomeEvaluationNotNeeded = new MockChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorSpy);
		assertEquals(0, population.size());

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNotNeeded, ineligibleForReproductionFromObject.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMakeIneligibleForReproduction() {
		Population population = new Population();

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(5.0);
		population.addIndividual(chromosome2);

		Double fitnessSum = new Double(10.0);
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(2, population.size());

		fitnessSum -= chromosome1.getFitness();
		population.makeIneligibleForReproduction(0);

		// Validate individuals List
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(1, population.size());
		assertSame(chromosome2, population.getIndividuals().get(0));

		// Validate ineligible List
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);
		assertEquals(1, ineligibleForReproductionFromObject.size());
		assertSame(chromosome1, ineligibleForReproductionFromObject.get(0));

		fitnessSum -= chromosome2.getFitness();
		population.makeIneligibleForReproduction(0);

		// Validate individuals List
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Validate ineligible List
		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosome2, ineligibleForReproductionFromObject.get(1));

		// Try to remove an individual that doesn't exist
		assertNull(population.removeIndividual(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResetEligibility() {
		Population population = new Population();

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockChromosome chromosomeEvaluationNeeded = new MockChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Add a chromosome that doesn't need evaluation
		MockChromosome chromosomeEvaluationNotNeeded = new MockChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorSpy);
		assertEquals(0, population.size());

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNeeded, ineligibleForReproductionFromObject.get(0));
		assertSame(chromosomeEvaluationNotNeeded, ineligibleForReproductionFromObject.get(1));

		population.resetEligibility();

		assertEquals(0, ineligibleForReproductionFromObject.size());

		fitnessSum = chromosomeEvaluationNeeded.getFitness()
				+ chromosomeEvaluationNotNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verify(fitnessEvaluatorSpy, times(1)).evaluate(same(chromosomeEvaluationNeeded));
		assertEquals(2, population.size());
		assertSame(chromosomeEvaluationNeeded, population.getIndividuals().get(0));
		assertSame(chromosomeEvaluationNotNeeded, population.getIndividuals().get(1));
	}

	@Test
	public void testSize() {
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		// This is needed to avoid a NullPointerException on fitnessEvaluator
		MockFitnessEvaluator fitnessEvaluatorMock = new MockFitnessEvaluator();
		MockFitnessEvaluator fitnessEvaluatorSpy = spy(fitnessEvaluatorMock);
		population.setFitnessEvaluator(fitnessEvaluatorSpy);

		assertEquals(0, population.size());

		population.addIndividual(new MockChromosome());

		assertEquals(1, population.size());

		population.addIndividual(new MockChromosome());

		assertEquals(2, population.size());
	}

	@Test
	public void testSortIndividuals() {
		Population population = new Population();
		population.setFitnessComparator(new AscendingFitnessComparator());

		MockChromosome chromosome1 = new MockChromosome();
		chromosome1.setFitness(3.0);
		population.addIndividual(chromosome1);

		MockChromosome chromosome2 = new MockChromosome();
		chromosome2.setFitness(2.0);
		population.addIndividual(chromosome2);

		MockChromosome chromosome3 = new MockChromosome();
		chromosome3.setFitness(1.0);
		population.addIndividual(chromosome3);

		assertSame(chromosome1, population.getIndividuals().get(0));
		assertSame(chromosome2, population.getIndividuals().get(1));
		assertSame(chromosome3, population.getIndividuals().get(2));

		population.sortIndividuals();

		assertSame(chromosome3, population.getIndividuals().get(0));
		assertSame(chromosome2, population.getIndividuals().get(1));
		assertSame(chromosome1, population.getIndividuals().get(2));
	}
}
