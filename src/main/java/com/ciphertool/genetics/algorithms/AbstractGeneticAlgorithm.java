package com.ciphertool.genetics.algorithms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.entities.statistics.PerformanceStatistics;
import com.ciphertool.genetics.population.Population;

public abstract class AbstractGeneticAlgorithm implements GeneticAlgorithm {
	protected Logger					log					= LoggerFactory.getLogger(getClass());

	protected Population				population;
	protected GeneticAlgorithmStrategy	strategy;
	protected Boolean					stopRequested		= false;
	protected Integer					generationCount		= 0;
	protected Integer					generationsToSkip;
	protected Integer					generationsToKeep;
	protected Boolean					verifyAncestry		= false;
	protected ExecutionStatistics		executionStatistics;
	@SuppressWarnings("rawtypes")
	protected MutationAlgorithm			mutationAlgorithm;
	protected AtomicInteger				mutations			= new AtomicInteger(0);
	@SuppressWarnings("rawtypes")
	protected CrossoverAlgorithm		crossoverAlgorithm;
	protected ExecutionStatisticsDao	executionStatisticsDao;
	protected GenerationStatisticsDao	generationStatisticsDao;
	protected TaskExecutor				taskExecutor;
	protected Boolean					persistStatistics	= false;
	protected Double					majorEvaluationPercentage;
	protected Integer					majorEvaluationStepSize;

	protected class SelectionResult {
		private Chromosome	mom;
		private Chromosome	dad;

		/**
		 * @param mom
		 *            the mom Chromosome to set
		 * @param dad
		 *            the dad Chromosome to set
		 */
		public SelectionResult(Chromosome mom, Chromosome dad) {
			this.mom = mom;
			this.dad = dad;
		}

		/**
		 * @return the mom Chromosome
		 */
		public Chromosome getMom() {
			return mom;
		}

		/**
		 * @return the dad Chromosome
		 */
		public Chromosome getDad() {
			return dad;
		}
	}

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

		long startEntropyCalculation = System.currentTimeMillis();
		BigDecimal entropy = this.population.calculateEntropy();
		generationStatistics.setEntropy(entropy);
		generationStatistics.getPerformanceStatistics().setEntropyMillis(System.currentTimeMillis()
				- startEntropyCalculation);

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		generationStatistics.getPerformanceStatistics().setEvaluationMillis(System.currentTimeMillis()
				- startEvaluation);

		long executionTime = System.currentTimeMillis() - start;
		generationStatistics.getPerformanceStatistics().setTotalMillis(executionTime);

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

		PerformanceStatistics performanceStats = new PerformanceStatistics();

		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		long startSelection = System.currentTimeMillis();
		this.population.reIndexSelector();
		select(populationSizeBeforeGeneration, moms, dads);
		performanceStats.setSelectionMillis(System.currentTimeMillis() - startSelection);

		long startCrossover = System.currentTimeMillis();
		generationStatistics.setNumberOfCrossovers(crossover(populationSizeBeforeGeneration, moms, dads));
		performanceStats.setCrossoverMillis(System.currentTimeMillis() - startCrossover);

		long startMutation = System.currentTimeMillis();
		generationStatistics.setNumberOfMutations(mutate(populationSizeBeforeGeneration));
		performanceStats.setMutationMillis(System.currentTimeMillis() - startMutation);

		long startEntropyCalculation = System.currentTimeMillis();
		BigDecimal entropy = this.population.calculateEntropy();
		generationStatistics.setEntropy(entropy);
		performanceStats.setEntropyMillis(System.currentTimeMillis() - startEntropyCalculation);

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		performanceStats.setEvaluationMillis(System.currentTimeMillis() - startEvaluation);

		if (majorEvaluationStepSize > 0 && (this.generationCount % majorEvaluationStepSize) == 0) {
			long startMajorEvaluation = System.currentTimeMillis();
			this.population.performMajorEvaluation(generationStatistics, majorEvaluationPercentage);
			performanceStats.setMajorEvaluationMillis(System.currentTimeMillis() - startMajorEvaluation);
		}

		performanceStats.setTotalMillis(System.currentTimeMillis() - generationStart);
		generationStatistics.setPerformanceStatistics(performanceStats);

		log.info(generationStatistics.toString());

		this.executionStatistics.addGenerationStatistics(generationStatistics);
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

			totalExecutionTime += generationStatistics.getPerformanceStatistics().getTotalMillis();
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

		if (this.persistStatistics) {
			persistStatistics();
		}

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
	 * @param majorEvaluationPercentage
	 *            the majorEvaluationPercentage to set
	 */
	@Required
	public void setMajorEvaluationPercentage(Double majorEvaluationPercentage) {
		this.majorEvaluationPercentage = majorEvaluationPercentage;
	}

	/**
	 * @param majorEvaluationStepSize
	 *            the majorEvaluationStepSize to set
	 */
	@Required
	public void setMajorEvaluationStepSize(Integer majorEvaluationStepSize) {
		this.majorEvaluationStepSize = majorEvaluationStepSize;
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

	/**
	 * @param persistStatistics
	 *            the persistStatistics to set
	 */
	@Required
	public void setPersistStatistics(boolean persistStatistics) {
		this.persistStatistics = persistStatistics;
	}
}
