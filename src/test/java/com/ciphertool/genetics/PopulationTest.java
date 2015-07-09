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
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class PopulationTest {
	private static ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	private static final double DEFAULT_FITNESS_VALUE = 100.0;

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
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField, population);

		Field geneticStructureField = ReflectionUtils.findField(MockBreeder.class, "geneticStructure");
		ReflectionUtils.makeAccessible(geneticStructureField);
		Object geneticStructureFromObject = ReflectionUtils.getField(geneticStructureField, breederFromObject);

		assertSame(geneticStructure, geneticStructureFromObject);
	}

	@Test
	public void testSetBreeder() {
		Population population = new Population();

		MockBreeder mockBreeder = new MockBreeder();
		population.setBreeder(mockBreeder);

		Field breederField = ReflectionUtils.findField(Population.class, "breeder");
		ReflectionUtils.makeAccessible(breederField);
		MockBreeder breederFromObject = (MockBreeder) ReflectionUtils.getField(breederField, population);

		assertSame(mockBreeder, breederFromObject);
	}

	@Test
	public void testSetFitnessEvaluator() {
		Population population = new Population();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Field fitnessEvaluatorField = ReflectionUtils.findField(Population.class, "fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		FitnessEvaluator fitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(
				fitnessEvaluatorField, population);

		assertSame(fitnessEvaluatorMock, fitnessEvaluatorFromObject);
	}

	@Test
	public void testSetFitnessComparator() {
		Population population = new Population();

		AscendingFitnessComparator ascendingFitnessComparator = new AscendingFitnessComparator();
		population.setFitnessComparator(ascendingFitnessComparator);

		Field fitnessComparatorField = ReflectionUtils.findField(Population.class, "fitnessComparator");
		ReflectionUtils.makeAccessible(fitnessComparatorField);
		AscendingFitnessComparator fitnessComparatorFromObject = (AscendingFitnessComparator) ReflectionUtils.getField(
				fitnessComparatorField, population);

		assertSame(ascendingFitnessComparator, fitnessComparatorFromObject);
	}

	@Test
	public void testSetTaskExecutor() {
		Population population = new Population();

		TaskExecutor taskExecutor = mock(TaskExecutor.class);
		population.setTaskExecutor(taskExecutor);

		Field taskExecutorField = ReflectionUtils.findField(Population.class, "taskExecutor");
		ReflectionUtils.makeAccessible(taskExecutorField);
		TaskExecutor taskExecutorFromObject = (TaskExecutor) ReflectionUtils.getField(taskExecutorField, population);

		assertSame(taskExecutor, taskExecutorFromObject);
	}

	@Test
	public void testSetSelector() {
		Population population = new Population();

		Selector selector = mock(Selector.class);
		population.setSelector(selector);

		Field selectorField = ReflectionUtils.findField(Population.class, "selector");
		ReflectionUtils.makeAccessible(selectorField);
		Selector selectorFromObject = (Selector) ReflectionUtils.getField(selectorField, population);

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

		FitnessEvaluator knownSolutionFitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorMock);

		Field knownSolutionFitnessEvaluatorField = ReflectionUtils.findField(Population.class,
				"knownSolutionFitnessEvaluator");
		ReflectionUtils.makeAccessible(knownSolutionFitnessEvaluatorField);
		FitnessEvaluator knownSolutionFitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(
				knownSolutionFitnessEvaluatorField, population);

		assertSame(knownSolutionFitnessEvaluatorMock, knownSolutionFitnessEvaluatorFromObject);
	}

	@Test
	public void testSetCompareToKnownSolution() {
		Population population = new Population();

		Boolean compareToKnownSolution = true;
		population.setCompareToKnownSolution(compareToKnownSolution);

		Field compareToKnownSolutionField = ReflectionUtils.findField(Population.class, "compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(compareToKnownSolutionField,
				population);

		assertSame(compareToKnownSolution, compareToKnownSolutionFromObject);
	}

	@Test
	public void testSetCompareToKnownSolutionDefault() {
		Population population = new Population();

		Field compareToKnownSolutionField = ReflectionUtils.findField(Population.class, "compareToKnownSolution");
		ReflectionUtils.makeAccessible(compareToKnownSolutionField);
		Boolean compareToKnownSolutionFromObject = (Boolean) ReflectionUtils.getField(compareToKnownSolutionField,
				population);

		assertEquals(false, compareToKnownSolutionFromObject);
	}

	@Test
	public void testGeneratorTask() {
		Population population = new Population();
		Population.GeneratorTask generatorTask = population.new GeneratorTask();

		MockKeylessChromosome chromosomeToReturn = new MockKeylessChromosome();
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

		int expectedPopulationSize = 10;

		Breeder breederMock = mock(Breeder.class);
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setFitness(5.0);
		when(breederMock.breed()).thenReturn(mockKeylessChromosome.clone());
		population.setBreeder(breederMock);

		assertEquals(0, population.size());
		assertEquals(new Double(0.0), population.getTotalFitness());

		population.breed(expectedPopulationSize);

		assertEquals(expectedPopulationSize, population.size());
		assertEquals(new Double(50.0), population.getTotalFitness());
	}

	@Test
	public void testEvaluatorTask() {
		Population population = new Population();
		MockKeylessChromosome chromosomeToEvaluate = new MockKeylessChromosome();

		FitnessEvaluator mockEvaluator = mock(FitnessEvaluator.class);
		Double fitnessToReturn = new Double(101.0);
		when(mockEvaluator.evaluate(same(chromosomeToEvaluate))).thenReturn(fitnessToReturn);

		Population.EvaluatorTask evaluatorTask = population.new EvaluatorTask(chromosomeToEvaluate);
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
	public void testDoConcurrentFitnessEvaluations() {
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		MockKeylessChromosome chromosomeEvaluationNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNeeded2 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded2.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNotNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(1.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeylessChromosome chromosomeEvaluationNotNeeded2 = new MockKeylessChromosome();
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
	public void testEvaluateFitness() {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		MockKeylessChromosome chromosomeEvaluationNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNeeded2 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNotNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeylessChromosome chromosomeEvaluationNotNeeded2 = new MockKeylessChromosome();
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
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(generationStatistics
				.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
	}

	@Test
	public void testEvaluateFitnessCompareToKnownSolution() {
		GenerationStatistics generationStatistics = new GenerationStatistics();

		Population population = new Population();
		population.setTaskExecutor(taskExecutor);
		population.setCompareToKnownSolution(true);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		FitnessEvaluator knownSolutionFitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorMock);

		MockKeylessChromosome chromosomeEvaluationNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded1);
		chromosomeEvaluationNeeded1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNeeded2 = new MockKeylessChromosome();
		chromosomeEvaluationNeeded2.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNeeded2);
		chromosomeEvaluationNeeded2.setEvaluationNeeded(true);

		MockKeylessChromosome chromosomeEvaluationNotNeeded1 = new MockKeylessChromosome();
		chromosomeEvaluationNotNeeded1.setFitness(5.0);
		population.addIndividual(chromosomeEvaluationNotNeeded1);

		MockKeylessChromosome chromosomeEvaluationNotNeeded2 = new MockKeylessChromosome();
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
		assertEquals(new Double(expectedTotalFitness / population.size()), new Double(generationStatistics
				.getAverageFitness()));
		assertEquals(new Double(100.1), new Double(generationStatistics.getBestFitness()));
		assertEquals(new Double(DEFAULT_FITNESS_VALUE), generationStatistics.getKnownSolutionProximity());
	}

	@Test
	public void testIncreaseAge() {
		Population population = new Population();
		population.setLifespan(5);

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockKeylessChromosome chromosome3 = new MockKeylessChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockKeylessChromosome chromosome4 = new MockKeylessChromosome();
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

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setAge(0);
		population.addIndividual(chromosome1);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
		chromosome2.setAge(50);
		population.addIndividual(chromosome2);

		MockKeylessChromosome chromosome3 = new MockKeylessChromosome();
		chromosome3.setAge(5);
		population.addIndividual(chromosome3);

		MockKeylessChromosome chromosome4 = new MockKeylessChromosome();
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
		when(selector.getNextIndex(anyListOf(Chromosome.class), anyDouble())).thenReturn(indexToReturn);
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

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeylessChromosome chromosomeEvaluationNeeded = new MockKeylessChromosome();
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
		MockKeylessChromosome chromosomeEvaluationNotNeeded = new MockKeylessChromosome();
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
		Population population = new Population();

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
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

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
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

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeylessChromosome chromosomeEvaluationNeeded = new MockKeylessChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
		assertEquals(0, population.size());

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
		assertEquals(1, ineligibleForReproductionFromObject.size());
		assertSame(chromosomeEvaluationNeeded, ineligibleForReproductionFromObject.get(0));

		// Add a chromosome that doesn't need evaluation
		MockKeylessChromosome chromosomeEvaluationNotNeeded = new MockKeylessChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
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

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(5.0);
		population.addIndividual(chromosome1);
		chromosome1.setEvaluationNeeded(true);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
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
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
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

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		Double fitnessSum = 0.0;
		assertEquals(fitnessSum, population.getTotalFitness());
		assertEquals(0, population.size());

		// Add a chromosome that needs evaluation
		MockKeylessChromosome chromosomeEvaluationNeeded = new MockKeylessChromosome();
		chromosomeEvaluationNeeded.setFitness(5.0);
		chromosomeEvaluationNeeded.setEvaluationNeeded(true);
		population.addIndividualAsIneligible(chromosomeEvaluationNeeded);

		// Add a chromosome that doesn't need evaluation
		MockKeylessChromosome chromosomeEvaluationNotNeeded = new MockKeylessChromosome();
		chromosomeEvaluationNotNeeded.setFitness(5.0);
		population.addIndividualAsIneligible(chromosomeEvaluationNotNeeded);

		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		// Validate - this shouldn't affect the individuals List
		assertEquals(new Double(0.0), population.getTotalFitness());
		verifyZeroInteractions(fitnessEvaluatorMock);
		assertEquals(0, population.size());

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);
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
		Population population = new Population();
		population.setTaskExecutor(taskExecutor);

		// This is needed to avoid a NullPointerException on fitnessEvaluator
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		population.setFitnessEvaluator(fitnessEvaluatorMock);

		assertEquals(0, population.size());

		population.addIndividual(new MockKeylessChromosome());

		assertEquals(1, population.size());

		population.addIndividual(new MockKeylessChromosome());

		assertEquals(2, population.size());
	}

	@Test
	public void testSortIndividuals() {
		Population population = new Population();
		population.setFitnessComparator(new AscendingFitnessComparator());

		MockKeylessChromosome chromosome1 = new MockKeylessChromosome();
		chromosome1.setFitness(3.0);
		population.addIndividual(chromosome1);

		MockKeylessChromosome chromosome2 = new MockKeylessChromosome();
		chromosome2.setFitness(2.0);
		population.addIndividual(chromosome2);

		MockKeylessChromosome chromosome3 = new MockKeylessChromosome();
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
