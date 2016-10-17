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

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.crossover.EvaluatedCrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.EvaluatedMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class GeneticAlgorithmStrategy {
	private Object				geneticStructure;
	private Integer				populationSize;
	private Integer				lifespan;
	private Double				survivalRate;
	private Double				mutationRate;
	private Integer				maxMutationsPerIndividual;
	private Double				crossoverRate;
	private Boolean				mutateDuringCrossover;
	private Integer				maxGenerations;
	@SuppressWarnings("rawtypes")
	private CrossoverAlgorithm	crossoverAlgorithm;
	private FitnessEvaluator	fitnessEvaluator;
	@SuppressWarnings("rawtypes")
	private MutationAlgorithm	mutationAlgorithm;
	private SelectionAlgorithm	selectionAlgorithm;
	private Selector			selector;
	private FitnessEvaluator	knownSolutionFitnessEvaluator;
	private Boolean				compareToKnownSolution;

	/**
	 * Default no-args constructor
	 */
	public GeneticAlgorithmStrategy() {
	}

	/**
	 * Full-args constructor
	 * 
	 * @param geneticStructure
	 *            the geneticStructure to set
	 * @param populationSize
	 *            the populationSize to set
	 * @param maxGenerations
	 *            the maxGenerations to set
	 * @param survivalRate
	 *            the survivalRate to set
	 * @param mutationRate
	 *            the mutationRate to set
	 * @param maxMutationsPerIndividual
	 *            the maxMutationsPerIndividual to set
	 * @param crossoverRate
	 *            the crossoverRate to set
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 * @param selectionAlgorithm
	 *            the selectionAlgorithm to set
	 * @param selector
	 *            the selector to set
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 * @param useKnownSolutionFitnessEvaluator
	 *            the useKnownSolutionFitnessEvaluator to set
	 */
	@SuppressWarnings("rawtypes")
	public GeneticAlgorithmStrategy(Object geneticStructure, Integer populationSize, Integer lifespan,
			Integer maxGenerations, Double survivalRate, Double mutationRate, Integer maxMutationsPerIndividual,
			Double crossoverRate, Boolean mutateDuringCrossover, FitnessEvaluator fitnessEvaluator,
			CrossoverAlgorithm crossoverAlgorithm, MutationAlgorithm mutationAlgorithm,
			SelectionAlgorithm selectionAlgorithm, Selector selector, FitnessEvaluator knownSolutionFitnessEvaluator,
			Boolean compareToKnownSolution) {
		this.geneticStructure = geneticStructure;
		this.populationSize = populationSize;
		this.lifespan = lifespan;
		this.maxGenerations = maxGenerations;
		this.setSurvivalRate(survivalRate);

		this.setMutationRate(mutationRate);
		this.maxMutationsPerIndividual = maxMutationsPerIndividual;

		this.setCrossoverRate(crossoverRate);
		this.mutateDuringCrossover = mutateDuringCrossover;

		this.fitnessEvaluator = fitnessEvaluator;
		this.fitnessEvaluator.setGeneticStructure(geneticStructure);

		this.crossoverAlgorithm = crossoverAlgorithm;

		if (crossoverAlgorithm instanceof EvaluatedCrossoverAlgorithm) {
			((EvaluatedCrossoverAlgorithm) this.crossoverAlgorithm).setFitnessEvaluator(this.fitnessEvaluator);
		}

		this.mutationAlgorithm = mutationAlgorithm;

		if (mutationAlgorithm instanceof EvaluatedMutationAlgorithm) {
			((EvaluatedMutationAlgorithm) this.mutationAlgorithm).setFitnessEvaluator(this.fitnessEvaluator);
		}

		this.selectionAlgorithm = selectionAlgorithm;
		this.selector = selector;

		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
		if (knownSolutionFitnessEvaluator != null) {
			this.knownSolutionFitnessEvaluator.setGeneticStructure(geneticStructure);
		}
		this.compareToKnownSolution = compareToKnownSolution;
	}

	/**
	 * @return the geneticStructure
	 */
	public Object getGeneticStructure() {
		return geneticStructure;
	}

	/**
	 * @param geneticStructure
	 *            the geneticStructure to set
	 */
	public void setGeneticStructure(Object geneticStructure) {
		this.geneticStructure = geneticStructure;
	}

	/**
	 * @return the populationSize
	 */
	public Integer getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the lifespan
	 */
	public Integer getLifespan() {
		return lifespan;
	}

	/**
	 * @param lifespan
	 *            the lifespan to set
	 */
	public void setLifespan(Integer lifespan) {
		this.lifespan = lifespan;
	}

	/**
	 * @return the survivalRate
	 */
	public Double getSurvivalRate() {
		return survivalRate;
	}

	/**
	 * @param survivalRate
	 *            the survivalRate to set
	 */
	public void setSurvivalRate(Double survivalRate) {
		if (survivalRate == null || survivalRate < 0.0 || survivalRate > 1.0) {
			throw new IllegalArgumentException("Tried to set a survivalRate of " + survivalRate
					+ ", but GeneicAlgorithmStrategy requires a survivalRate between 0.0 and 1.0 inclusive.");
		}

		this.survivalRate = survivalRate;
	}

	/**
	 * @return the mutationRate
	 */
	public Double getMutationRate() {
		return mutationRate;
	}

	/**
	 * @param mutationRate
	 *            the mutationRate to set
	 */
	public void setMutationRate(Double mutationRate) {
		if (mutationRate == null || mutationRate < 0.0 || mutationRate > 1.0) {
			throw new IllegalArgumentException("Tried to set a mutationRate of " + mutationRate
					+ ", but GeneicAlgorithmStrategy requires a mutationRate between 0.0 and 1.0 inclusive.");
		}

		this.mutationRate = mutationRate;
	}

	/**
	 * @return the maxMutationsPerIndividual
	 */
	public Integer getMaxMutationsPerIndividual() {
		return maxMutationsPerIndividual;
	}

	/**
	 * @param maxMutationsPerIndividual
	 *            the maxMutationsPerIndividual to set
	 */
	public void setMaxMutationsPerIndividual(Integer maxMutationsPerIndividual) {
		this.maxMutationsPerIndividual = maxMutationsPerIndividual;
	}

	/**
	 * @return the crossoverRate
	 */
	public Double getCrossoverRate() {
		return crossoverRate;
	}

	/**
	 * @param crossoverRate
	 *            the crossoverRate to set
	 */
	public void setCrossoverRate(Double crossoverRate) {
		if (crossoverRate == null || crossoverRate < 0.0 || crossoverRate > 1.0) {
			throw new IllegalArgumentException("Tried to set a crossoverRate of " + crossoverRate
					+ ", but GeneicAlgorithmStrategy requires a crossoverRate between 0.0 and 1.0 inclusive.");
		}

		this.crossoverRate = crossoverRate;
	}

	/**
	 * @return the maxGenerations
	 */
	public Integer getMaxGenerations() {
		return maxGenerations;
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	public void setMaxGenerations(Integer maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @return the crossoverAlgorithm
	 */
	@SuppressWarnings("rawtypes")
	public CrossoverAlgorithm getCrossoverAlgorithm() {
		return crossoverAlgorithm;
	}

	/**
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	@SuppressWarnings("rawtypes")
	public void setCrossoverAlgorithm(CrossoverAlgorithm crossoverAlgorithm) {
		this.crossoverAlgorithm = crossoverAlgorithm;
	}

	/**
	 * @return the fitnessEvaluator
	 */
	public FitnessEvaluator getFitnessEvaluator() {
		return fitnessEvaluator;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @return the mutationAlgorithm
	 */
	@SuppressWarnings("rawtypes")
	public MutationAlgorithm getMutationAlgorithm() {
		return mutationAlgorithm;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@SuppressWarnings("rawtypes")
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @return the selectionAlgorithm
	 */
	public SelectionAlgorithm getSelectionAlgorithm() {
		return selectionAlgorithm;
	}

	/**
	 * @param selectionAlgorithm
	 *            the selectionAlgorithm to set
	 */
	public void setSelectionAlgorithm(SelectionAlgorithm selectionAlgorithm) {
		this.selectionAlgorithm = selectionAlgorithm;
	}

	/**
	 * @return the selector
	 */
	public Selector getSelector() {
		return selector;
	}

	/**
	 * @param selector
	 *            the selector to set
	 */
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	/**
	 * @return the knownSolutionFitnessEvaluator
	 */
	public FitnessEvaluator getKnownSolutionFitnessEvaluator() {
		return knownSolutionFitnessEvaluator;
	}

	/**
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 */
	public void setKnownSolutionFitnessEvaluator(FitnessEvaluator knownSolutionFitnessEvaluator) {
		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
	}

	/**
	 * @return the compareToKnownSolution
	 */
	public Boolean getCompareToKnownSolution() {
		return compareToKnownSolution;
	}

	/**
	 * @param compareToKnownSolution
	 *            the compareToKnownSolution to set
	 */
	public void setCompareToKnownSolution(Boolean compareToKnownSolution) {
		this.compareToKnownSolution = compareToKnownSolution;
	}

	/**
	 * @return the mutateDuringCrossover
	 */
	public Boolean getMutateDuringCrossover() {
		return mutateDuringCrossover;
	}

	/**
	 * @param mutateDuringCrossover
	 *            the mutateDuringCrossover to set
	 */
	public void setMutateDuringCrossover(Boolean mutateDuringCrossover) {
		this.mutateDuringCrossover = mutateDuringCrossover;
	}
}
