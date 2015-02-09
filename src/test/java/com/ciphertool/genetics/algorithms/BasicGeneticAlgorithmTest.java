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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class BasicGeneticAlgorithmTest {
	private static final double DEFAULT_FITNESS_VALUE = 100.0;

	@Test
	public void testSetPopulation() {
		Population populationToSet = mock(Population.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationToSet);

		Population populationFromObject = basicGeneticAlgorithm.getPopulation();

		assertSame(populationToSet, populationFromObject);
	}

	@Test
	public void testSetExecutionStatisticsDao() {
		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		Field executionStatisticsDaoField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"executionStatisticsDao");
		ReflectionUtils.makeAccessible(executionStatisticsDaoField);
		ExecutionStatisticsDao executionStatisticsDaoFromObject = (ExecutionStatisticsDao) ReflectionUtils
				.getField(executionStatisticsDaoField, basicGeneticAlgorithm);

		assertSame(executionStatisticsDaoToSet, executionStatisticsDaoFromObject);
	}

	@Test
	public void testSetStrategy() {
		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();

		Population populationMock = mock(Population.class);
		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);
		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		SelectionAlgorithm selectionAlgorithmMock = mock(SelectionAlgorithm.class);
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		FitnessEvaluator knownSolutionFitnessEvaluatorMock = mock(FitnessEvaluator.class);
		Object geneticStructure = new Object();
		int lifeSpan = 10;
		boolean compareToKnownSolution = true;
		boolean mutateDuringCrossover = true;
		int maxMutationsPerIndividual = 5;

		strategyToSet.setGeneticStructure(geneticStructure);
		strategyToSet.setFitnessEvaluator(fitnessEvaluatorMock);
		strategyToSet.setLifespan(lifeSpan);
		strategyToSet.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorMock);
		strategyToSet.setCompareToKnownSolution(compareToKnownSolution);
		strategyToSet.setCrossoverAlgorithm(crossoverAlgorithmMock);
		strategyToSet.setMutationAlgorithm(mutationAlgorithmMock);
		strategyToSet.setMutateDuringCrossover(mutateDuringCrossover);
		strategyToSet.setMaxMutationsPerIndividual(maxMutationsPerIndividual);
		strategyToSet.setSelectionAlgorithm(selectionAlgorithmMock);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);
		basicGeneticAlgorithm.setStrategy(strategyToSet);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		GeneticAlgorithmStrategy strategyFromObject = (GeneticAlgorithmStrategy) ReflectionUtils
				.getField(strategyField, basicGeneticAlgorithm);

		assertSame(strategyToSet, strategyFromObject);
		verify(populationMock, times(1)).setGeneticStructure(same(geneticStructure));
		verify(populationMock, times(1)).setFitnessEvaluator(same(fitnessEvaluatorMock));
		verify(populationMock, times(1)).setLifespan(eq(lifeSpan));
		verify(populationMock, times(1)).setKnownSolutionFitnessEvaluator(
				same(knownSolutionFitnessEvaluatorMock));
		verify(populationMock, times(1)).setCompareToKnownSolution(eq(compareToKnownSolution));
		verifyNoMoreInteractions(populationMock);

		verify(crossoverAlgorithmMock, times(1)).setMutationAlgorithm(same(mutationAlgorithmMock));
		verify(crossoverAlgorithmMock, times(1))
				.setMutateDuringCrossover(eq(mutateDuringCrossover));
		verifyNoMoreInteractions(crossoverAlgorithmMock);

		verify(mutationAlgorithmMock, times(1)).setMaxMutationsPerChromosome(
				eq(maxMutationsPerIndividual));
		verifyNoMoreInteractions(mutationAlgorithmMock);

		Field crossoverAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		CrossoverAlgorithm crossoverAlgorithmFromObject = (CrossoverAlgorithm) ReflectionUtils
				.getField(crossoverAlgorithmField, basicGeneticAlgorithm);

		assertSame(crossoverAlgorithmMock, crossoverAlgorithmFromObject);

		Field mutationAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils
				.getField(mutationAlgorithmField, basicGeneticAlgorithm);

		assertSame(mutationAlgorithmMock, mutationAlgorithmFromObject);

		Field selectionAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		SelectionAlgorithm selectionAlgorithmFromObject = (SelectionAlgorithm) ReflectionUtils
				.getField(selectionAlgorithmField, basicGeneticAlgorithm);

		assertSame(selectionAlgorithmMock, selectionAlgorithmFromObject);
	}

	@Test
	public void testInitialize() {
		Date beforeInitialize = new Date();

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		int populationSize = 100;
		int lifeSpan = 0;
		double survivalRate = 0.1;
		double mutationRate = 0.0;
		double crossoverRate = 0.0;
		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);
		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		strategyToSet.setGeneticStructure(new Object());
		strategyToSet.setPopulationSize(populationSize);
		strategyToSet.setLifespan(lifeSpan);
		strategyToSet.setSurvivalRate(survivalRate);
		strategyToSet.setMutationRate(mutationRate);
		strategyToSet.setMaxMutationsPerIndividual(0);
		strategyToSet.setCrossoverRate(crossoverRate);
		strategyToSet.setMutateDuringCrossover(false);
		strategyToSet.setMaxGenerations(-1);
		strategyToSet.setCrossoverAlgorithm(crossoverAlgorithmMock);
		strategyToSet.setFitnessEvaluator(fitnessEvaluatorMock);
		strategyToSet.setMutationAlgorithm(mutationAlgorithmMock);
		strategyToSet.setSelectionAlgorithm(mock(SelectionAlgorithm.class));
		strategyToSet.setSelector(mock(Selector.class));

		Population populationMock = mock(Population.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		Field generationCountField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, basicGeneticAlgorithm, 1);

		Field stopRequestedField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"stopRequested");
		ReflectionUtils.makeAccessible(stopRequestedField);
		ReflectionUtils.setField(stopRequestedField, basicGeneticAlgorithm, true);

		Field executionStatisticsField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ExecutionStatistics executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils
				.getField(executionStatisticsField, basicGeneticAlgorithm);
		assertNull(executionStatisticsFromObject);

		basicGeneticAlgorithm.initialize();

		int generationCountFromObject = (int) ReflectionUtils.getField(generationCountField,
				basicGeneticAlgorithm);
		boolean stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				basicGeneticAlgorithm);
		executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils.getField(
				executionStatisticsField, basicGeneticAlgorithm);

		assertEquals(0, generationCountFromObject);
		assertFalse(stopRequestedFromObject);
		assertNotNull(executionStatisticsFromObject);
		assertTrue(executionStatisticsFromObject.getStartDateTime().getTime() >= beforeInitialize
				.getTime());
		assertEquals(populationSize, executionStatisticsFromObject.getPopulationSize().intValue());
		assertEquals(lifeSpan, executionStatisticsFromObject.getLifespan().intValue());
		assertEquals(new Double(survivalRate), executionStatisticsFromObject.getSurvivalRate());
		assertEquals(new Double(mutationRate), executionStatisticsFromObject.getMutationRate());
		assertEquals(new Double(crossoverRate), executionStatisticsFromObject.getCrossoverRate());
		assertEquals(crossoverAlgorithmMock.getClass().getSimpleName(),
				executionStatisticsFromObject.getCrossoverAlgorithm());
		assertEquals(fitnessEvaluatorMock.getClass().getSimpleName(), executionStatisticsFromObject
				.getFitnessEvaluator());
		assertEquals(mutationAlgorithmMock.getClass().getSimpleName(),
				executionStatisticsFromObject.getMutationAlgorithm());

		verify(populationMock, times(1)).clearIndividuals();
		verify(populationMock, times(1)).breed(eq(populationSize));
		verify(populationMock, times(1)).evaluateFitness(null);
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);
	}

	@Test
	public void testFinish() {
		Date beforeFinish = new Date();

		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, basicGeneticAlgorithm,
				executionStatistics);

		Field generationCountField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, basicGeneticAlgorithm, 1);

		basicGeneticAlgorithm.finish();

		assertTrue(executionStatistics.getEndDateTime().getTime() >= beforeFinish.getTime());

		verify(executionStatisticsDaoToSet, times(1)).insert(same(executionStatistics));

		ExecutionStatistics executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils
				.getField(executionStatisticsField, basicGeneticAlgorithm);
		assertNull(executionStatisticsFromObject);
	}

	@Test
	public void testProceedWithNextGeneration() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		int initialPopulationSize = 100;
		int populationSize = 100;
		int index = 0;
		double survivalRate = 0.9;
		double mutationRate = 0.1;
		double crossoverRate = 0.1;

		Population populationMock = mock(Population.class);

		List<Chromosome> individuals = new ArrayList<Chromosome>();
		for (int i = 0; i < initialPopulationSize; i++) {
			individuals.add(new MockKeylessChromosome());
		}

		when(populationMock.selectIndex()).thenReturn(index);
		when(populationMock.getIndividuals()).thenReturn(individuals);
		when(populationMock.size()).thenReturn(initialPopulationSize);
		when(populationMock.selectIndex()).thenReturn(0);
		basicGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setPopulationSize(populationSize);
		strategyToSet.setSurvivalRate(survivalRate);
		strategyToSet.setMutationRate(mutationRate);
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		SelectionAlgorithm selectionAlgorithmMock = mock(SelectionAlgorithm.class);

		Field selectionAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		ReflectionUtils.setField(selectionAlgorithmField, basicGeneticAlgorithm,
				selectionAlgorithmMock);

		Field generationCountField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, basicGeneticAlgorithm, 0);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, basicGeneticAlgorithm,
				mutationAlgorithmMock);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, basicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class)))
				.thenReturn(Arrays.asList(chromosomeToReturn));

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, basicGeneticAlgorithm,
				executionStatistics);

		assertEquals(0, executionStatistics.getGenerationStatisticsList().size());

		basicGeneticAlgorithm.proceedWithNextGeneration();

		assertEquals(1, executionStatistics.getGenerationStatisticsList().size());

		/*
		 * The population size should be reduced by the number of parents used
		 * during crossover.
		 */
		assertEquals(100, populationMock.size());

		int generationCountFromObject = (int) ReflectionUtils.getField(generationCountField,
				basicGeneticAlgorithm);
		assertEquals(1, generationCountFromObject);

		verify(selectionAlgorithmMock, times(1)).select(same(populationMock), eq(populationSize),
				eq(survivalRate));
		verifyNoMoreInteractions(selectionAlgorithmMock);

		verify(populationMock, times(30)).selectIndex();
		verify(populationMock, times(30)).getIndividuals();
		verify(populationMock, times(30)).makeIneligibleForReproduction(index);
		verify(populationMock, times(20)).addIndividualAsIneligible(any(Chromosome.class));
		verify(populationMock, times(5)).size();
		verify(populationMock, times(1)).increaseAge();
		verify(populationMock, times(1)).resetEligibility();
		verify(populationMock, times(1)).breed(populationSize);
		verify(populationMock, times(1)).evaluateFitness(any(GenerationStatistics.class));
		verifyNoMoreInteractions(populationMock);

		verify(mutationAlgorithmMock, times(10)).mutateChromosome(any(Chromosome.class));
		verifyNoMoreInteractions(mutationAlgorithmMock);

		verify(crossoverAlgorithmMock, times(10)).crossover(any(Chromosome.class),
				any(Chromosome.class));
		verifyNoMoreInteractions(crossoverAlgorithmMock);
	}

	@Test
	public void testValidateParameters_NoErrors() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setGeneticStructure(new Object());
		strategyToSet.setPopulationSize(1);
		strategyToSet.setLifespan(0);
		strategyToSet.setSurvivalRate(0.1);
		strategyToSet.setMutationRate(0.0);
		strategyToSet.setMaxMutationsPerIndividual(0);
		strategyToSet.setCrossoverRate(0.0);
		strategyToSet.setMutateDuringCrossover(false);
		strategyToSet.setMaxGenerations(-1);
		strategyToSet.setCrossoverAlgorithm(mock(CrossoverAlgorithm.class));
		strategyToSet.setFitnessEvaluator(mock(FitnessEvaluator.class));
		strategyToSet.setMutationAlgorithm(mock(MutationAlgorithm.class));
		strategyToSet.setSelectionAlgorithm(mock(SelectionAlgorithm.class));
		strategyToSet.setSelector(mock(Selector.class));

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);
	}

	@Test
	public void testValidateParameters_AllErrors() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setGeneticStructure(null);
		strategyToSet.setPopulationSize(0);
		strategyToSet.setLifespan(null);

		/*
		 * This must be set via reflection because the setter method does its
		 * own validation
		 */
		Field survivalRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class,
				"survivalRate");
		ReflectionUtils.makeAccessible(survivalRateField);
		ReflectionUtils.setField(survivalRateField, strategyToSet, -0.1);

		/*
		 * This must be set via reflection because the setter method does its
		 * own validation
		 */
		Field mutationRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class,
				"mutationRate");
		ReflectionUtils.makeAccessible(mutationRateField);
		ReflectionUtils.setField(mutationRateField, strategyToSet, -0.1);

		strategyToSet.setMaxMutationsPerIndividual(-1);

		/*
		 * This must be set via reflection because the setter method does its
		 * own validation
		 */
		Field crossoverRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class,
				"crossoverRate");
		ReflectionUtils.makeAccessible(crossoverRateField);
		ReflectionUtils.setField(crossoverRateField, strategyToSet, -0.1);

		strategyToSet.setMutateDuringCrossover(null);
		strategyToSet.setMaxGenerations(0);
		strategyToSet.setCrossoverAlgorithm(null);
		strategyToSet.setFitnessEvaluator(null);
		strategyToSet.setMutationAlgorithm(null);
		strategyToSet.setSelectionAlgorithm(null);
		strategyToSet.setSelector(null);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		boolean exceptionCaught = false;

		try {
			basicGeneticAlgorithm.validateParameters();
		} catch (IllegalStateException ise) {
			String expectedMessage = "Unable to execute genetic algorithm because one or more of the required parameters are missing.  The validation errors are:";
			expectedMessage += "\n\t-Parameter 'geneticStructure' cannot be null.";
			expectedMessage += "\n\t-Parameter 'populationSize' must be greater than zero.";
			expectedMessage += "\n\t-Parameter 'lifespan' cannot be null.";
			expectedMessage += "\n\t-Parameter 'survivalRate' must be greater than zero.";
			expectedMessage += "\n\t-Parameter 'mutationRate' must be greater than or equal to zero.";
			expectedMessage += "\n\t-Parameter 'maxMutationsPerIndividual' must be greater than or equal to zero.";
			expectedMessage += "\n\t-Parameter 'crossoverRate' must be greater than or equal to zero.";
			expectedMessage += "\n\t-Parameter 'mutateDuringCrossover' cannot be null.";
			expectedMessage += "\n\t-Parameter 'maxGenerations' cannot be null and must not equal zero.";
			expectedMessage += "\n\t-Parameter 'crossoverAlgorithm' cannot be null.";
			expectedMessage += "\n\t-Parameter 'fitnessEvaluator' cannot be null.";
			expectedMessage += "\n\t-Parameter 'mutationAlgorithm' cannot be null.";
			expectedMessage += "\n\t-Parameter 'selectionAlgorithm' cannot be null.";
			expectedMessage += "\n\t-Parameter 'selectorMethod' cannot be null.";

			assertEquals(expectedMessage, ise.getMessage());

			exceptionCaught = true;
		}

		assertTrue(exceptionCaught);
	}

	@Test
	public void testSelect() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		int populationSize = 100;
		double survivalRate = 0.9;

		Population population = new Population();
		basicGeneticAlgorithm.setPopulation(population);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setPopulationSize(populationSize);
		strategyToSet.setSurvivalRate(survivalRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		SelectionAlgorithm selectionAlgorithmMock = mock(SelectionAlgorithm.class);

		Field selectionAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		ReflectionUtils.setField(selectionAlgorithmField, basicGeneticAlgorithm,
				selectionAlgorithmMock);

		basicGeneticAlgorithm.select();

		verify(selectionAlgorithmMock, times(1)).select(same(population), eq(populationSize),
				eq(survivalRate));
		verifyNoMoreInteractions(selectionAlgorithmMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCrossover() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

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
			population.addIndividual(new MockKeylessChromosome());
		}

		basicGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, basicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class)))
				.thenReturn(Arrays.asList(chromosomeToReturn));

		GeneticAlgorithmStrategy strategy = new GeneticAlgorithmStrategy();
		strategy.setCrossoverRate(0.1);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategy);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = basicGeneticAlgorithm.crossover(initialPopulationSize);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(5, childrenProduced);

		/*
		 * The population size should be reduced by the number of parents used
		 * during crossover.
		 */
		assertEquals(40, population.size());

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
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		Population population = new Population();

		Chromosome chromosome = new MockKeylessChromosome();
		population.addIndividual(chromosome);
		basicGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, basicGeneticAlgorithm,
				crossoverAlgorithmMock);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class,
				"ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils
				.getField(ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = basicGeneticAlgorithm.crossover(10);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(1, population.size());

		assertEquals(0, childrenProduced);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		verifyZeroInteractions(crossoverAlgorithmMock);
	}

	@Test
	public void testDeterminePairsToCrossover() {
		int initialPopulationSize = 100;

		Population populationMock = mock(Population.class);
		when(populationMock.size()).thenReturn(initialPopulationSize);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double crossoverRate = 0.5;
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		long pairsToCrossover = basicGeneticAlgorithm
				.determinePairsToCrossover(initialPopulationSize);

		assertEquals(50, pairsToCrossover);
	}

	@Test
	public void testDeterminePairsToCrossover_SmallPopulation() {
		int initialPopulationSize = 100;
		int actualPopulationSize = 50;

		Population populationMock = mock(Population.class);
		when(populationMock.size()).thenReturn(actualPopulationSize);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double crossoverRate = 0.5;
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		long pairsToCrossover = basicGeneticAlgorithm
				.determinePairsToCrossover(initialPopulationSize);

		assertEquals(25, pairsToCrossover);
	}

	@Test
	public void testMutate() {
		int initialPopulationSize = 100;
		int index = 0;

		Population populationMock = mock(Population.class);
		when(populationMock.selectIndex()).thenReturn(index);
		when(populationMock.getIndividuals()).thenReturn(Arrays.asList(mock(Chromosome.class)));
		when(populationMock.size()).thenReturn(initialPopulationSize);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double mutationRate = 0.5;
		strategyToSet.setMutationRate(mutationRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, basicGeneticAlgorithm,
				mutationAlgorithmMock);

		basicGeneticAlgorithm.mutate(initialPopulationSize);

		verify(populationMock, times(50)).selectIndex();
		verify(populationMock, times(50)).getIndividuals();
		verify(populationMock, times(50)).makeIneligibleForReproduction(index);
		verify(populationMock, times(50)).addIndividualAsIneligible(any(Chromosome.class));
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);

		verify(mutationAlgorithmMock, times(50)).mutateChromosome(any(Chromosome.class));
		verifyNoMoreInteractions(mutationAlgorithmMock);
	}

	@Test
	public void testMutate_SmallPopulation() {
		int initialPopulationSize = 100;
		int actualPopulationSize = 25;
		int index = 0;

		Population populationMock = mock(Population.class);
		when(populationMock.selectIndex()).thenReturn(index);
		when(populationMock.getIndividuals()).thenReturn(Arrays.asList(mock(Chromosome.class)));
		when(populationMock.size()).thenReturn(actualPopulationSize);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double mutationRate = 0.5;
		strategyToSet.setMutationRate(mutationRate);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, basicGeneticAlgorithm,
				mutationAlgorithmMock);

		basicGeneticAlgorithm.mutate(initialPopulationSize);

		verify(populationMock, times(actualPopulationSize)).selectIndex();
		verify(populationMock, times(actualPopulationSize)).getIndividuals();
		verify(populationMock, times(actualPopulationSize)).makeIneligibleForReproduction(index);
		verify(populationMock, times(actualPopulationSize)).addIndividualAsIneligible(
				any(Chromosome.class));
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);

		verify(mutationAlgorithmMock, times(actualPopulationSize)).mutateChromosome(
				any(Chromosome.class));
		verifyNoMoreInteractions(mutationAlgorithmMock);
	}

	@Test
	public void testSpawnInitialPopulation() {
		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		int populationSize = 100;
		strategyToSet.setPopulationSize(populationSize);

		Population populationMock = mock(Population.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setPopulation(populationMock);

		Field strategyField = ReflectionUtils.findField(BasicGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, basicGeneticAlgorithm, strategyToSet);

		basicGeneticAlgorithm.spawnInitialPopulation();

		verify(populationMock, times(1)).clearIndividuals();
		verify(populationMock, times(1)).breed(eq(populationSize));
		verify(populationMock, times(1)).evaluateFitness(null);
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);
	}

	@Test
	public void testPersistStatistics() {
		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();
		basicGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, basicGeneticAlgorithm,
				executionStatistics);

		basicGeneticAlgorithm.persistStatistics();

		verify(executionStatisticsDaoToSet, times(1)).insert(same(executionStatistics));
	}

	@Test
	public void testRequestStop() {
		BasicGeneticAlgorithm basicGeneticAlgorithm = new BasicGeneticAlgorithm();

		Field stopRequestedField = ReflectionUtils.findField(BasicGeneticAlgorithm.class,
				"stopRequested");
		ReflectionUtils.makeAccessible(stopRequestedField);
		boolean stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				basicGeneticAlgorithm);

		assertEquals(false, stopRequestedFromObject);

		basicGeneticAlgorithm.requestStop();

		stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				basicGeneticAlgorithm);

		assertEquals(true, stopRequestedFromObject);
	}
}
