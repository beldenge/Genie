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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.StandardPopulation;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.NonUniformMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;

public class MultigenerationalGeneticAlgorithm implements GeneticAlgorithm {
	private Logger log = LoggerFactory.getLogger(getClass());

	protected GeneticAlgorithmStrategy strategy;
	protected StandardPopulation population;
	@SuppressWarnings("rawtypes")
	protected CrossoverAlgorithm crossoverAlgorithm;
	@SuppressWarnings("rawtypes")
	protected MutationAlgorithm mutationAlgorithm;
	protected SelectionAlgorithm selectionAlgorithm;
	protected boolean stopRequested;
	private ExecutionStatisticsDao executionStatisticsDao;
	protected int generationCount = 0;
	protected ExecutionStatistics executionStatistics;

	public MultigenerationalGeneticAlgorithm() {
	}

	@Override
	public void evolveAutonomously() {
		try {
			initialize();

			do {
				proceedWithNextGeneration();
			} while (!this.stopRequested
					&& (this.strategy.getMaxGenerations() < 0 || this.generationCount < this.strategy
							.getMaxGenerations()));
		} catch (InterruptedException ie) {
			log.info(ie.getMessage());

			this.population.recoverFromBackup();
		}

		finish();
	}

	@Override
	public void initialize() throws InterruptedException {
		validateParameters();

		this.generationCount = 0;

		this.stopRequested = false;
		this.population.setStopRequested(false);

		Date startDate = new Date();
		this.executionStatistics = new ExecutionStatistics(startDate, this.strategy);

		this.spawnInitialPopulation();
	}

	@Override
	public void finish() {
		long totalExecutionTime = 0;

		for (GenerationStatistics generationStatistics : this.executionStatistics.getGenerationStatisticsList()) {
			if (generationStatistics.getGeneration() == 0) {
				// This is the initial spawning of the population, which will potentially skew the average
				continue;
			}

			totalExecutionTime += generationStatistics.getExecutionTime();
		}

		long averageExecutionTime = 0;

		if (this.generationCount > 1) {
			/*
			 * We subtract 1 from the generation count because the zeroth generation is just the initial spawning of the
			 * population. And, we add one to the result because the remainder from division is truncated due to use of
			 * primitive type long, and we want to round up.
			 */
			averageExecutionTime = (totalExecutionTime / (this.generationCount - 1)) + 1;
		} else {
			averageExecutionTime = totalExecutionTime;
		}

		log.info("Average generation time is " + averageExecutionTime + "ms.");

		this.executionStatistics.setEndDateTime(new Date());

		persistStatistics();

		// This needs to be reset to null in case the algorithm is re-run
		this.executionStatistics = null;
	}

	@Override
	public void proceedWithNextGeneration() throws InterruptedException {
		this.population.backupIndividuals();

		this.generationCount++;

		GenerationStatistics generationStatistics = new GenerationStatistics(this.executionStatistics,
				this.generationCount);

		long generationStart = System.currentTimeMillis();

		int populationSizeBeforeGeneration = this.population.size();

		/*
		 * Doing the select first improves performance by a ratio of up to (1 - survivalRate). It makes more sense as
		 * well since only survivors can reproduce.
		 */
		long startSelect = System.currentTimeMillis();
		int totalDeaths = select();
		if (log.isDebugEnabled()) {
			log.debug("Selection took " + (System.currentTimeMillis() - startSelect) + "ms.");
		}

		/*
		 * We want to increase age before children are produced in following steps so that the children do not age
		 * before having a chance to do anything.
		 */
		long startAging = System.currentTimeMillis();
		totalDeaths += this.population.increaseAge();
		generationStatistics.setNumberSelectedOut(totalDeaths);
		if (log.isDebugEnabled()) {
			log.debug("Aging took " + (System.currentTimeMillis() - startAging) + "ms.");
		}

		if (strategy.getCrossoverRate() > 0.0) {
			long startCrossover = System.currentTimeMillis();
			generationStatistics.setNumberOfCrossovers(crossover(populationSizeBeforeGeneration));
			if (log.isDebugEnabled()) {
				log.debug("Crossover took " + (System.currentTimeMillis() - startCrossover) + "ms.");
			}
		}

		if (strategy.getMutationRate() > 0.0) {
			long startMutation = System.currentTimeMillis();
			generationStatistics.setNumberOfMutations(mutate(populationSizeBeforeGeneration));
			if (log.isDebugEnabled()) {
				log.debug("Mutation took " + (System.currentTimeMillis() - startMutation) + "ms.");
			}
		}

		this.population.resetEligibility();

		/*
		 * Adds new random solutions to the population to fill back to the population size. Ultimately, this should not
		 * be necessary, but it is here as a failsafe in crossover and mutation do not produce enough children.
		 */
		long startBreeding = System.currentTimeMillis();
		generationStatistics.setNumberRandomlyGenerated(this.population.breed(this.strategy.getPopulationSize()));
		if (log.isDebugEnabled()) {
			log.debug("Breeding took " + (System.currentTimeMillis() - startBreeding) + "ms.");
		}

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		if (log.isDebugEnabled()) {
			log.debug("Evaluation took " + (System.currentTimeMillis() - startEvaluation) + "ms.");
		}

		long executionTime = (System.currentTimeMillis() - generationStart);
		generationStatistics.setExecutionTime(executionTime);

		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	protected void validateParameters() {
		List<String> validationErrors = new ArrayList<String>();

		if (strategy.getGeneticStructure() == null) {
			validationErrors.add("Parameter 'geneticStructure' cannot be null.");
		}

		if (strategy.getPopulationSize() == null || strategy.getPopulationSize() <= 0) {
			validationErrors.add("Parameter 'populationSize' must be greater than zero.");
		}

		if (strategy.getLifespan() == null) {
			validationErrors.add("Parameter 'lifespan' cannot be null.");
		}

		if (strategy.getSurvivalRate() == null || strategy.getSurvivalRate() < 0) {
			validationErrors.add("Parameter 'survivalRate' must be greater than zero.");
		}

		if (strategy.getMutationRate() == null || strategy.getMutationRate() < 0) {
			validationErrors.add("Parameter 'mutationRate' must be greater than or equal to zero.");
		}

		if (strategy.getMaxMutationsPerIndividual() == null || strategy.getMaxMutationsPerIndividual() < 0) {
			validationErrors.add("Parameter 'maxMutationsPerIndividual' must be greater than or equal to zero.");
		}

		if (strategy.getCrossoverRate() == null || strategy.getCrossoverRate() < 0) {
			validationErrors.add("Parameter 'crossoverRate' must be greater than or equal to zero.");
		}

		if (strategy.getMutateDuringCrossover() == null) {
			validationErrors.add("Parameter 'mutateDuringCrossover' cannot be null.");
		}

		if (strategy.getMaxGenerations() == null || strategy.getMaxGenerations() == 0) {
			validationErrors.add("Parameter 'maxGenerations' cannot be null and must not equal zero.");
		}

		if (strategy.getCrossoverAlgorithm() == null) {
			validationErrors.add("Parameter 'crossoverAlgorithm' cannot be null.");
		}

		if (strategy.getFitnessEvaluator() == null) {
			validationErrors.add("Parameter 'fitnessEvaluator' cannot be null.");
		}

		if (strategy.getMutationAlgorithm() == null) {
			validationErrors.add("Parameter 'mutationAlgorithm' cannot be null.");
		}

		if (strategy.getSelectionAlgorithm() == null) {
			validationErrors.add("Parameter 'selectionAlgorithm' cannot be null.");
		}

		if (strategy.getSelector() == null) {
			validationErrors.add("Parameter 'selectorMethod' cannot be null.");
		}

		if (validationErrors.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to execute genetic algorithm because one or more of the required parameters are missing.  The validation errors are:");

			for (String validationError : validationErrors) {
				sb.append("\n\t-" + validationError);
			}

			throw new IllegalStateException(sb.toString());
		}
	}

	@Override
	public int select() {
		return selectionAlgorithm.select(this.population, this.strategy.getPopulationSize(), this.strategy
				.getSurvivalRate());
	}

	@SuppressWarnings("unchecked")
	@Override
	public int crossover(int initialPopulationSize) throws InterruptedException {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return 0;
		}

		Chromosome mom = null;
		Chromosome dad = null;
		List<Chromosome> children = null;
		int momIndex = -1;
		int dadIndex = -1;

		long pairsToCrossover = determinePairsToCrossover(initialPopulationSize);

		log.debug("Pairs to crossover: " + pairsToCrossover);

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();
		for (int i = 0; i < pairsToCrossover; i++) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during crossover.");
			}

			momIndex = this.population.selectIndex();
			mom = this.population.getIndividuals().get(momIndex);

			/*
			 * Remove mom from the population to prevent parents from reproducing more than one time per generation.
			 */
			this.population.makeIneligibleForReproduction(momIndex);

			dadIndex = this.population.selectIndex();
			dad = this.population.getIndividuals().get(dadIndex);

			/*
			 * Remove dad from the population to prevent parents from reproducing more than one time per generation.
			 */
			this.population.makeIneligibleForReproduction(dadIndex);

			children = crossoverAlgorithm.crossover(mom, dad);

			/*
			 * Add children after all crossover operations are completed so that children are not inadvertently breeding
			 * immediately after birth.
			 */
			childrenToAdd.addAll(children);
		}

		for (Chromosome child : childrenToAdd) {
			this.population.addIndividualAsIneligible(child);
		}

		return (int) pairsToCrossover;
	}

	protected long determinePairsToCrossover(long initialPopulationSize) {
		/*
		 * We have to round down to protect against ArrayIndexOutOfBoundsException in edge cases. Also choose the
		 * minimum between the current population size and the calculated number of pairs in case there are not enough
		 * eligible individuals.
		 */
		return Math.min((long) (initialPopulationSize * strategy.getCrossoverRate()),
				((long) (this.population.size() / 2)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public int mutate(int initialPopulationSize) throws InterruptedException {
		int mutantIndex = -1;

		/*
		 * Set the number of mutations to perform to be the lesser of the current eligible population size and the total
		 * initial population size (before any other operations made individuals ineligible for reproduction) multiplied
		 * by the configured mutation rate.
		 */
		long mutations = Math.min(Math.round((initialPopulationSize * strategy.getMutationRate())), this.population
				.size());

		log.debug("Chromosomes to mutate: " + mutations);

		List<Chromosome> children = new ArrayList<Chromosome>();

		for (int i = 0; i < mutations; i++) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during mutation.");
			}

			mutantIndex = this.population.selectIndex();
			Chromosome chromosomeToMutate = this.population.getIndividuals().get(mutantIndex).clone();

			/*
			 * Remove the Chromosome from the population temporarily so that it is not re-selected by the next spin of
			 * the roulette wheel. Add it to a List to be re-added after all mutations are complete.
			 */
			this.population.makeIneligibleForReproduction(mutantIndex);

			/*
			 * Mutate a gene within the Chromosome. The original Chromosome has been cloned.
			 */
			mutationAlgorithm.mutateChromosome(chromosomeToMutate);
			children.add(chromosomeToMutate);
		}

		/*
		 * Re-add the original (now mutated) Chromosomes
		 */
		for (Chromosome chromosome : children) {
			this.population.addIndividualAsIneligible(chromosome);
		}

		return (int) mutations;
	}

	protected void spawnInitialPopulation() throws InterruptedException {
		GenerationStatistics generationStatistics = new GenerationStatistics(this.executionStatistics,
				this.generationCount);

		long start = System.currentTimeMillis();

		this.population.clearIndividuals();

		this.population.breed(strategy.getPopulationSize());

		this.population.evaluateFitness(generationStatistics);

		generationStatistics.setEntropy(calculateEntropy());

		long executionTime = System.currentTimeMillis() - start;
		generationStatistics.setExecutionTime(executionTime);

		log.info("Took " + executionTime + "ms to spawn initial population of size " + this.population.size());

		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	@SuppressWarnings("rawtypes")
	public double calculateEntropy() {
		if (!(this.population.getIndividuals().get(0) instanceof KeyedChromosome)) {
			throw new UnsupportedOperationException(
					"Calculation of entropy is currently only supported for KeyedChromosome types.");
		}

		Map<Object, Map<Object, Integer>> symbolCounts = new HashMap<Object, Map<Object, Integer>>();

		// Count occurrences of each Gene value
		for (Chromosome chromosome : this.population.getIndividuals()) {
			for (Object key : ((KeyedChromosome) chromosome).getGenes().keySet()) {
				if (!symbolCounts.containsKey(key)) {
					symbolCounts.put(key, new HashMap<Object, Integer>());
				}

				Object geneValue = ((KeyedChromosome) chromosome).getGenes().get(key);

				if (!symbolCounts.get(key).containsKey(geneValue)) {
					symbolCounts.get(key).put(geneValue, 0);
				}

				symbolCounts.get(key).put(geneValue, symbolCounts.get(key).get(geneValue) + 1);
			}
		}

		Map<Object, Map<Object, Double>> symbolProbabilities = new HashMap<Object, Map<Object, Double>>();

		double populationSize = (double) this.population.getIndividuals().size();

		// Calculate probability of each Gene value
		for (Object key : symbolCounts.keySet()) {
			for (Object geneValue : symbolCounts.get(key).keySet()) {
				if (!symbolProbabilities.containsKey(key)) {
					symbolProbabilities.put(key, new HashMap<Object, Double>());
				}

				symbolProbabilities.get(key).put(geneValue,
						((double) symbolCounts.get(key).get(geneValue) / populationSize));
			}
		}

		int base = symbolCounts.keySet().size();

		double totalEntropy = 0.0;
		double entropyForSymbol;
		double probability = 0.0;

		// Calculate the entropy of each Gene independently, and add it to the total entropy value
		for (Object key : symbolProbabilities.keySet()) {
			entropyForSymbol = 0.0;

			for (Object geneValue : symbolProbabilities.get(key).keySet()) {
				probability = symbolProbabilities.get(key).get(geneValue);
				entropyForSymbol += (probability * logBase(probability, base));
			}

			totalEntropy += (-1.0 * entropyForSymbol);
		}

		// return the average entropy among the symbols
		return totalEntropy / (double) symbolProbabilities.keySet().size();
	}

	private static double logBase(double num, int base) {
		return (Math.log(num) / Math.log(base));
	}

	/**
	 * @param executionStatistics
	 *            the ExecutionStatistics to persist
	 */
	protected void persistStatistics() {
		log.info("Persisting statistics to database.");

		long startInsert = System.currentTimeMillis();

		this.executionStatisticsDao.insert(this.executionStatistics);

		log.info("Took " + (System.currentTimeMillis() - startInsert) + "ms to persist statistics to database.");
	}

	@Override
	public void requestStop() {
		this.stopRequested = true;

		this.population.requestStop();
	}

	/**
	 * @return the population
	 */
	@Override
	public StandardPopulation getPopulation() {
		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	@Required
	@Override
	public void setPopulation(Population population) {
		this.population = (StandardPopulation) population;
	}

	/**
	 * @return the strategy
	 */
	@Override
	public GeneticAlgorithmStrategy getStrategy() {
		return strategy;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setStrategy(GeneticAlgorithmStrategy geneticAlgorithmStrategy) {
		this.population.setGeneticStructure(geneticAlgorithmStrategy.getGeneticStructure());
		this.population.setFitnessEvaluator(geneticAlgorithmStrategy.getFitnessEvaluator());
		this.population.setLifespan(geneticAlgorithmStrategy.getLifespan());
		this.population.setKnownSolutionFitnessEvaluator(geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
		this.population.setCompareToKnownSolution(geneticAlgorithmStrategy.getCompareToKnownSolution());

		this.crossoverAlgorithm = geneticAlgorithmStrategy.getCrossoverAlgorithm();
		this.crossoverAlgorithm.setMutationAlgorithm(geneticAlgorithmStrategy.getMutationAlgorithm());
		this.crossoverAlgorithm.setMutateDuringCrossover(geneticAlgorithmStrategy.getMutateDuringCrossover());

		this.mutationAlgorithm = geneticAlgorithmStrategy.getMutationAlgorithm();

		if (this.mutationAlgorithm instanceof UniformMutationAlgorithm) {
			((UniformMutationAlgorithm) this.mutationAlgorithm).setMutationRate(geneticAlgorithmStrategy
					.getMutationRate());
		}

		if (this.mutationAlgorithm instanceof NonUniformMutationAlgorithm) {
			((NonUniformMutationAlgorithm) this.mutationAlgorithm)
					.setMaxMutationsPerChromosome(geneticAlgorithmStrategy.getMaxMutationsPerIndividual());
		}

		this.selectionAlgorithm = geneticAlgorithmStrategy.getSelectionAlgorithm();

		this.strategy = geneticAlgorithmStrategy;
	}

	/**
	 * @param executionStatisticsDao
	 *            the executionStatisticsDao to set
	 */
	@Required
	public void setExecutionStatisticsDao(ExecutionStatisticsDao executionStatisticsDao) {
		this.executionStatisticsDao = executionStatisticsDao;
	}
}
