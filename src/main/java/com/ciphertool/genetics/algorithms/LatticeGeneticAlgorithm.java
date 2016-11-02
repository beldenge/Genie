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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.SpatialChromosomeWrapper;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.population.LatticePopulation;

public class LatticeGeneticAlgorithm extends AbstractGeneticAlgorithm {
	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	private class CrossoverTask implements Callable<List<SpatialChromosomeWrapper>> {
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

	@Override
	public int crossover(int initialPopulationSize) throws InterruptedException {
		LatticePopulation latticePopulation = (LatticePopulation) this.population;

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
		for (int x = 0; x < latticePopulation.getLatticeRows(); x++) {
			for (int y = 0; y < latticePopulation.getLatticeColumns(); y++) {
				if (stopRequested) {
					throw new InterruptedException("Stop requested during crossover.");
				}
				parents = latticePopulation.selectIndices(x, y);

				if (parents == null || parents.isEmpty() || parents.size() < 2) {
					throw new IllegalStateException("Unable to produce two parents for crossover");
				}

				mom = parents.get(0);
				dad = parents.get(1);

				if (ThreadLocalRandom.current().nextDouble() > strategy.getCrossoverRate()) {
					childrenToAdd.add(new SpatialChromosomeWrapper(x, y,
							latticePopulation.getIndividualsAsArray()[x][y].clone()));

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

			latticePopulation.addIndividual(child);
		}

		return (int) childrenToAdd.size();
	}

	private List<SpatialChromosomeWrapper> doConcurrentCrossovers(List<FutureTask<List<SpatialChromosomeWrapper>>> futureTasks)
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
		LatticePopulation latticePopulation = (LatticePopulation) this.population;

		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		mutations.set(0);

		/*
		 * Execute each mutation concurrently.
		 */
		for (int x = 0; x < latticePopulation.getLatticeRows(); x++) {
			for (int y = 0; y < latticePopulation.getLatticeColumns(); y++) {
				futureTask = new FutureTask<Void>(new MutationTask(latticePopulation.getIndividualsAsArray()[x][y]));
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
}
