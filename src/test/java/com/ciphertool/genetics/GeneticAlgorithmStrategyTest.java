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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.crossover.impl.EqualOpportunityGeneCrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.impl.MultipleMutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.ProbabilisticSelectionAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.RouletteSelector;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class GeneticAlgorithmStrategyTest {
	private static final double DEFAULT_FITNESS_VALUE = 100.0;

	@SuppressWarnings("rawtypes")
	@Test
	public void testConstructorWithoutComparisonToKnownSolution() {
		Object geneticStructureToSet = new Object();
		Integer populationSizeToSet = new Integer(500);
		Integer lifespanToSet = new Integer(10);
		Integer maxGenerationsToSet = new Integer(1000);
		Double survivalRateToSet = new Double(0.9);
		Double mutationRateToSet = new Double(0.05);
		Integer maxMutationsPerIndividualToSet = new Integer(5);
		Double crossoverRateToSet = new Double(0.1);
		Boolean mutateDuringCrossoverToSet = new Boolean(true);
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorToSet.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		CrossoverAlgorithm crossoverAlgorithmToSet = new EqualOpportunityGeneCrossoverAlgorithm();
		MutationAlgorithm mutationAlgorithmToSet = new MultipleMutationAlgorithm();
		SelectionAlgorithm selectionAlgorithmToSet = new ProbabilisticSelectionAlgorithm();
		Selector selectorToSet = new RouletteSelector();

		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy(geneticStructureToSet,
				populationSizeToSet, lifespanToSet, maxGenerationsToSet, survivalRateToSet, mutationRateToSet,
				maxMutationsPerIndividualToSet, crossoverRateToSet, mutateDuringCrossoverToSet, fitnessEvaluatorToSet,
				crossoverAlgorithmToSet, mutationAlgorithmToSet, selectionAlgorithmToSet, selectorToSet, null, false);

		assertSame(geneticStructureToSet, geneticAlgorithmStrategy.getGeneticStructure());
		assertSame(populationSizeToSet, geneticAlgorithmStrategy.getPopulationSize());
		assertSame(lifespanToSet, geneticAlgorithmStrategy.getLifespan());
		assertSame(maxGenerationsToSet, geneticAlgorithmStrategy.getMaxGenerations());
		assertSame(survivalRateToSet, geneticAlgorithmStrategy.getSurvivalRate());
		assertSame(mutationRateToSet, geneticAlgorithmStrategy.getMutationRate());
		assertSame(maxMutationsPerIndividualToSet, geneticAlgorithmStrategy.getMaxMutationsPerIndividual());
		assertSame(crossoverRateToSet, geneticAlgorithmStrategy.getCrossoverRate());
		assertSame(mutateDuringCrossoverToSet, geneticAlgorithmStrategy.getMutateDuringCrossover());
		assertSame(fitnessEvaluatorToSet, geneticAlgorithmStrategy.getFitnessEvaluator());
		assertSame(crossoverAlgorithmToSet, geneticAlgorithmStrategy.getCrossoverAlgorithm());
		assertSame(mutationAlgorithmToSet, geneticAlgorithmStrategy.getMutationAlgorithm());
		assertSame(selectionAlgorithmToSet, geneticAlgorithmStrategy.getSelectionAlgorithm());
		assertSame(selectorToSet, geneticAlgorithmStrategy.getSelector());
		assertNull(geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
		assertFalse(geneticAlgorithmStrategy.getCompareToKnownSolution());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testConstructorWithComparisonToKnownSolution() {
		Object geneticStructureToSet = new Object();
		Integer populationSizeToSet = new Integer(500);
		Integer lifespanToSet = new Integer(10);
		Integer maxGenerationsToSet = new Integer(1000);
		Double survivalRateToSet = new Double(0.9);
		Double mutationRateToSet = new Double(0.05);
		Integer maxMutationsPerIndividualToSet = new Integer(5);
		Double crossoverRateToSet = new Double(0.1);
		Boolean mutateDuringCrossoverToSet = new Boolean(true);
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorToSet.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		CrossoverAlgorithm crossoverAlgorithmToSet = new EqualOpportunityGeneCrossoverAlgorithm();
		MutationAlgorithm mutationAlgorithmToSet = new MultipleMutationAlgorithm();
		SelectionAlgorithm selectionAlgorithmToSet = new ProbabilisticSelectionAlgorithm();
		Selector selectorToSet = new RouletteSelector();
		FitnessEvaluator knownSolutionFitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorToSet.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		Boolean compareToKnownSolutionToSet = new Boolean(true);

		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy(geneticStructureToSet,
				populationSizeToSet, lifespanToSet, maxGenerationsToSet, survivalRateToSet, mutationRateToSet,
				maxMutationsPerIndividualToSet, crossoverRateToSet, mutateDuringCrossoverToSet, fitnessEvaluatorToSet,
				crossoverAlgorithmToSet, mutationAlgorithmToSet, selectionAlgorithmToSet, selectorToSet,
				knownSolutionFitnessEvaluatorToSet, compareToKnownSolutionToSet);

		assertSame(geneticStructureToSet, geneticAlgorithmStrategy.getGeneticStructure());
		assertSame(populationSizeToSet, geneticAlgorithmStrategy.getPopulationSize());
		assertSame(lifespanToSet, geneticAlgorithmStrategy.getLifespan());
		assertSame(maxGenerationsToSet, geneticAlgorithmStrategy.getMaxGenerations());
		assertSame(survivalRateToSet, geneticAlgorithmStrategy.getSurvivalRate());
		assertSame(mutationRateToSet, geneticAlgorithmStrategy.getMutationRate());
		assertSame(maxMutationsPerIndividualToSet, geneticAlgorithmStrategy.getMaxMutationsPerIndividual());
		assertSame(crossoverRateToSet, geneticAlgorithmStrategy.getCrossoverRate());
		assertSame(mutateDuringCrossoverToSet, geneticAlgorithmStrategy.getMutateDuringCrossover());
		assertSame(fitnessEvaluatorToSet, geneticAlgorithmStrategy.getFitnessEvaluator());
		assertSame(crossoverAlgorithmToSet, geneticAlgorithmStrategy.getCrossoverAlgorithm());
		assertSame(mutationAlgorithmToSet, geneticAlgorithmStrategy.getMutationAlgorithm());
		assertSame(selectionAlgorithmToSet, geneticAlgorithmStrategy.getSelectionAlgorithm());
		assertSame(selectorToSet, geneticAlgorithmStrategy.getSelector());
		assertSame(knownSolutionFitnessEvaluatorToSet, geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
		assertSame(compareToKnownSolutionToSet, geneticAlgorithmStrategy.getCompareToKnownSolution());
	}

	@Test
	public void testSetGeneticStructure() {
		Object geneticStructureToSet = new Object();
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setGeneticStructure(geneticStructureToSet);

		assertSame(geneticStructureToSet, geneticAlgorithmStrategy.getGeneticStructure());
	}

	@Test
	public void testSetPopulationSize() {
		Integer populationSizeToSet = new Integer(500);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setPopulationSize(populationSizeToSet);

		assertSame(populationSizeToSet, geneticAlgorithmStrategy.getPopulationSize());
	}

	@Test
	public void testSetLifespan() {
		Integer lifespanToSet = new Integer(10);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setLifespan(lifespanToSet);

		assertSame(lifespanToSet, geneticAlgorithmStrategy.getLifespan());
	}

	@Test
	public void testSetSurvivalRate() {
		Double survivalRateToSet = new Double(0.9);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setSurvivalRate(survivalRateToSet);

		assertSame(survivalRateToSet, geneticAlgorithmStrategy.getSurvivalRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSurvivalRateInvalid() {
		Double survivalRateToSet = new Double(1.1);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setSurvivalRate(survivalRateToSet);

		assertSame(survivalRateToSet, geneticAlgorithmStrategy.getSurvivalRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSurvivalRateNegative() {
		Double survivalRateToSet = new Double(-0.1);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setSurvivalRate(survivalRateToSet);

		assertSame(survivalRateToSet, geneticAlgorithmStrategy.getSurvivalRate());
	}

	@Test
	public void testSetMutationRate() {
		Double mutationRateToSet = new Double(0.05);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMutationRate(mutationRateToSet);

		assertSame(mutationRateToSet, geneticAlgorithmStrategy.getMutationRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetMutationRateInvalid() {
		Double mutationRateToSet = new Double(1.05);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMutationRate(mutationRateToSet);

		assertSame(mutationRateToSet, geneticAlgorithmStrategy.getMutationRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetMutationRateNegative() {
		Double mutationRateToSet = new Double(-0.05);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMutationRate(mutationRateToSet);

		assertSame(mutationRateToSet, geneticAlgorithmStrategy.getMutationRate());
	}

	@Test
	public void testSetMaxMutationsPerIndividual() {
		Integer maxMutationsPerIndividualToSet = new Integer(5);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMaxMutationsPerIndividual(maxMutationsPerIndividualToSet);

		assertSame(maxMutationsPerIndividualToSet, geneticAlgorithmStrategy.getMaxMutationsPerIndividual());
	}

	@Test
	public void testSetCrossoverRate() {
		Double crossoverRateToSet = new Double(0.1);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setCrossoverRate(crossoverRateToSet);

		assertSame(crossoverRateToSet, geneticAlgorithmStrategy.getCrossoverRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCrossoverRateInvalid() {
		Double crossoverRateToSet = new Double(1.1);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setCrossoverRate(crossoverRateToSet);

		assertSame(crossoverRateToSet, geneticAlgorithmStrategy.getCrossoverRate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCrossoverRateNegative() {
		Double crossoverRateToSet = new Double(-0.1);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setCrossoverRate(crossoverRateToSet);

		assertSame(crossoverRateToSet, geneticAlgorithmStrategy.getCrossoverRate());
	}

	@Test
	public void testSetMutateDuringCrossover() {
		Boolean mutateDuringCrossoverToSet = new Boolean(true);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		assertSame(mutateDuringCrossoverToSet, geneticAlgorithmStrategy.getMutateDuringCrossover());
	}

	@Test
	public void testSetMaxGenerations() {
		Integer maxGenerationsToSet = new Integer(1000);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMaxGenerations(maxGenerationsToSet);

		assertSame(maxGenerationsToSet, geneticAlgorithmStrategy.getMaxGenerations());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testSetCrossoverAlgorithm() {
		CrossoverAlgorithm crossoverAlgorithmToSet = new EqualOpportunityGeneCrossoverAlgorithm();
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setCrossoverAlgorithm(crossoverAlgorithmToSet);

		assertSame(crossoverAlgorithmToSet, geneticAlgorithmStrategy.getCrossoverAlgorithm());
	}

	@Test
	public void testSetFitnessEvaluator() {
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorToSet.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setFitnessEvaluator(fitnessEvaluatorToSet);

		assertSame(fitnessEvaluatorToSet, geneticAlgorithmStrategy.getFitnessEvaluator());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = new MultipleMutationAlgorithm();
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setMutationAlgorithm(mutationAlgorithmToSet);

		assertSame(mutationAlgorithmToSet, geneticAlgorithmStrategy.getMutationAlgorithm());
	}

	@Test
	public void testSetSelectionAlgorithm() {
		SelectionAlgorithm selectionAlgorithmToSet = new ProbabilisticSelectionAlgorithm();
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setSelectionAlgorithm(selectionAlgorithmToSet);

		assertSame(selectionAlgorithmToSet, geneticAlgorithmStrategy.getSelectionAlgorithm());
	}

	@Test
	public void testSetSelector() {
		Selector selectorToSet = new RouletteSelector();
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setSelector(selectorToSet);

		assertSame(selectorToSet, geneticAlgorithmStrategy.getSelector());
	}

	@Test
	public void testSetKnownSolutionFitnessEvaluator() {
		FitnessEvaluator knownSolutionFitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		when(knownSolutionFitnessEvaluatorToSet.evaluate(any(Chromosome.class))).thenReturn(DEFAULT_FITNESS_VALUE);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setKnownSolutionFitnessEvaluator(knownSolutionFitnessEvaluatorToSet);

		assertSame(knownSolutionFitnessEvaluatorToSet, geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
	}

	@Test
	public void testSetCompareToKnownSolution() {
		Boolean compareToKnownSolutionToSet = new Boolean(true);
		GeneticAlgorithmStrategy geneticAlgorithmStrategy = new GeneticAlgorithmStrategy();
		geneticAlgorithmStrategy.setCompareToKnownSolution(compareToKnownSolutionToSet);

		assertSame(compareToKnownSolutionToSet, geneticAlgorithmStrategy.getCompareToKnownSolution());
	}
}
