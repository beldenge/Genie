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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.LatticePopulation;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.SpatialChromosomeWrapper;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.NonUniformMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.dao.GenerationStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;

public class LatticeGeneticAlgorithm implements GeneticAlgorithm {
	private Logger						log				= LoggerFactory.getLogger(getClass());

	private GeneticAlgorithmStrategy	strategy;
	private LatticePopulation			population;
	@SuppressWarnings("rawtypes")
	private CrossoverAlgorithm			crossoverAlgorithm;
	@SuppressWarnings("rawtypes")
	private MutationAlgorithm			mutationAlgorithm;
	private boolean						stopRequested;
	private ExecutionStatisticsDao		executionStatisticsDao;
	private GenerationStatisticsDao		generationStatisticsDao;
	private int							generationCount	= 0;
	private ExecutionStatistics			executionStatistics;
	private Integer						generationsToSkip;
	private Integer						generationsToKeep;
	private TaskExecutor				taskExecutor;
	private boolean						verifyAncestry;
	private AtomicInteger				mutations		= new AtomicInteger(0);

	@PostConstruct
	public void verifyParameters() {
		if (verifyAncestry && (generationsToSkip == null || generationsToKeep == null)) {
			throw new IllegalStateException(
					"When verifyAncestry is set to true, both generationsToSkip and generationsToSkip must be set.  generationsToSkip="
							+ generationsToSkip + ", generationsToKeep=" + generationsToKeep);
		}
	}

	@Override
	public void evolveAutonomously() {
		try {
			initialize();

			do {
				proceedWithNextGeneration();
			} while (!this.stopRequested && (this.strategy.getMaxGenerations() < 0
					|| this.generationCount < this.strategy.getMaxGenerations()));
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

	protected void spawnInitialPopulation() throws InterruptedException {
		GenerationStatistics generationStatistics = new GenerationStatistics(this.executionStatistics,
				this.generationCount);

		long start = System.currentTimeMillis();

		this.population.clearIndividuals();

		this.population.breed();

		this.population.evaluateFitness(generationStatistics);

		generationStatistics.setEntropy(calculateEntropy());

		long executionTime = System.currentTimeMillis() - start;
		generationStatistics.setExecutionTime(executionTime);

		log.info("Took " + executionTime + "ms to spawn initial population of size " + this.population.size());

		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	protected class CrossoverTask implements Callable<List<SpatialChromosomeWrapper>> {
		private int			xPos;
		private int			yPos;
		private Chromosome	mom;
		private Chromosome	dad;

		public CrossoverTask(Chromosome mom, Chromosome dad, int xPos, int yPos) {
			this.mom = mom;
			this.dad = dad;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<SpatialChromosomeWrapper> call() throws Exception {
			List<Chromosome> children = crossoverAlgorithm.crossover(mom, dad);

			List<SpatialChromosomeWrapper> wrappedChildren = new ArrayList<SpatialChromosomeWrapper>();
			for (Chromosome child : children) {
				wrappedChildren.add(new SpatialChromosomeWrapper(xPos, yPos, child));
			}

			return wrappedChildren;
		}
	}

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	protected class MutationTask implements Callable<Void> {

		private Chromosome chromosome;

		public MutationTask(Chromosome chromosome) {
			this.chromosome = chromosome;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Void call() throws Exception {
			/*
			 * Mutate a gene within the Chromosome. The original Chromosome has been cloned.
			 */
			if (mutationAlgorithm.mutateChromosome(chromosome)) {
				mutations.incrementAndGet();
			}

			return null;
		}
	}

	@Override
	public void proceedWithNextGeneration() throws InterruptedException {
		this.population.backupIndividuals();

		this.generationCount++;

		GenerationStatistics generationStatistics = new GenerationStatistics(this.executionStatistics,
				this.generationCount);

		long generationStart = System.currentTimeMillis();

		int populationSizeBeforeGeneration = this.population.size();

		long startCrossover = System.currentTimeMillis();
		generationStatistics.setNumberOfCrossovers(crossover(populationSizeBeforeGeneration));
		if (log.isDebugEnabled()) {
			log.debug("Crossover took " + (System.currentTimeMillis() - startCrossover) + "ms.");
		}

		long startMutation = System.currentTimeMillis();
		generationStatistics.setNumberOfMutations(mutate(populationSizeBeforeGeneration));
		if (log.isDebugEnabled()) {
			log.debug("Mutation took " + (System.currentTimeMillis() - startMutation) + "ms.");
		}

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		if (log.isDebugEnabled()) {
			log.debug("Evaluation took " + (System.currentTimeMillis() - startEvaluation) + "ms.");
		}

		long startEntropyCalculation = System.currentTimeMillis();
		generationStatistics.setEntropy(calculateEntropy());
		if (log.isDebugEnabled()) {
			log.debug("Entropy calculation took " + (System.currentTimeMillis() - startEntropyCalculation) + "ms.");
		}

		long executionTime = (System.currentTimeMillis() - generationStart);
		generationStatistics.setExecutionTime(executionTime);

		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	@Override
	public int crossover(int initialPopulationSize) throws InterruptedException {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return 0;
		}

		log.debug("Pairs to crossover: " + initialPopulationSize);

		List<SpatialChromosomeWrapper> childrenToAdd = new ArrayList<SpatialChromosomeWrapper>();

		/*
		 * We first remove all the parent Chromosomes since the children are guaranteed to be at least as fit.
		 */
		Chromosome mom = null;
		Chromosome dad = null;
		List<Chromosome> parents = null;
		List<FutureTask<List<SpatialChromosomeWrapper>>> futureTasks = new ArrayList<FutureTask<List<SpatialChromosomeWrapper>>>();
		for (int x = 0; x < this.population.getLatticeRows(); x++) {
			for (int y = 0; y < this.population.getLatticeColumns(); y++) {
				if (stopRequested) {
					throw new InterruptedException("Stop requested during crossover.");
				}
				parents = this.population.selectIndices(x, y);

				if (parents == null || parents.isEmpty() || parents.size() < 2) {
					throw new IllegalStateException("Unable to produce two parents for crossover");
				}

				mom = parents.get(0);
				dad = parents.get(1);

				if (ThreadLocalRandom.current().nextDouble() > strategy.getCrossoverRate()) {
					childrenToAdd.add(new SpatialChromosomeWrapper(x, y,
							this.population.getIndividualsAsArray()[x][y].clone()));

					// Skipping crossover
					continue;
				}

				if (mom == dad) {
					/*
					 * There is no point in crossing over identical parents, because the result would essentially be
					 * duplicating that parent in the population
					 */
					y--;
					continue;
				}

				if (verifyAncestry && this.generationCount > this.generationsToKeep && mom.getAncestry() != null
						&& dad.getAncestry() != null
						&& !mom.getAncestry().sharesLineageWith(dad.getAncestry(), generationsToSkip)) {
					/*
					 * The idea is to make sure that individuals which share too much ancestry (i.e. immediate family
					 * members) or not enough ancestry (i.e. different species) cannot reproduce.
					 */
					y--;
					continue;
				}

				futureTasks.add(new FutureTask<List<SpatialChromosomeWrapper>>(new CrossoverTask(mom, dad, x, y)));
			}
		}

		List<SpatialChromosomeWrapper> crossoverResults = doConcurrentCrossovers(futureTasks);
		if (crossoverResults != null && !crossoverResults.isEmpty()) {
			childrenToAdd.addAll(crossoverResults);
		}

		if (childrenToAdd == null || childrenToAdd.size() < initialPopulationSize) {
			log.error(((null == childrenToAdd) ? "No" : childrenToAdd.size())
					+ " children produced from concurrent crossover execution.  Expected " + initialPopulationSize
					+ " children.");

			return ((null == childrenToAdd) ? 0 : childrenToAdd.size());
		}

		this.population.clearIndividuals();

		for (SpatialChromosomeWrapper child : childrenToAdd) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			this.population.addIndividual(child);
		}

		return (int) childrenToAdd.size();
	}

	protected List<SpatialChromosomeWrapper> doConcurrentCrossovers(List<FutureTask<List<SpatialChromosomeWrapper>>> futureTasks)
			throws InterruptedException {
		/*
		 * Execute each crossover concurrently. Parents should produce two children, but this is not necessarily always
		 * guaranteed.
		 */
		for (FutureTask<List<SpatialChromosomeWrapper>> futureTask : futureTasks) {
			this.taskExecutor.execute(futureTask);
		}

		List<SpatialChromosomeWrapper> childrenToAdd = new ArrayList<SpatialChromosomeWrapper>();
		// Add the result of each FutureTask to the population since it represents a new child Chromosome.
		for (FutureTask<List<SpatialChromosomeWrapper>> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during concurrent crossovers");
			}

			try {
				/*
				 * Add children after all crossover operations are completed so that children are not inadvertently
				 * breeding immediately after birth.
				 */
				childrenToAdd.addAll(future.get());
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for CrossoverTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for CrossoverTask ", ee);
			}
		}

		return childrenToAdd;
	}

	@Override
	public int mutate(int initialPopulationSize) throws InterruptedException {
		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		mutations.set(0);

		/*
		 * Execute each mutation concurrently.
		 */
		for (int x = 0; x < this.population.getLatticeRows(); x++) {
			for (int y = 0; y < this.population.getLatticeColumns(); y++) {
				futureTask = new FutureTask<Void>(new MutationTask(this.population.getIndividualsAsArray()[x][y]));
				futureTasks.add(futureTask);
				this.taskExecutor.execute(futureTask);
			}
		}

		for (FutureTask<Void> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during mutation");
			}

			try {
				future.get();
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for MutationTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for MutationTask ", ee);
			}
		}

		return mutations.get();
	}

	@Override
	public int select() {
		// Do nothing. We are testing a Genetic Algorithm which has no separate "Select" step.

		return 0;
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

		double populationSize = (double) this.population.size();

		// Calculate probability of each Gene value
		for (Object key : symbolCounts.keySet()) {
			for (Object geneValue : symbolCounts.get(key).keySet()) {
				if (!symbolProbabilities.containsKey(key)) {
					symbolProbabilities.put(key, new HashMap<Object, Double>());
				}

				symbolProbabilities.get(key).put(geneValue, ((double) symbolCounts.get(key).get(geneValue)
						/ populationSize));
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

	@Override
	public void requestStop() {
		this.stopRequested = true;

		this.population.requestStop();
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

	/**
	 * @param executionStatistics
	 *            the ExecutionStatistics to persist
	 */
	protected void persistStatistics() {
		log.info("Persisting statistics to database.");

		long startInsert = System.currentTimeMillis();

		this.generationStatisticsDao.insertBatch(this.executionStatistics.getGenerationStatisticsList());
		this.executionStatisticsDao.insert(this.executionStatistics);

		log.info("Took " + (System.currentTimeMillis() - startInsert) + "ms to persist statistics to database.");
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void setStrategy(GeneticAlgorithmStrategy geneticAlgorithmStrategy) {
		this.population.setGeneticStructure(geneticAlgorithmStrategy.getGeneticStructure());
		this.population.setFitnessEvaluator(geneticAlgorithmStrategy.getFitnessEvaluator());
		this.population.setLifespan(geneticAlgorithmStrategy.getLifespan());
		this.population.setKnownSolutionFitnessEvaluator(geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
		this.population.setCompareToKnownSolution(geneticAlgorithmStrategy.getCompareToKnownSolution());

		this.crossoverAlgorithm = geneticAlgorithmStrategy.getCrossoverAlgorithm();

		this.mutationAlgorithm = geneticAlgorithmStrategy.getMutationAlgorithm();

		if (this.mutationAlgorithm instanceof UniformMutationAlgorithm) {
			((UniformMutationAlgorithm) this.mutationAlgorithm).setMutationRate(geneticAlgorithmStrategy.getMutationRate());
		}

		if (this.mutationAlgorithm instanceof NonUniformMutationAlgorithm) {
			((NonUniformMutationAlgorithm) this.mutationAlgorithm).setMaxMutationsPerChromosome(geneticAlgorithmStrategy.getMaxMutationsPerIndividual());
		}

		this.strategy = geneticAlgorithmStrategy;
	}

	/**
	 * @return the strategy
	 */
	@Override
	public GeneticAlgorithmStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @param taskExecutor
	 *            the taskExecutor to set
	 */
	@Required
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * @param generationsToSkip
	 *            the generationsToSkip to set
	 */
	public void setGenerationsToSkip(int generationsToSkip) {
		this.generationsToSkip = generationsToSkip;
	}

	/**
	 * @param generationsToKeep
	 *            the generationsToKeep to set
	 */
	public void setGenerationsToKeep(int generationsToKeep) {
		this.generationsToKeep = generationsToKeep;
	}

	/**
	 * @param verifyAncestry
	 *            the verifyAncestry to set
	 */
	@Required
	public void setVerifyAncestry(boolean verifyAncestry) {
		this.verifyAncestry = verifyAncestry;
	}

	/**
	 * @return the population
	 */
	public LatticePopulation getPopulation() {
		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	@Required
	@Override
	public void setPopulation(Population population) {
		this.population = (LatticePopulation) population;
	}

	/**
	 * @param executionStatisticsDao
	 *            the executionStatisticsDao to set
	 */
	@Required
	public void setExecutionStatisticsDao(ExecutionStatisticsDao executionStatisticsDao) {
		this.executionStatisticsDao = executionStatisticsDao;
	}

	/**
	 * @param generationStatisticsDao
	 *            the generationStatisticsDao to set
	 */
	@Required
	public void setGenerationStatisticsDao(GenerationStatisticsDao generationStatisticsDao) {
		this.generationStatisticsDao = generationStatisticsDao;
	}
}
