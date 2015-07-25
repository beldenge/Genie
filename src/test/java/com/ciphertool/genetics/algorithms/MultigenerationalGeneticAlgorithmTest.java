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
import com.ciphertool.genetics.algorithms.mutation.NonUniformMutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;

public class MultigenerationalGeneticAlgorithmTest {
	private static final double DEFAULT_FITNESS_VALUE = 100.0;

	@Test
	public void testSetPopulation() {
		Population populationToSet = mock(Population.class);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationToSet);

		Population populationFromObject = multigenerationalGeneticAlgorithm.getPopulation();

		assertSame(populationToSet, populationFromObject);
	}

	@Test
	public void testSetExecutionStatisticsDao() {
		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		Field executionStatisticsDaoField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"executionStatisticsDao");
		ReflectionUtils.makeAccessible(executionStatisticsDaoField);
		ExecutionStatisticsDao executionStatisticsDaoFromObject = (ExecutionStatisticsDao) ReflectionUtils.getField(
				executionStatisticsDaoField, multigenerationalGeneticAlgorithm);

		assertSame(executionStatisticsDaoToSet, executionStatisticsDaoFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSetStrategy() {
		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();

		Population populationMock = mock(Population.class);
		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);
		NonUniformMutationAlgorithm mutationAlgorithmMock = mock(NonUniformMutationAlgorithm.class);
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

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);
		multigenerationalGeneticAlgorithm.setStrategy(strategyToSet);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		GeneticAlgorithmStrategy strategyFromObject = (GeneticAlgorithmStrategy) ReflectionUtils.getField(
				strategyField, multigenerationalGeneticAlgorithm);

		assertSame(strategyToSet, strategyFromObject);
		verify(populationMock, times(1)).setGeneticStructure(same(geneticStructure));
		verify(populationMock, times(1)).setFitnessEvaluator(same(fitnessEvaluatorMock));
		verify(populationMock, times(1)).setLifespan(eq(lifeSpan));
		verify(populationMock, times(1)).setKnownSolutionFitnessEvaluator(same(knownSolutionFitnessEvaluatorMock));
		verify(populationMock, times(1)).setCompareToKnownSolution(eq(compareToKnownSolution));
		verifyNoMoreInteractions(populationMock);

		verify(crossoverAlgorithmMock, times(1)).setMutationAlgorithm(same(mutationAlgorithmMock));
		verify(crossoverAlgorithmMock, times(1)).setMutateDuringCrossover(eq(mutateDuringCrossover));
		verifyNoMoreInteractions(crossoverAlgorithmMock);

		verify(mutationAlgorithmMock, times(1)).setMaxMutationsPerChromosome(eq(maxMutationsPerIndividual));
		verifyNoMoreInteractions(mutationAlgorithmMock);

		Field crossoverAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		CrossoverAlgorithm crossoverAlgorithmFromObject = (CrossoverAlgorithm) ReflectionUtils.getField(
				crossoverAlgorithmField, multigenerationalGeneticAlgorithm);

		assertSame(crossoverAlgorithmMock, crossoverAlgorithmFromObject);

		Field mutationAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils.getField(
				mutationAlgorithmField, multigenerationalGeneticAlgorithm);

		assertSame(mutationAlgorithmMock, mutationAlgorithmFromObject);

		Field selectionAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		SelectionAlgorithm selectionAlgorithmFromObject = (SelectionAlgorithm) ReflectionUtils.getField(
				selectionAlgorithmField, multigenerationalGeneticAlgorithm);

		assertSame(selectionAlgorithmMock, selectionAlgorithmFromObject);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testInitialize() throws InterruptedException {
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

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		Field generationCountField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, multigenerationalGeneticAlgorithm, 1);

		Field stopRequestedField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "stopRequested");
		ReflectionUtils.makeAccessible(stopRequestedField);
		ReflectionUtils.setField(stopRequestedField, multigenerationalGeneticAlgorithm, true);

		Field executionStatisticsField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ExecutionStatistics executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils.getField(
				executionStatisticsField, multigenerationalGeneticAlgorithm);
		assertNull(executionStatisticsFromObject);

		multigenerationalGeneticAlgorithm.initialize();

		int generationCountFromObject = (int) ReflectionUtils.getField(generationCountField,
				multigenerationalGeneticAlgorithm);
		boolean stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				multigenerationalGeneticAlgorithm);
		executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils.getField(executionStatisticsField,
				multigenerationalGeneticAlgorithm);

		assertEquals(0, generationCountFromObject);
		assertFalse(stopRequestedFromObject);
		assertNotNull(executionStatisticsFromObject);
		assertTrue(executionStatisticsFromObject.getStartDateTime().getTime() >= beforeInitialize.getTime());
		assertEquals(populationSize, executionStatisticsFromObject.getPopulationSize().intValue());
		assertEquals(lifeSpan, executionStatisticsFromObject.getLifespan().intValue());
		assertEquals(new Double(survivalRate), executionStatisticsFromObject.getSurvivalRate());
		assertEquals(new Double(mutationRate), executionStatisticsFromObject.getMutationRate());
		assertEquals(new Double(crossoverRate), executionStatisticsFromObject.getCrossoverRate());
		assertEquals(crossoverAlgorithmMock.getClass().getSimpleName(), executionStatisticsFromObject
				.getCrossoverAlgorithm());
		assertEquals(fitnessEvaluatorMock.getClass().getSimpleName(), executionStatisticsFromObject
				.getFitnessEvaluator());
		assertEquals(mutationAlgorithmMock.getClass().getSimpleName(), executionStatisticsFromObject
				.getMutationAlgorithm());

		verify(populationMock, times(1)).clearIndividuals();
		verify(populationMock, times(1)).breed(eq(populationSize));
		verify(populationMock, times(1)).evaluateFitness(any(GenerationStatistics.class));
		verify(populationMock, times(1)).size();
		verify(populationMock, times(1)).setStopRequested(false);
		verifyNoMoreInteractions(populationMock);
	}

	@Test
	public void testFinish() {
		Date beforeFinish = new Date();

		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, multigenerationalGeneticAlgorithm, executionStatistics);

		Field generationCountField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, multigenerationalGeneticAlgorithm, 1);

		multigenerationalGeneticAlgorithm.finish();

		assertTrue(executionStatistics.getEndDateTime().getTime() >= beforeFinish.getTime());

		verify(executionStatisticsDaoToSet, times(1)).insert(same(executionStatistics));

		ExecutionStatistics executionStatisticsFromObject = (ExecutionStatistics) ReflectionUtils.getField(
				executionStatisticsField, multigenerationalGeneticAlgorithm);
		assertNull(executionStatisticsFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testProceedWithNextGeneration() throws InterruptedException {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

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
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setPopulationSize(populationSize);
		strategyToSet.setSurvivalRate(survivalRate);
		strategyToSet.setMutationRate(mutationRate);
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		SelectionAlgorithm selectionAlgorithmMock = mock(SelectionAlgorithm.class);

		Field selectionAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		ReflectionUtils.setField(selectionAlgorithmField, multigenerationalGeneticAlgorithm, selectionAlgorithmMock);

		Field generationCountField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"generationCount");
		ReflectionUtils.makeAccessible(generationCountField);
		ReflectionUtils.setField(generationCountField, multigenerationalGeneticAlgorithm, 0);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, multigenerationalGeneticAlgorithm, mutationAlgorithmMock);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, multigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class))).thenReturn(
				Arrays.asList(chromosomeToReturn));

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, multigenerationalGeneticAlgorithm, executionStatistics);

		assertEquals(0, executionStatistics.getGenerationStatisticsList().size());

		multigenerationalGeneticAlgorithm.proceedWithNextGeneration();

		assertEquals(1, executionStatistics.getGenerationStatisticsList().size());

		/*
		 * The population size should be reduced by the number of parents used during crossover.
		 */
		assertEquals(100, populationMock.size());

		int generationCountFromObject = (int) ReflectionUtils.getField(generationCountField,
				multigenerationalGeneticAlgorithm);
		assertEquals(1, generationCountFromObject);

		verify(selectionAlgorithmMock, times(1)).select(same(populationMock), eq(populationSize), eq(survivalRate));
		verifyNoMoreInteractions(selectionAlgorithmMock);

		verify(populationMock, times(1)).backupIndividuals();
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

		verify(crossoverAlgorithmMock, times(10)).crossover(any(Chromosome.class), any(Chromosome.class));
		verifyNoMoreInteractions(crossoverAlgorithmMock);
	}

	@Test
	public void testValidateParameters_NoErrors() {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

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

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);
	}

	@Test
	public void testValidateParameters_AllErrors() {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setGeneticStructure(null);
		strategyToSet.setPopulationSize(0);
		strategyToSet.setLifespan(null);

		/*
		 * This must be set via reflection because the setter method does its own validation
		 */
		Field survivalRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class, "survivalRate");
		ReflectionUtils.makeAccessible(survivalRateField);
		ReflectionUtils.setField(survivalRateField, strategyToSet, -0.1);

		/*
		 * This must be set via reflection because the setter method does its own validation
		 */
		Field mutationRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class, "mutationRate");
		ReflectionUtils.makeAccessible(mutationRateField);
		ReflectionUtils.setField(mutationRateField, strategyToSet, -0.1);

		strategyToSet.setMaxMutationsPerIndividual(-1);

		/*
		 * This must be set via reflection because the setter method does its own validation
		 */
		Field crossoverRateField = ReflectionUtils.findField(GeneticAlgorithmStrategy.class, "crossoverRate");
		ReflectionUtils.makeAccessible(crossoverRateField);
		ReflectionUtils.setField(crossoverRateField, strategyToSet, -0.1);

		strategyToSet.setMutateDuringCrossover(null);
		strategyToSet.setMaxGenerations(0);
		strategyToSet.setCrossoverAlgorithm(null);
		strategyToSet.setFitnessEvaluator(null);
		strategyToSet.setMutationAlgorithm(null);
		strategyToSet.setSelectionAlgorithm(null);
		strategyToSet.setSelector(null);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		boolean exceptionCaught = false;

		try {
			multigenerationalGeneticAlgorithm.validateParameters();
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
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

		int populationSize = 100;
		double survivalRate = 0.9;

		Population population = new Population();
		multigenerationalGeneticAlgorithm.setPopulation(population);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		strategyToSet.setPopulationSize(populationSize);
		strategyToSet.setSurvivalRate(survivalRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		SelectionAlgorithm selectionAlgorithmMock = mock(SelectionAlgorithm.class);

		Field selectionAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"selectionAlgorithm");
		ReflectionUtils.makeAccessible(selectionAlgorithmField);
		ReflectionUtils.setField(selectionAlgorithmField, multigenerationalGeneticAlgorithm, selectionAlgorithmMock);

		multigenerationalGeneticAlgorithm.select();

		verify(selectionAlgorithmMock, times(1)).select(same(population), eq(populationSize), eq(survivalRate));
		verifyNoMoreInteractions(selectionAlgorithmMock);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testCrossover() throws InterruptedException {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

		Population population = new Population();
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

		multigenerationalGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, multigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Chromosome chromosomeToReturn = new MockKeylessChromosome();
		when(crossoverAlgorithmMock.crossover(any(Chromosome.class), any(Chromosome.class))).thenReturn(
				Arrays.asList(chromosomeToReturn));

		GeneticAlgorithmStrategy strategy = new GeneticAlgorithmStrategy();
		strategy.setCrossoverRate(0.1);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategy);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = multigenerationalGeneticAlgorithm.crossover(initialPopulationSize);

		ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(5, childrenProduced);

		/*
		 * The population size should be reduced by the number of parents used during crossover.
		 */
		assertEquals(40, population.size());

		// There should be 10 ineligible parents, along with the 5 children
		assertEquals(15, ineligibleForReproductionFromObject.size());

		verify(selectorMock, times(10)).getNextIndex(anyListOf(Chromosome.class), any(Double.class));

		verify(crossoverAlgorithmMock, times(5)).crossover(any(Chromosome.class), any(Chromosome.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testCrossover_SmallPopulation() throws InterruptedException {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

		Population population = new Population();

		Chromosome chromosome = new MockKeylessChromosome();
		population.addIndividual(chromosome);
		multigenerationalGeneticAlgorithm.setPopulation(population);

		CrossoverAlgorithm crossoverAlgorithmMock = mock(CrossoverAlgorithm.class);

		Field crossoverAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"crossoverAlgorithm");
		ReflectionUtils.makeAccessible(crossoverAlgorithmField);
		ReflectionUtils.setField(crossoverAlgorithmField, multigenerationalGeneticAlgorithm, crossoverAlgorithmMock);

		Field ineligibleForReproductionField = ReflectionUtils.findField(Population.class, "ineligibleForReproduction");
		ReflectionUtils.makeAccessible(ineligibleForReproductionField);
		List<Chromosome> ineligibleForReproductionFromObject = (List<Chromosome>) ReflectionUtils.getField(
				ineligibleForReproductionField, population);

		assertEquals(0, ineligibleForReproductionFromObject.size());

		int childrenProduced = multigenerationalGeneticAlgorithm.crossover(10);

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

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double crossoverRate = 0.5;
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		long pairsToCrossover = multigenerationalGeneticAlgorithm.determinePairsToCrossover(initialPopulationSize);

		assertEquals(50, pairsToCrossover);
	}

	@Test
	public void testDeterminePairsToCrossover_SmallPopulation() {
		int initialPopulationSize = 100;
		int actualPopulationSize = 50;

		Population populationMock = mock(Population.class);
		when(populationMock.size()).thenReturn(actualPopulationSize);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double crossoverRate = 0.5;
		strategyToSet.setCrossoverRate(crossoverRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		long pairsToCrossover = multigenerationalGeneticAlgorithm.determinePairsToCrossover(initialPopulationSize);

		assertEquals(25, pairsToCrossover);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMutate() throws InterruptedException {
		int initialPopulationSize = 100;
		int index = 0;

		Population populationMock = mock(Population.class);
		when(populationMock.selectIndex()).thenReturn(index);
		when(populationMock.getIndividuals()).thenReturn(Arrays.asList(mock(Chromosome.class)));
		when(populationMock.size()).thenReturn(initialPopulationSize);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double mutationRate = 0.5;
		strategyToSet.setMutationRate(mutationRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, multigenerationalGeneticAlgorithm, mutationAlgorithmMock);

		multigenerationalGeneticAlgorithm.mutate(initialPopulationSize);

		verify(populationMock, times(50)).selectIndex();
		verify(populationMock, times(50)).getIndividuals();
		verify(populationMock, times(50)).makeIneligibleForReproduction(index);
		verify(populationMock, times(50)).addIndividualAsIneligible(any(Chromosome.class));
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);

		verify(mutationAlgorithmMock, times(50)).mutateChromosome(any(Chromosome.class));
		verifyNoMoreInteractions(mutationAlgorithmMock);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testMutate_SmallPopulation() throws InterruptedException {
		int initialPopulationSize = 100;
		int actualPopulationSize = 25;
		int index = 0;

		Population populationMock = mock(Population.class);
		when(populationMock.selectIndex()).thenReturn(index);
		when(populationMock.getIndividuals()).thenReturn(Arrays.asList(mock(Chromosome.class)));
		when(populationMock.size()).thenReturn(actualPopulationSize);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		double mutationRate = 0.5;
		strategyToSet.setMutationRate(mutationRate);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		MutationAlgorithm mutationAlgorithmMock = mock(MutationAlgorithm.class);
		Field mutationAlgorithmField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		ReflectionUtils.setField(mutationAlgorithmField, multigenerationalGeneticAlgorithm, mutationAlgorithmMock);

		multigenerationalGeneticAlgorithm.mutate(initialPopulationSize);

		verify(populationMock, times(actualPopulationSize)).selectIndex();
		verify(populationMock, times(actualPopulationSize)).getIndividuals();
		verify(populationMock, times(actualPopulationSize)).makeIneligibleForReproduction(index);
		verify(populationMock, times(actualPopulationSize)).addIndividualAsIneligible(any(Chromosome.class));
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);

		verify(mutationAlgorithmMock, times(actualPopulationSize)).mutateChromosome(any(Chromosome.class));
		verifyNoMoreInteractions(mutationAlgorithmMock);
	}

	@Test
	public void testSpawnInitialPopulation() throws InterruptedException {
		GeneticAlgorithmStrategy strategyToSet = new GeneticAlgorithmStrategy();
		int populationSize = 100;
		strategyToSet.setPopulationSize(populationSize);

		Population populationMock = mock(Population.class);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		Field strategyField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "strategy");
		ReflectionUtils.makeAccessible(strategyField);
		ReflectionUtils.setField(strategyField, multigenerationalGeneticAlgorithm, strategyToSet);

		multigenerationalGeneticAlgorithm.spawnInitialPopulation();

		verify(populationMock, times(1)).clearIndividuals();
		verify(populationMock, times(1)).breed(eq(populationSize));
		verify(populationMock, times(1)).evaluateFitness(any(GenerationStatistics.class));
		verify(populationMock, times(1)).size();
		verifyNoMoreInteractions(populationMock);
	}

	@Test
	public void testPersistStatistics() {
		ExecutionStatisticsDao executionStatisticsDaoToSet = mock(ExecutionStatisticsDao.class);

		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();
		multigenerationalGeneticAlgorithm.setExecutionStatisticsDao(executionStatisticsDaoToSet);

		ExecutionStatistics executionStatistics = new ExecutionStatistics();
		Field executionStatisticsField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class,
				"executionStatistics");
		ReflectionUtils.makeAccessible(executionStatisticsField);
		ReflectionUtils.setField(executionStatisticsField, multigenerationalGeneticAlgorithm, executionStatistics);

		multigenerationalGeneticAlgorithm.persistStatistics();

		verify(executionStatisticsDaoToSet, times(1)).insert(same(executionStatistics));
	}

	@Test
	public void testRequestStop() {
		MultigenerationalGeneticAlgorithm multigenerationalGeneticAlgorithm = new MultigenerationalGeneticAlgorithm();

		Population populationMock = mock(Population.class);
		multigenerationalGeneticAlgorithm.setPopulation(populationMock);

		Field stopRequestedField = ReflectionUtils.findField(MultigenerationalGeneticAlgorithm.class, "stopRequested");
		ReflectionUtils.makeAccessible(stopRequestedField);
		boolean stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				multigenerationalGeneticAlgorithm);

		assertEquals(false, stopRequestedFromObject);

		multigenerationalGeneticAlgorithm.requestStop();

		stopRequestedFromObject = (boolean) ReflectionUtils.getField(stopRequestedField,
				multigenerationalGeneticAlgorithm);

		verify(populationMock, times(1)).requestStop();
		verifyNoMoreInteractions(populationMock);
		assertEquals(true, stopRequestedFromObject);
	}
}
