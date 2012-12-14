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
import com.ciphertool.genetics.util.ChromosomeGenerator;
import com.ciphertool.genetics.util.FitnessComparator;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class Population {
	private Logger log = Logger.getLogger(getClass());
	private ChromosomeGenerator chromosomeGenerator;
	private List<Chromosome> individuals = new ArrayList<Chromosome>();
	private FitnessEvaluator fitnessEvaluator;
	private FitnessComparator fitnessComparator;
	private Double totalFitness;
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
			return chromosomeGenerator.generateChromosome();
		}
	}

	public void populateIndividuals(Integer maxIndividuals) {
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
	public void evaluateAllFitness() {
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
		this.evaluateAllFitness();

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
		List<Chromosome> individualsToRemove = new ArrayList<Chromosome>();
		for (Chromosome individual : this.individuals) {
			/*
			 * A value less than zero represents immortality, so always increase
			 * the age in that case. Otherwise, only increase the age if this
			 * individual has more generations to live.
			 */
			if (this.lifespan < 0 || individual.getAge() < this.lifespan) {
				individual.increaseAge();
			} else {
				individualsToRemove.add(individual);
			}
		}

		/*
		 * Remove expired individuals outside the loop to avoid concurrent
		 * modification exceptions.
		 */
		for (Chromosome individualToRemove : individualsToRemove) {
			this.removeIndividual(individualToRemove);
		}
	}

	/**
	 * This method should only really be called at the end of a genetic
	 * algorithm.
	 * 
	 * @return
	 */
	public Chromosome getBestFitIndividual() {
		/*
		 * Evaluate fitness once more for safety so that we are guaranteed to
		 * have updated fitness values.
		 */
		return this.evaluateFitness(null);
	}

	/*
	 * This method depends on the totalFitness and individuals' fitness being
	 * accurately maintained. Returns the actual Chromosome chosen.
	 */
	public Chromosome spinObjectRouletteWheel() {
		long randomIndex = (int) (Math.random() * totalFitness);

		Chromosome chosenIndividual = null;

		for (Chromosome individual : individuals) {
			if (individual.getFitness() == null) {
				log.warn("Attempted to spin roulette wheel but an individual was found with a null fitness value.  Please make a call to evaluateFitness() before attempting to spin the roulette wheel. "
						+ individual);
			}

			randomIndex -= individual.getFitness();

			/*
			 * If we have subtracted everything from randomIndex, then the ball
			 * has stopped rolling.
			 */
			if (randomIndex <= 0) {
				chosenIndividual = individual;

				break;
			}
		}

		return chosenIndividual;
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
	public void removeIndividual(int indexToRemove) {
		if (indexToRemove < 0 || indexToRemove > this.individuals.size() - 1) {
			log.error("Tried to remove individual by invalid index " + indexToRemove
					+ " from population of size " + this.size() + ".  Returning.");

			return;
		}

		this.totalFitness -= this.individuals.get(indexToRemove).getFitness();

		this.individuals.remove(indexToRemove);
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
	 * @param obj
	 *            the Object to set
	 */
	public void setGeneticStructure(Object obj) {
		this.chromosomeGenerator.setGeneticStructure(obj);
	}

	/**
	 * @param chromosomeGenerator
	 *            the chromosomeGenerator to set
	 */
	@Required
	public void setChromosomeGenerator(ChromosomeGenerator chromosomeGenerator) {
		this.chromosomeGenerator = chromosomeGenerator;
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
