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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.population.StandardPopulation;

public class StandardGeneticAlgorithm extends AbstractGeneticAlgorithm {
	private Logger	log	= LoggerFactory.getLogger(getClass());

	private int		elitism;

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

	@Override
	public void select(int initialPopulationSize, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		StandardPopulation standardPopulation = (StandardPopulation) this.population;

		int momIndex = -1;
		int dadIndex = -1;
		Chromosome mom;
		Chromosome dad;

		long pairsToCrossover = (initialPopulationSize - elitism) / this.crossoverAlgorithm.numberOfOffspring();

		for (int i = 0; i < Math.max(0, pairsToCrossover); i++) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during crossover.");
			}

			momIndex = standardPopulation.selectIndex();
			mom = this.population.getIndividuals().get(momIndex);

			dadIndex = standardPopulation.selectIndex();
			// Ensure that dadIndex is different from momIndex
			dadIndex += (dadIndex == momIndex) ? ((dadIndex == 0) ? 1 : -1) : 0;
			dad = this.population.getIndividuals().get(dadIndex);

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
	}

	@Override
	public int crossover(int pairsToCrossover, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		StandardPopulation standardPopulation = (StandardPopulation) this.population;

		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return 0;
		}

		log.debug("Pairs to crossover: " + pairsToCrossover);

		List<Chromosome> crossoverResults = doConcurrentCrossovers(moms, dads);
		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();

		if (crossoverResults != null && !crossoverResults.isEmpty()) {
			childrenToAdd.addAll(crossoverResults);
		}

		if (childrenToAdd == null || (childrenToAdd.size() + elitism) < pairsToCrossover) {
			log.error(((null == childrenToAdd) ? "No" : childrenToAdd.size())
					+ " children produced from concurrent crossover execution.  Expected " + pairsToCrossover
					+ " children.");

			return ((null == childrenToAdd) ? 0 : childrenToAdd.size());
		}

		List<Chromosome> eliteIndividuals = new ArrayList<Chromosome>();

		if (elitism > 0) {
			standardPopulation.sortIndividuals();

			for (int i = this.population.size() - 1; i >= this.population.size() - elitism; i--) {
				eliteIndividuals.add(this.population.getIndividuals().get(i));
			}
		}

		this.population.clearIndividuals();

		for (Chromosome elite : eliteIndividuals) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			standardPopulation.addIndividual(elite);
		}

		for (Chromosome child : childrenToAdd) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			standardPopulation.addIndividual(child);
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
		StandardPopulation standardPopulation = (StandardPopulation) this.population;

		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		mutations.set(0);

		standardPopulation.sortIndividuals();

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

	/**
	 * @param elitism
	 *            the elitism to set
	 */
	@Required
	public void setElitism(int elitism) {
		this.elitism = elitism;
	}
}
