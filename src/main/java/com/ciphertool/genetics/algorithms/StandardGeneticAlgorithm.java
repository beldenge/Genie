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
import java.util.List;
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

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;

public class StandardGeneticAlgorithm extends MultigenerationalGeneticAlgorithm {
	private Logger			log			= LoggerFactory.getLogger(getClass());

	private Integer			generationsToSkip;
	private Integer			generationsToKeep;
	private TaskExecutor	taskExecutor;
	private boolean			verifyAncestry;
	private AtomicInteger	mutations	= new AtomicInteger(0);
	private int				elitism;

	@PostConstruct
	public void verifyParameters() {
		if (verifyAncestry && (generationsToSkip == null || generationsToKeep == null)) {
			throw new IllegalStateException(
					"When verifyAncestry is set to true, both generationsToSkip and generationsToSkip must be set.  generationsToSkip="
							+ generationsToSkip + ", generationsToKeep=" + generationsToKeep);
		}
	}

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	protected class CrossoverTask implements Callable<List<Chromosome>> {

		private Chromosome	mom;
		private Chromosome	dad;

		public CrossoverTask(Chromosome mom, Chromosome dad) {
			this.mom = mom;
			this.dad = dad;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Chromosome> call() throws Exception {
			return crossoverAlgorithm.crossover(mom, dad);
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
			Chromosome original = chromosome.clone();

			/*
			 * Mutate a gene within the Chromosome. The original Chromosome has been cloned.
			 */
			mutationAlgorithm.mutateChromosome(chromosome);

			if (!chromosome.equals(original)) {
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

		int momIndex = -1;
		int dadIndex = -1;

		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();

		/*
		 * We first remove all the parent Chromosomes since the children are guaranteed to be at least as fit.
		 */
		for (int i = 0; i < (initialPopulationSize - elitism) / this.crossoverAlgorithm.numberOfOffspring(); i++) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during crossover.");
			}
			momIndex = this.population.selectIndex();
			dadIndex = this.population.selectIndex();

			if (ThreadLocalRandom.current().nextDouble() > strategy.getCrossoverRate()) {
				childrenToAdd.add(this.population.getIndividuals().get(momIndex).clone());
				childrenToAdd.add(this.population.getIndividuals().get(dadIndex).clone());

				// Skipping crossover
				continue;
			}

			if (momIndex == dadIndex) {
				/*
				 * There is no point in crossing over identical parents, because the result would essentially be
				 * duplicating that parent in the population
				 */
				i--;
				continue;
			}

			Chromosome mom = this.population.getIndividuals().get(momIndex);
			Chromosome dad = this.population.getIndividuals().get(dadIndex);

			if (verifyAncestry && this.generationCount > this.generationsToKeep && mom.getAncestry() != null
					&& dad.getAncestry() != null
					&& !mom.getAncestry().sharesLineageWith(dad.getAncestry(), generationsToSkip)) {
				/*
				 * The idea is to make sure that individuals which share too much ancestry (i.e. immediate family
				 * members) or not enough ancestry (i.e. different species) cannot reproduce.
				 */
				i--;
				continue;
			}

			moms.add(mom);
			dads.add(dad);
		}

		List<Chromosome> crossoverResults = doConcurrentCrossovers(moms, dads);
		if (crossoverResults != null && !crossoverResults.isEmpty()) {
			childrenToAdd.addAll(crossoverResults);
		}

		if (childrenToAdd == null || (childrenToAdd.size() + elitism) < initialPopulationSize) {
			log.error(((null == childrenToAdd) ? "No" : childrenToAdd.size())
					+ " children produced from concurrent crossover execution.  Expected " + initialPopulationSize
					+ " children.");

			return ((null == childrenToAdd) ? 0 : childrenToAdd.size());
		}

		List<Chromosome> eliteIndividuals = new ArrayList<Chromosome>();

		this.population.sortIndividuals();

		for (int i = this.population.size() - 1; i >= this.population.size() - elitism; i--) {
			eliteIndividuals.add(this.population.getIndividuals().get(i));
		}

		this.population.clearIndividuals();

		for (Chromosome elite : eliteIndividuals) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			this.population.addIndividual(elite);
		}

		for (Chromosome child : childrenToAdd) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			this.population.addIndividual(child);
		}

		return (int) childrenToAdd.size();
	}

	protected List<Chromosome> doConcurrentCrossovers(List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		if (moms.size() != dads.size()) {
			throw new IllegalStateException(
					"Attempted to perform crossover on the population, but there are not an equal number of moms and dads.  Something is wrong.  Moms: "
							+ moms.size() + ", Dads:  " + dads.size());
		}

		List<FutureTask<List<Chromosome>>> futureTasks = new ArrayList<FutureTask<List<Chromosome>>>();
		FutureTask<List<Chromosome>> futureTask = null;

		Chromosome mom = null;
		Chromosome dad = null;

		/*
		 * Execute each crossover concurrently. Parents should produce two children, but this is not necessarily always
		 * guaranteed.
		 */
		for (int i = 0; i < moms.size(); i++) {
			mom = moms.get(i);
			dad = dads.get(i);

			futureTask = new FutureTask<List<Chromosome>>(new CrossoverTask(mom, dad));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
		}

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();
		// Add the result of each FutureTask to the population since it represents a new child Chromosome.
		for (FutureTask<List<Chromosome>> future : futureTasks) {
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

		this.population.sortIndividuals();

		/*
		 * Execute each mutation concurrently.
		 */
		for (int i = this.population.size() - elitism - 1; i >= 0; i--) {
			futureTask = new FutureTask<Void>(new MutationTask(this.population.getIndividuals().get(i)));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
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
	 * @param elitism
	 *            the elitism to set
	 */
	@Required
	public void setElitism(int elitism) {
		this.elitism = elitism;
	}
}
