package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.NonUniformMutationAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.UniformMutationAlgorithm;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.dao.GenerationStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.population.Population;

public abstract class AbstractGeneticAlgorithm implements GeneticAlgorithm {
	protected Logger					log				= LoggerFactory.getLogger(getClass());

	protected Population				population;
	protected GeneticAlgorithmStrategy	strategy;
	protected boolean					stopRequested;
	protected int						generationCount	= 0;
	protected Integer					generationsToSkip;
	protected Integer					generationsToKeep;
	protected boolean					verifyAncestry;
	protected ExecutionStatistics		executionStatistics;
	@SuppressWarnings("rawtypes")
	protected MutationAlgorithm			mutationAlgorithm;
	protected AtomicInteger				mutations		= new AtomicInteger(0);
	@SuppressWarnings("rawtypes")
	protected CrossoverAlgorithm		crossoverAlgorithm;
	protected ExecutionStatisticsDao	executionStatisticsDao;
	protected GenerationStatisticsDao	generationStatisticsDao;
	protected TaskExecutor				taskExecutor;

	@PostConstruct
	public void verifyParameters() {
		if (verifyAncestry && (generationsToSkip == null || generationsToKeep == null)) {
			throw new IllegalStateException(
					"When verifyAncestry is set to true, both generationsToSkip and generationsToSkip must be set.  generationsToSkip="
							+ generationsToSkip + ", generationsToKeep=" + generationsToKeep);
		}
	}

	@Override
	public void spawnInitialPopulation() throws InterruptedException {
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
		long crossoverTime = System.currentTimeMillis() - startCrossover;

		long startMutation = System.currentTimeMillis();
		generationStatistics.setNumberOfMutations(mutate(populationSizeBeforeGeneration));
		long mutationTime = System.currentTimeMillis() - startMutation;

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		long evaluationTime = System.currentTimeMillis() - startEvaluation;

		long startEntropyCalculation = System.currentTimeMillis();
		generationStatistics.setEntropy(calculateEntropy());
		long entropyTime = System.currentTimeMillis() - startEntropyCalculation;

		long executionTime = (System.currentTimeMillis() - generationStart);
		generationStatistics.setExecutionTime(executionTime);

		log.info("[crossoverTime=" + crossoverTime + ", mutationTime=" + mutationTime + ", evaluationTime="
				+ evaluationTime + ", entropyTime=" + entropyTime + "]");
		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double calculateEntropy() {
		if (!(this.population.getIndividuals().get(0) instanceof KeyedChromosome)) {
			throw new UnsupportedOperationException(
					"Calculation of entropy is currently only supported for KeyedChromosome types.");
		}

		Map<Object, Map<Object, Integer>> symbolCounts = new HashMap<Object, Map<Object, Integer>>();

		Object geneKey;
		// Count occurrences of each Gene value
		for (Chromosome chromosome : this.population.getIndividuals()) {
			for (Map.Entry<Object, Gene> entry : ((KeyedChromosome<Object>) chromosome).getGenes().entrySet()) {
				geneKey = entry.getKey();

				if (!symbolCounts.containsKey(geneKey)) {
					symbolCounts.put(geneKey, new HashMap<Object, Integer>());
				}

				Object geneValue = ((KeyedChromosome) chromosome).getGenes().get(geneKey);

				if (!symbolCounts.get(geneKey).containsKey(geneValue)) {
					symbolCounts.get(geneKey).put(geneValue, 0);
				}

				symbolCounts.get(geneKey).put(geneValue, symbolCounts.get(geneKey).get(geneValue) + 1);
			}
		}

		Map<Object, Map<Object, Double>> symbolProbabilities = new HashMap<Object, Map<Object, Double>>();

		double populationSize = (double) this.population.size();

		Object symbolCountsKey;
		Object geneValue;

		// Calculate probability of each Gene value
		for (Map.Entry<Object, Map<Object, Integer>> entry : symbolCounts.entrySet()) {
			symbolCountsKey = entry.getKey();

			for (Map.Entry<Object, Integer> entryInner : symbolCounts.get(symbolCountsKey).entrySet()) {
				geneValue = entryInner.getKey();

				if (!symbolProbabilities.containsKey(symbolCountsKey)) {
					symbolProbabilities.put(symbolCountsKey, new HashMap<Object, Double>());
				}

				symbolProbabilities.get(symbolCountsKey).put(geneValue, ((double) symbolCounts.get(symbolCountsKey).get(geneValue)
						/ populationSize));
			}
		}

		int base = symbolCounts.size();

		double totalEntropy = 0.0;
		double entropyForSymbol;
		double probability = 0.0;

		Object symbolProbabilitiesKey;
		Object independentGeneValue;

		// Calculate the entropy of each Gene independently, and add it to the total entropy value
		for (Map.Entry<Object, Map<Object, Double>> entry : symbolProbabilities.entrySet()) {
			symbolProbabilitiesKey = entry.getKey();

			entropyForSymbol = 0.0;

			for (Map.Entry<Object, Double> entryInner : symbolProbabilities.get(symbolProbabilitiesKey).entrySet()) {
				independentGeneValue = entryInner.getKey();

				probability = symbolProbabilities.get(symbolProbabilitiesKey).get(independentGeneValue);
				entropyForSymbol += (probability * logBase(probability, base));
			}

			totalEntropy += (-1.0 * entropyForSymbol);
		}

		// return the average entropy among the symbols
		return totalEntropy / (double) symbolProbabilities.size();
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
		this.population.setKnownSolutionFitnessEvaluator(geneticAlgorithmStrategy.getKnownSolutionFitnessEvaluator());
		this.population.setCompareToKnownSolution(geneticAlgorithmStrategy.getCompareToKnownSolution());
		this.population.setTargetSize(geneticAlgorithmStrategy.getPopulationSize());
		this.population.setSelector(geneticAlgorithmStrategy.getSelector());

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
	@Override
	public Population getPopulation() {
		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	@Required
	@Override
	public void setPopulation(Population population) {
		this.population = population;
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
