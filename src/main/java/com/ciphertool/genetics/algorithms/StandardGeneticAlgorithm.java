package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;

public class StandardGeneticAlgorithm extends MultigenerationalGeneticAlgorithm {
	private Logger log = Logger.getLogger(getClass());

	private TaskExecutor taskExecutor;

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	protected class CrossoverTask implements Callable<List<Chromosome>> {

		private Chromosome mom;
		private Chromosome dad;

		public CrossoverTask(Chromosome mom, Chromosome dad) {
			this.mom = mom;
			this.dad = dad;
		}

		@Override
		public List<Chromosome> call() throws Exception {
			return crossoverAlgorithm.crossover(mom, dad);
		}
	}

	@Override
	public void proceedWithNextGeneration() {
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

		long startEvaluation = System.currentTimeMillis();
		this.population.evaluateFitness(generationStatistics);
		if (log.isDebugEnabled()) {
			log.debug("Evaluation took " + (System.currentTimeMillis() - startEvaluation) + "ms.");
		}

		long executionTime = (System.currentTimeMillis() - generationStart);
		generationStatistics.setExecutionTime(executionTime);

		log.info(generationStatistics);

		this.executionStatistics.addGenerationStatistics(generationStatistics);
	}

	/*
	 * Crossover algorithm utilizing Roulette Wheel Selection
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#crossover()
	 */
	@Override
	public int crossover(int initialPopulationSize) {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return 0;
		}

		log.debug("Pairs to crossover: " + initialPopulationSize);

		int momIndex = -1;
		int dadIndex = -1;

		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		/*
		 * We first remove all the parent Chromosomes since the children are guaranteed to be at least as fit. This also
		 * prevents parents from reproducing more than one time per generation.
		 */
		for (int i = 0; i < initialPopulationSize; i++) {
			momIndex = this.population.selectIndex();
			moms.add(this.population.getIndividuals().get(momIndex));

			dadIndex = this.population.selectIndex();
			dads.add(this.population.getIndividuals().get(dadIndex));
		}

		List<Chromosome> childrenToAdd = doConcurrentCrossovers(initialPopulationSize, moms, dads);

		if (childrenToAdd == null || childrenToAdd.size() < initialPopulationSize) {
			log.error(((null == childrenToAdd) ? "No" : childrenToAdd.size())
					+ " children produced from concurrent crossover execution.  Expected " + initialPopulationSize
					+ " children.");

			return ((null == childrenToAdd) ? 0 : childrenToAdd.size());
		}

		this.population.clearIndividuals();

		for (Chromosome child : childrenToAdd) {
			this.population.addIndividual(child);
		}

		return (int) childrenToAdd.size();
	}

	protected List<Chromosome> doConcurrentCrossovers(long pairsToCrossover, List<Chromosome> moms,
			List<Chromosome> dads) {
		List<FutureTask<List<Chromosome>>> futureTasks = new ArrayList<FutureTask<List<Chromosome>>>();
		FutureTask<List<Chromosome>> futureTask = null;

		Chromosome mom = null;
		Chromosome dad = null;

		/*
		 * Execute each crossover concurrently. Parents should produce two children, but this is not necessarily always
		 * guaranteed.
		 */
		for (int i = 0; i < pairsToCrossover; i++) {
			mom = moms.get(i);
			dad = dads.get(i);

			futureTask = new FutureTask<List<Chromosome>>(new CrossoverTask(mom, dad));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
		}

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();
		// Add the result of each FutureTask to the population since it represents a new child Chromosome.
		for (FutureTask<List<Chromosome>> future : futureTasks) {
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
	public int mutate(int initialPopulationSize) {
		// Do nothing. We are testing a Genetic Algorithm which has no separate "Mutate" step.

		return 0;
	}

	@Override
	public int select() {
		// Do nothing. We are testing a Genetic Algorithm which has no separate "Select" step.

		return 0;
	}

	/**
	 * @param taskExecutor
	 *            the taskExecutor to set
	 */
	@Required
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}
