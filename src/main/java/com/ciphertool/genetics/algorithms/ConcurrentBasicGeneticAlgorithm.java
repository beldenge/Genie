/**
 * Copyright 2012 George Belden
 * 
 * This file is part of ZodiacGenetics.
 * 
 * ZodiacGenetics is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ZodiacGenetics is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ZodiacGenetics. If not, see <http://www.gnu.org/licenses/>.
 */

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

public class ConcurrentBasicGeneticAlgorithm extends BasicGeneticAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	private TaskExecutor taskExecutor;

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes,
	 * producing one child Chromosome.
	 */
	private class CrossoverTask implements Callable<Chromosome> {

		private Chromosome mom;
		private Chromosome dad;

		public CrossoverTask(Chromosome mom, Chromosome dad) {
			this.mom = mom;
			this.dad = dad;
		}

		@Override
		public Chromosome call() throws Exception {
			return crossoverAlgorithm.crossover(mom, dad);
		}
	}

	/*
	 * Crossover algorithm utilizing Roulette Wheel Selection
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#crossover()
	 */
	@Override
	public void crossover() {
		int initialPopulationSize = this.population.size();

		long pairsToCrossover = Math.round((initialPopulationSize * crossoverRate) / 2);

		log.debug("Pairs to crossover: " + pairsToCrossover);

		Chromosome mom = null;
		Chromosome dad = null;
		int momIndex = -1;
		int dadIndex = -1;

		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		/*
		 * We first remove all the parent Chromosomes since the children are
		 * guaranteed to be better fit.
		 */
		for (int i = 0; i < pairsToCrossover; i++) {
			momIndex = this.population.spinIndexRouletteWheel();
			moms.add(this.population.getIndividuals().get(momIndex));
			this.population.removeIndividual(momIndex);

			dadIndex = this.population.spinIndexRouletteWheel();
			dads.add(this.population.getIndividuals().get(dadIndex));
			this.population.removeIndividual(dadIndex);
		}

		List<FutureTask<Chromosome>> futureTasks = new ArrayList<FutureTask<Chromosome>>();
		FutureTask<Chromosome> futureTask = null;

		/*
		 * Execute each crossover concurrently. Parents always produce two
		 * children, so the population should end with the same size as when it
		 * began the crossovers.
		 */
		for (int i = 0; i < pairsToCrossover; i++) {
			mom = moms.get(i);
			dad = dads.get(i);

			futureTask = new FutureTask<Chromosome>(new CrossoverTask(mom, dad));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);

			futureTask = new FutureTask<Chromosome>(new CrossoverTask(dad, mom));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
		}

		/*
		 * Add the result of each FutureTask to the population since it
		 * represents a new child Chromosome.
		 */
		for (FutureTask<Chromosome> future : futureTasks) {
			try {
				this.population.addIndividual(future.get());
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for CrossoverTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for CrossoverTask ", ee);
			}
		}
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
