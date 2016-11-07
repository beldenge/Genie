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

package com.ciphertool.genetics.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.Breeder;
import com.ciphertool.genetics.ChromosomePrinter;
import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessComparator;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class StandardPopulation implements Population {
	private Logger					log									= LoggerFactory.getLogger(getClass());
	private Breeder					breeder;
	private List<Chromosome>		individuals							= new ArrayList<Chromosome>();
	private List<Chromosome>		backup								= new ArrayList<Chromosome>();
	private FitnessEvaluator		fitnessEvaluator;
	private FitnessComparator		fitnessComparator;
	private Selector				selector;
	private Double					totalFitness						= 0.0;
	private TaskExecutor			taskExecutor;
	private ChromosomePrinter		chromosomePrinter;
	private FitnessEvaluator		knownSolutionFitnessEvaluator;
	private static final boolean	COMPARE_TO_KNOWN_SOLUTION_DEFAULT	= false;
	private Boolean					compareToKnownSolution				= COMPARE_TO_KNOWN_SOLUTION_DEFAULT;
	private boolean					stopRequested;
	private int						targetSize;
	private int						maxToPrint;

	public StandardPopulation() {
	}

	/**
	 * A concurrent task for adding a brand new Chromosome to the population.
	 */
	protected class GeneratorTask implements Callable<Chromosome> {
		public GeneratorTask() {
		}

		@Override
		public Chromosome call() throws Exception {
			return breeder.breed();
		}
	}

	public int breed() {
		List<FutureTask<Chromosome>> futureTasks = new ArrayList<FutureTask<Chromosome>>();
		FutureTask<Chromosome> futureTask = null;

		int individualsAdded = 0;
		for (int i = this.individuals.size(); i < targetSize; i++) {
			futureTask = new FutureTask<Chromosome>(new GeneratorTask());
			futureTasks.add(futureTask);

			this.taskExecutor.execute(futureTask);
		}

		for (FutureTask<Chromosome> future : futureTasks) {
			if (stopRequested) {
				return individualsAdded;
			}

			try {
				this.addIndividual(future.get());

				individualsAdded++;
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for GeneratorTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for GeneratorTask ", ee);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Added " + individualsAdded + " individuals to the population.");
		}

		return individualsAdded;
	}

	/**
	 * A concurrent task for evaluating the fitness of a Chromosome.
	 */
	protected class EvaluatorTask implements Callable<Void> {
		private Chromosome chromosome;

		public EvaluatorTask(Chromosome chromosome) {
			this.chromosome = chromosome;
		}

		@Override
		public Void call() throws Exception {
			this.chromosome.setFitness(fitnessEvaluator.evaluate(this.chromosome));

			return null;
		}
	}

	/**
	 * A concurrent task for evaluating the fitness of a Chromosome.
	 */
	protected class KnownSolutionEvaluatorTask implements Callable<Void> {
		private Chromosome chromosome;

		public KnownSolutionEvaluatorTask(Chromosome chromosome) {
			this.chromosome = chromosome;
		}

		@Override
		public Void call() throws Exception {
			knownSolutionFitnessEvaluator.evaluate(this.chromosome);

			return null;
		}
	}

	/**
	 * This method executes all the fitness evaluations concurrently.
	 * 
	 * @throws InterruptedException
	 *             if stop is requested
	 */
	protected int doConcurrentFitnessEvaluations() throws InterruptedException {
		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		int evaluationCount = 0;
		for (Chromosome individual : individuals) {
			/*
			 * Only evaluate individuals that have changed since the last evaluation.
			 */
			if (individual.isEvaluationNeeded()) {
				evaluationCount++;
				futureTask = new FutureTask<Void>(new EvaluatorTask(individual));
				futureTasks.add(futureTask);
				this.taskExecutor.execute(futureTask);
			}
		}

		for (FutureTask<Void> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during concurrent fitness evaluations.");
			}

			try {
				future.get();
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for EvaluatorTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for EvaluatorTask ", ee);
			}
		}

		return evaluationCount;
	}

	@Override
	public Chromosome evaluateFitness(GenerationStatistics generationStatistics) throws InterruptedException {
		generationStatistics.setNumberOfEvaluations(this.doConcurrentFitnessEvaluations());

		this.totalFitness = 0.0;

		Chromosome bestFitIndividual = null;

		for (Chromosome individual : individuals) {
			this.totalFitness += individual.getFitness();

			if (bestFitIndividual == null || individual.getFitness() > bestFitIndividual.getFitness()) {
				bestFitIndividual = individual;
			}
		}

		Double averageFitness = Double.valueOf(this.totalFitness) / Double.valueOf(individuals.size());

		if (generationStatistics != null) {
			generationStatistics.setAverageFitness(averageFitness);
			generationStatistics.setBestFitness(bestFitIndividual.getFitness());

			if (this.compareToKnownSolution) {
				/*
				 * We have to clone the best fit individual since the knownSolutionFitnessEvaluator sets properties on
				 * the Chromosome, and we want it to do that in all other cases.
				 */
				Chromosome bestFitClone = bestFitIndividual.clone();
				generationStatistics.setKnownSolutionProximity(this.knownSolutionFitnessEvaluator.evaluate(bestFitClone));
			}
		}

		return bestFitIndividual;
	}

	/*
	 * This method depends on the totalFitness and individuals' fitness being accurately maintained. Returns the index
	 * of the Chromosome chosen.
	 */
	public int selectIndex() {
		return this.selector.getNextIndex(individuals, totalFitness);
	}

	/**
	 * @return the individuals
	 */
	@Override
	public List<Chromosome> getIndividuals() {
		return Collections.unmodifiableList(individuals);
	}

	/**
	 * Removes an individual from the population based on its index. This is much more efficient than removing by
	 * equality.
	 * 
	 * @param individual
	 */
	public Chromosome removeIndividual(int indexToRemove) {
		if (indexToRemove < 0 || indexToRemove > this.individuals.size() - 1) {
			log.error("Tried to remove individual by invalid index " + indexToRemove + " from population of size "
					+ this.size() + ".  Returning.");

			return null;
		}

		this.totalFitness -= this.individuals.get(indexToRemove).getFitness();

		return this.individuals.remove(indexToRemove);
	}

	@Override
	public void recoverFromBackup() {
		if (this.backup == null || this.backup.isEmpty()) {
			log.info("Attempted to recover from backup, but backup was empty.  Nothing to do.");

			return;
		}

		clearIndividuals();

		addAllIndividuals(this.backup);
	}

	@Override
	public void backupIndividuals() {
		this.backup.clear();

		this.backup.addAll(this.individuals);
	}

	@Override
	public void clearIndividuals() {
		this.individuals.clear();

		this.totalFitness = 0.0;
	}

	public void addAllIndividuals(List<Chromosome> individuals) {
		for (Chromosome individual : individuals) {
			addIndividual(individual);
		}
	}

	/**
	 * @param individual
	 */
	public boolean addIndividual(Chromosome individual) {
		this.individuals.add(individual);

		individual.setPopulation(this);

		this.totalFitness += individual.getFitness();

		return individual.isEvaluationNeeded();
	}

	public int size() {
		return this.individuals.size();
	}

	public void sortIndividuals() {
		Collections.sort(individuals, this.fitnessComparator);
	}

	/**
	 * Prints every Chromosome in this population in ascending order by fitness. Note that a lower fitness value can be
	 * a better value depending on the strategy.
	 */
	@Override
	public void printAscending() {
		this.sortIndividuals();

		int size = this.individuals.size();

		for (int i = size - maxToPrint; i < size; i++) {
			log.info("Chromosome " + i + ": " + chromosomePrinter.print(this.individuals.get(i)));
		}
	}

	@Override
	public void reIndexSelector() {
		this.selector.reIndex(this.individuals);
	}

	/**
	 * @return the totalFitness
	 */
	public Double getTotalFitness() {
		return totalFitness;
	}

	@Override
	public void requestStop() {
		this.stopRequested = true;
	}

	@Override
	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	@Override
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

	@Override
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

	@Override
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	@Override
	public void setKnownSolutionFitnessEvaluator(FitnessEvaluator knownSolutionFitnessEvaluator) {
		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
	}

	@Override
	public void setCompareToKnownSolution(Boolean compareToKnownSolution) {
		this.compareToKnownSolution = compareToKnownSolution;
	}

	/**
	 * @param chromosomePrinter
	 *            the chromosomePrinter to set
	 */
	@Required
	public void setChromosomePrinter(ChromosomePrinter chromosomePrinter) {
		this.chromosomePrinter = chromosomePrinter;
	}

	@Override
	public void setTargetSize(int targetSize) {
		this.targetSize = targetSize;
	}

	/**
	 * @param maxToPrint
	 *            the maxToPrint to set
	 */
	@Required
	public void setMaxToPrint(int maxToPrint) {
		this.maxToPrint = maxToPrint;
	}
}
