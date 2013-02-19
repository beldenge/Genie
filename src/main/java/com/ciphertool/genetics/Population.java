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

package com.ciphertool.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.GenerationStatistics;
import com.ciphertool.genetics.util.Breeder;
import com.ciphertool.genetics.util.FitnessComparator;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class Population {
	private Logger log = Logger.getLogger(getClass());
	private Breeder breeder;
	private List<Chromosome> individuals = new ArrayList<Chromosome>();
	private FitnessEvaluator fitnessEvaluator;
	private FitnessComparator fitnessComparator;
	private Double totalFitness = 0.0;
	private TaskExecutor taskExecutor;
	private int lifespan;
	private FitnessEvaluator knownSolutionFitnessEvaluator;
	private static final boolean COMPARE_TO_KNOWN_SOLUTION_DEFAULT = false;
	private Boolean compareToKnownSolution = COMPARE_TO_KNOWN_SOLUTION_DEFAULT;

	public Population() {
	}

	/**
	 * A concurrent task for adding a brand new Chromosome to the population.
	 */
	private class GeneratorTask implements Callable<Chromosome> {

		public GeneratorTask() {
		}

		@Override
		public Chromosome call() throws Exception {
			return breeder.breed();
		}
	}

	public void breed(Integer maxIndividuals) {
		List<FutureTask<Chromosome>> futureTasks = new ArrayList<FutureTask<Chromosome>>();
		FutureTask<Chromosome> futureTask = null;

		int individualsAdded = 0;
		for (int i = this.individuals.size(); i < maxIndividuals; i++) {
			futureTask = new FutureTask<Chromosome>(new GeneratorTask());
			futureTasks.add(futureTask);

			this.taskExecutor.execute(futureTask);
		}

		for (FutureTask<Chromosome> future : futureTasks) {
			try {
				this.individuals.add(future.get());

				individualsAdded++;
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for GeneratorTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for GeneratorTask ", ee);
			}
		}

		log.debug("Added " + individualsAdded + " individuals to the population.");
	}

	/**
	 * A concurrent task for evaluating the fitness of a Chromosome.
	 */
	private class EvaluatorTask implements Callable<Double> {

		private Chromosome chromosome;

		public EvaluatorTask(Chromosome chromosome) {
			this.chromosome = chromosome;
		}

		@Override
		public Double call() throws Exception {
			return fitnessEvaluator.evaluate(this.chromosome);
		}
	}

	/**
	 * This method executes all the fitness evaluations concurrently.
	 */
	private void doConcurrentFitnessEvaluations() {
		List<FutureTask<Double>> futureTasks = new ArrayList<FutureTask<Double>>();
		FutureTask<Double> futureTask = null;

		int evaluationCount = 0;
		for (Chromosome individual : individuals) {
			/*
			 * Only evaluate individuals that have changed since the last
			 * evaluation.
			 */
			if (individual.isDirty()) {
				evaluationCount++;
				futureTask = new FutureTask<Double>(new EvaluatorTask(individual));
				futureTasks.add(futureTask);
				this.taskExecutor.execute(futureTask);
			}
		}
		log.debug("Evaluations carried out: " + evaluationCount);

		for (FutureTask<Double> future : futureTasks) {
			try {
				future.get();
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for EvaluatorTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for EvaluatorTask ", ee);
			}
		}
	}

	public Chromosome evaluateFitness(GenerationStatistics generationStatistics) {
		this.doConcurrentFitnessEvaluations();

		this.totalFitness = 0.0;

		Chromosome bestFitIndividual = null;

		for (Chromosome individual : individuals) {
			this.totalFitness += individual.getFitness();

			if (bestFitIndividual == null
					|| individual.getFitness() > bestFitIndividual.getFitness()) {
				bestFitIndividual = individual;
			}
		}

		Double averageFitness = Double.valueOf(this.totalFitness)
				/ Double.valueOf(individuals.size());

		if (log.isDebugEnabled()) {
			log.debug("Population of size " + individuals.size() + " has an average fitness of "
					+ String.format("%1$,.2f", averageFitness));

			log.debug("Best fitness in population is "
					+ String.format("%1$,.2f", bestFitIndividual.getFitness()));
		}

		if (generationStatistics != null) {
			generationStatistics.setAverageFitness(averageFitness);
			generationStatistics.setBestFitness(bestFitIndividual.getFitness());

			if (this.compareToKnownSolution) {
				/*
				 * We have to clone the best fit individual since the
				 * knownSolutionFitnessEvaluator sets properties on the
				 * Chromosome, and we want it to do that in all other cases.
				 */
				Chromosome bestFitClone = bestFitIndividual.clone();
				generationStatistics.setKnownSolutionProximity(this.knownSolutionFitnessEvaluator
						.evaluate(bestFitClone));
			}
		}

		return bestFitIndividual;
	}

	public void increaseAge() {
		int originalSize = this.individuals.size();
		Chromosome individual = null;

		/*
		 * actualIndex is used for removing individuals from this Population
		 * since the size will decrement each time an individual is removed,
		 * thus making the loop index incorrect.
		 */
		int actualIndex = 0;
		for (int i = 0; i < originalSize; i++) {
			individual = this.individuals.get(actualIndex);

			/*
			 * A value less than zero represents immortality, so always increase
			 * the age in that case. Otherwise, only increase the age if this
			 * individual has more generations to live.
			 */
			if (this.lifespan < 0 || individual.getAge() < this.lifespan) {
				individual.increaseAge();
				actualIndex++;
			} else {
				/*
				 * We have to remove by index in case there is more than one
				 * Chromosome that is equal, since more than likely the unique
				 * key will not have been generated from database yet.
				 */
				this.removeIndividual(actualIndex);
			}
		}
	}

	/*
	 * This method depends on the totalFitness and individuals' fitness being
	 * accurately maintained. Returns the index of the Chromosome chosen.
	 */
	public int spinIndexRouletteWheel() {
		long randomIndex = (int) (Math.random() * totalFitness);

		int winningIndex = -1;
		Chromosome nextIndividual = null;

		for (int i = 0; i < individuals.size(); i++) {
			nextIndividual = individuals.get(i);
			if (nextIndividual.getFitness() == null) {
				log.warn("Attempted to spin roulette wheel but an individual was found with a null fitness value.  Please make a call to evaluateFitness() before attempting to spin the roulette wheel. "
						+ nextIndividual);
			}

			randomIndex -= nextIndividual.getFitness();

			/*
			 * If we have subtracted everything from randomIndex, then the ball
			 * has stopped rolling.
			 */
			if (randomIndex <= 0) {
				winningIndex = i;

				break;
			}
		}

		return winningIndex;
	}

	/**
	 * @return the individuals
	 */
	public List<Chromosome> getIndividuals() {
		return Collections.unmodifiableList(individuals);
	}

	/**
	 * Removes an individual from the population based on its index. This is
	 * much more efficient than removing by equality.
	 * 
	 * @param individual
	 */
	public Chromosome removeIndividual(int indexToRemove) {
		if (indexToRemove < 0 || indexToRemove > this.individuals.size() - 1) {
			log.error("Tried to remove individual by invalid index " + indexToRemove
					+ " from population of size " + this.size() + ".  Returning.");

			return null;
		}

		this.totalFitness -= this.individuals.get(indexToRemove).getFitness();

		return this.individuals.remove(indexToRemove);
	}

	public void removeIndividual(Chromosome individual) {
		if (!this.individuals.contains(individual)) {
			log.error("Tried to remove individual from population but the individual already doesn't exist.  Returning."
					+ individual);

			return;
		}

		this.totalFitness -= individual.getFitness();

		this.individuals.remove(individual);
	}

	public void clearIndividuals() {
		this.individuals.clear();

		this.totalFitness = 0.0;
	}

	/**
	 * @param individual
	 */
	public void addIndividual(Chromosome individual) {
		this.individuals.add(individual);

		/*
		 * Only evaluate this individual if it hasn't been evaluated yet by some
		 * other process.
		 */
		if (individual.isDirty()) {
			fitnessEvaluator.evaluate(individual);
		}

		this.totalFitness += individual.getFitness();
	}

	public int size() {
		return this.individuals.size();
	}

	public void sortIndividuals() {
		Collections.sort(individuals, this.fitnessComparator);
	}

	/**
	 * Prints every Chromosome in this population in ascending order by fitness.
	 * Note that a lower fitness value can be a better value depending on the
	 * strategy.
	 */
	public void printAscending() {
		this.sortIndividuals();

		int index = this.size();
		for (Chromosome individual : this.individuals) {
			log.info("Chromosome " + index + ": " + individual);
			index--;
		}
	}

	/**
	 * @return the totalFitness
	 */
	public Double getTotalFitness() {
		return totalFitness;
	}

	/**
	 * @param obj
	 *            the Object to set
	 */
	public void setGeneticStructure(Object obj) {
		this.breeder.setGeneticStructure(obj);
	}

	/**
	 * @param breeder
	 *            the breeder to set
	 */
	public void setBreeder(Breeder breeder) {
		this.breeder = breeder;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Required
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @param fitnessComparator
	 *            the fitnessComparator to set
	 */
	@Required
	public void setFitnessComparator(FitnessComparator fitnessComparator) {
		this.fitnessComparator = fitnessComparator;
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
	 * @param lifespan
	 *            the lifespan to set
	 */
	@Required
	public void setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}

	/**
	 * This is NOT required. We will not always know the solution. In fact, that
	 * should be the rare case.
	 * 
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 */
	public void setKnownSolutionFitnessEvaluator(FitnessEvaluator knownSolutionFitnessEvaluator) {
		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
	}

	/**
	 * This is NOT required.
	 * 
	 * @param compareToKnownSolution
	 *            the compareToKnownSolution to set
	 */
	public void setCompareToKnownSolution(Boolean compareToKnownSolution) {
		this.compareToKnownSolution = compareToKnownSolution;
	}
}
