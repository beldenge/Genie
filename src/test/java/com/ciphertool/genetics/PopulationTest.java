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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
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
import com.ciphertool.genetics.mocks.MockKeyedChromosome;

public class PopulationTest {
	private static ThreadPoolTaskExecutor	taskExecutor			= new ThreadPoolTaskExecutor();
	private static final double				DEFAULT_FITNESS_VALUE	= 100.0;

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
		StandardPopulation population = new StandardPopulation();

		MockBreeder mockBreeder = new MockBreeder();
		population.setBreeder(mockBreeder);

		Object geneticStructure = new Object();
		population.setGeneticStructure(geneticStructure);

		Field breederField = ReflectionUtils.findField(StandardPopulation.class, "breeder");
		ReflectionUtils.makeAccessible(breederField);
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField, population);

		Field geneticStructureField = ReflectionUtils.findField(MockBreeder.class, "geneticStructure");
		ReflectionUtils.makeAccessible(geneticStructureField);
		Object geneticStructureFromObject = ReflectionUtils.getField(geneticStructureField, breederFromObject);

		assertSame(geneticStructure, geneticStructureFromObject);
	}

	@Test
	public void testSetBreeder() {
		StandardPopulation population = new StandardPopulation();

		MockBreeder mockBreeder = new MockBreeder();
		population.setBreeder(mockBreeder);

		Field breederField = ReflectionUtils.findField(StandardPopulation.class, "breeder");
		ReflectionUtils.makeAccessible(breederField);
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField, population);

		assertSame(mockBreeder, breederFromObject);
	}

	@Test
	public void testSetFitnessEvaluator() {
		StandardPopulation population = new StandardPopulation();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Field fitnessEvaluatorField = ReflectionUtils.findField(StandardPopulation.class, "fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		FitnessEvaluator fitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(fitnessEvaluatorField, population);

		assertSame(fitnessEvaluatorMock, fitnessEvaluatorFromObject);
	}

	@Test
	public void testSetFitnessComparator() {
		StandardPopulation population = new StandardPopulation();

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();
		population.setFitnessComparator(ascendingFitnessComparator);

		Field fitnessComparatorField = ReflectionUtils.findField(StandardPopulation.class, "fitnessComparator");
		ReflectionUtils.makeAccessible(fitnessComparatorField);
		AscendingFitnessComparator fitnessComparatorFromObject = (AscendingFitnessComparator) ReflectionUtils.getField(fitnessComparatorField, population);

		assertSame(ascendingFitnessComparator, fitnessComparatorFromObject);
	}

	@Test
	public void testSetTaskExecutor() {
		StandardPopulation population = new StandardPopulation();

		TaskExecutor taskExecutor = mock(TaskExecutor.class);
		population.setTaskExecutor(taskExecutor);

		Field taskExecutorField = ReflectionUtils.findField(StandardPopulation.class, "taskExecutor");
		ReflectionUtils.makeAccessible(taskExecutorField);
		TaskExecutor taskExecutorFromObject = (TaskExecutor) ReflectionUtils.getField(taskExecutorField, population);

		assertSame(taskExecutor, taskExecutorFromObject);
	}

	@Test
	public void testSetSelector() {
		StandardPopulation population = new StandardPopulation();

		Selector selector = mock(Selector.class);
		population.setSelector(selector);

		Field selectorField = ReflectionUtils.findField(StandardPopulation.class, "selector");
		ReflectionUtils.makeAccessible(selectorField);
		Selector selectorFromObject = (Selector) ReflectionUtils.getField(selectorField, population);

		assertSame(selector, selectorFromObject);
	}

	@Test
	public void testSetLifespan() {
		StandardPopulation population = new StandardPopulation();

		int lifespanToSet = 25;
		population.setLifespan(lifespanToSet);

		Field lifespanField = ReflectionUtils.findField(StandardPopulation.class, "lifespan");
		ReflectionUtils.makeAccessible(lifespanField);
		int lifespanFromObject = (int) ReflectionUtils.getField(lifespanField, population);

		assertSame(lifespanToSet, lifespanFromObject);
	}

	@Test
	public void testSetKnownSolutionFitnessEvaluator() {
		StandardPopulation population = new StandardPopulation();

		FitnessEvaluator knownSolutionFitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorMock);

		Field knownSolutionFitnessEvaluatorField = ReflectionUtils.findField(StandardPopulation.class, "knownSolutionFitnessEvaluator");
		ReflectionUtils.makeAccessible(knownSolutionFitnessEvaluatorField);
		FitnessEvaluator knownSolutionFitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(knownSolutionFitnessEvaluatorField, population);

		assertSame(knownSolutionFitnessEvaluatorMock, knownSolutionFitnessEvaluatorFromObject);
	}

	@Test
	public void testSetCompareToKnownSolution() {
		StandardPopulation population = new StandardPopulation();

		Boolean compareToKnownSolution = true;
		population.setCompareToKnownSolution(compareToKnownSolution);

		Field compareToKnownSolutionField = ReflectionUtils.findField(StandardPopulation.class, "compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(compareToKnownSolutionField, population);

		assertSame(compareToKnownSolution, compareToKnownSolutionFromObject);
	}

	@Test
	public void testSetCompareToKnownSolutionDefault() {
		StandardPopulation population = new StandardPopulation();

		Field compareToKnownSolutionField = ReflectionUtils.findField(StandardPopulation.class, "compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(compareToKnownSolutionField, population);

		assertEquals(false, compareToKnownSolutionFromObject);
	}

	@Test
	public void testGeneratorTask() {
		StandardPopulation population = new StandardPopulation();
		StandardPopulation.GeneratorTask generatorTask = population.new GeneratorTask();

		MockKeyedChromosome chromosomeToReturn = new MockKeyedChromosome();
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
		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);

		int expectedPopulationSize = 10;

		Breeder breederMock = mock(Breeder.class);
		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();
		mockKeyedChromosome.setFitness(5.0);
		when(breederMock.breed()).thenReturn(mockKeyedChromosome.clone());
		population.setBreeder(breederMock);

		assertEquals(0, population.size());
		assertEquals(new Double(0.0), population.getTotalFitness());

		population.breed(expectedPopulationSize);

		assertEquals(expectedPopulationSize, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());
	}

	@Test
	public void testEvaluatorTask() {
		StandardPopulation population = new StandardPopulation();
		MockKeyedChromosome chromosomeToEvaluate = new MockKeyedChromosome();

		FitnessEvaluator mockEvaluator = mock(FitnessEvaluator.class);
		Double fitnessToReturn = new Double(101.0);
		when(mockEvaluator.evaluate(same(chromosomeToEvaluate))).thenReturn(fitnessToReturn);

		StandardPopulation.EvaluatorTask evaluatorTask = population.new EvaluatorTask(chromosomeToEvaluate);
		population.setFitnessEvaluator(mockEvaluator);

		Void fitnessReturned = null;
		try {
			fitnessReturned = evaluatorTask.call();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertNull(fitnessReturned);
	}

	@Test
	public void testDoConcurrentFitnessEvaluations() throws InterruptedException {
		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		MockKeyedChromosome chromosomeEvaluationNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNeeded2 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded2.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNotNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeyedChromosome chromosomeEvaluationNotNeeded2 = new MockKeyedChromosome();
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
		verify(fitnessEvaluatorMock, times(2)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testEvaluateFitness() throws InterruptedException {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		MockKeyedChromosome chromosomeEvaluationNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNeeded2 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNotNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeyedChromosome chromosomeEvaluationNotNeeded2 = new MockKeyedChromosome();
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
		verify(fitnessEvaluatorMock, times(2)).evaluate(any(Chromosome.class));

		/*
		 * The fitnessEvaluatorMock always returns 100.0, so the total is (100.0 x 2) + 5.0 + 100.1, since two
		 * individuals are re-evaluated
		 */
		Double expectedTotalFitness = new Double(305.1);

		assertEquals(expectedTotalFitness, population.getTotalFitness());
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(
				generationStatistics.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
	}

	@Test
	public void testEvaluateFitnessCompareToKnownSolution() throws InterruptedException {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);
		population.setCompareToKnownSolution(true);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		FitnessEvaluator knownSolutionFitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorMock);

		MockKeyedChromosome chromosomeEvaluationNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNeeded2 = new MockKeyedChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeyedChromosome chromosomeEvaluationNotNeeded1 = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeyedChromosome chromosomeEvaluationNotNeeded2 = new MockKeyedChromosome();
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
		verify(fitnessEvaluatorMock, times(2)).evaluate(any(Chromosome.class));

		/*
		 * The fitnessEvaluatorMock always returns 100.0, so the total is (100.0 x 2) + 5.0 + 100.1, since two
		 * individuals are re-evaluated
		 */
		Double expectedTotalFitness = new Double(305.1);

		assertEquals(expectedTotalFitness, population.getTotalFitness());
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(
				generationStatistics.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
		assertEquals(new Double(DEFAULT_FITNESS_VALUE), generationStatistics.getKnownSolutionProximity());
	}

	@Test
	public void testIncreaseAge() throws InterruptedException {
		StandardPopulation population = new StandardPopulation();
		population.setLifespan(5);

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockKeyedChromosome chromosome3 = new MockKeyedChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockKeyedChromosome chromosome4 = new MockKeyedChromosome();
		chromosome4.setAge(4);
		population.addIndividual(chromosome4);

		population.increaseAge();

		assertEquals(2, population.size());
		assertEquals(1, chromosome1.getAge());
		assertEquals(5, chromosome4.getAge());
	}

	@Test
	public void testIncreaseAgeIndefinitely() throws InterruptedException {
		StandardPopulation population = new StandardPopulation();
		population.setLifespan(-1);

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockKeyedChromosome chromosome3 = new MockKeyedChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockKeyedChromosome chromosome4 = new MockKeyedChromosome();
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
		StandardPopulation population = new StandardPopulation();

		int indexToReturn = 7;

		Selector selector = mock(Selector.class);
		when(selector.getNextIndex(anyListOf(Chromosome.class), anyDouble())).thenReturn(indexToReturn);
		population.setSelector(selector);

		assertEquals(indexToReturn, population.selectIndex());
		verify(selector, times(1)).getNextIndex(anyListOf(Chromosome.class), anyDouble());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIndividualsUnmodifiable() {
		StandardPopulation population = new StandardPopulation();
		population.addIndividual(mock(Chromosome.class));
		population.addIndividual(mock(Chromosome.class));
		population.addIndividual(mock(Chromosome.class));

		List<Chromosome> individuals = population.getIndividuals();
		individuals.remove(0); // should throw exception
	}

	@Test
	public void getNullIndividuals() {
		StandardPopulation population = new StandardPopulation();
		assertNotNull(population.getIndividuals());
	}

	@Test
	public void testAddIndividual() {
		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeyedChromosome chromosomeEvaluationNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividual(chromosomeEvaluationNeeded);

		// Validate
		fitnessSum += chromosomeEvaluationNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verify(fitnessEvaluatorMock, times(1)).evaluate(same(chromosomeEvaluationNeeded));
		assertEquals(1, population.size());
		assertSame(chromosomeEvaluationNeeded, population.getIndividuals().get(0));

		// Add a chromosome that doesn't need evaluation
		MockKeyedChromosome chromosomeEvaluationNotNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded);

		// Validate
		fitnessSum += chromosomeEvaluationNotNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verifyNoMoreInteractions(fitnessEvaluatorMock);
		assertEquals(2, population.size());
		assertSame(chromosomeEvaluationNotNeeded, population.getIndividuals().get(1));
	}

	@Test
	public void testRemoveIndividual() {
		StandardPopulation population = new StandardPopulation();

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
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
		StandardPopulation population = new StandardPopulation();

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
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
		StandardPopulation population = new StandardPopulation();

		Field ineligibleForReproductionField = ReflectionUtils.findField(StandardPopulation.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeyedChromosome chromosomeEvaluationNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
		assertEquals(0, population.size());

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);
		assertEquals(1, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNeeded, ineligibleForReproductionFromObject.get(0));

		// Add a chromosome that doesn't need evaluation
		MockKeyedChromosome chromosomeEvaluationNotNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
		assertEquals(0, population.size());

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNotNeeded, ineligibleForReproductionFromObject.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMakeIneligibleForReproduction() {
		StandardPopulation population = new StandardPopulation();

		Field ineligibleForReproductionField = ReflectionUtils.findField(StandardPopulation.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
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
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);
		assertEquals(1, ineligibleForReproductionFromObject.size());
		assertSame(chromosome1, ineligibleForReproductionFromObject.get(0));

		fitnessSum -= chromosome2.getFitness();
		population.makeIneligibleForReproduction(0);

		// Validate individuals List
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Validate ineligible List
		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosome2, ineligibleForReproductionFromObject.get(1));

		// Try to remove an individual that doesn't exist
		assertNull(population.removeIndividual(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResetEligibility() {
		StandardPopulation population = new StandardPopulation();

		Field ineligibleForReproductionField = ReflectionUtils.findField(StandardPopulation.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeyedChromosome chromosomeEvaluationNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Add a chromosome that doesn't need evaluation
		MockKeyedChromosome chromosomeEvaluationNotNeeded = new MockKeyedChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
		assertEquals(0, population.size());

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(ineligibleForReproductionField, population);
		assertEquals(2, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNeeded, ineligibleForReproductionFromObject.get(0));
		assertSame(chromosomeEvaluationNotNeeded, ineligibleForReproductionFromObject.get(1));

		population.resetEligibility();

		assertEquals(0, ineligibleForReproductionFromObject.size());

		fitnessSum = chromosomeEvaluationNeeded.getFitness() + chromosomeEvaluationNotNeeded.getFitness();
		assertEquals(fitnessSum, population.getTotalFitness());
		verify(fitnessEvaluatorMock, times(1)).evaluate(same(chromosomeEvaluationNeeded));
		assertEquals(2, population.size());
		assertSame(chromosomeEvaluationNeeded, population.getIndividuals().get(0));
		assertSame(chromosomeEvaluationNotNeeded, population.getIndividuals().get(1));
	}

	@Test
	public void testSize() {
		StandardPopulation population = new StandardPopulation();
		population.setTaskExecutor(taskExecutor);

		// This is needed to avoid a NullPointerException on fitnessEvaluator
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		assertEquals(0, population.size());

		population.addIndividual(new MockKeyedChromosome());

		assertEquals(1, population.size());

		population.addIndividual(new MockKeyedChromosome());

		assertEquals(2, population.size());
	}

	@Test
	public void testSortIndividuals() {
		StandardPopulation population = new StandardPopulation();
		population.setFitnessComparator(new AscendingFitnessComparator());

		MockKeyedChromosome chromosome1 = new MockKeyedChromosome();
		chromosome1.setFitness(3.0);
		population.addIndividual(chromosome1);

		MockKeyedChromosome chromosome2 = new MockKeyedChromosome();
		chromosome2.setFitness(2.0);
		population.addIndividual(chromosome2);

		MockKeyedChromosome chromosome3 = new MockKeyedChromosome();
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
