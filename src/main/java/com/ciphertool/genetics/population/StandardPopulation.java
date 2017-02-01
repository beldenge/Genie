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

import java.math.BigDecimal;
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
	private FitnessEvaluator		majorFitnessEvaluator;
	private FitnessComparator		fitnessComparator;
	private Selector				selector;
	private BigDecimal				totalFitness						= BigDecimal.ZERO;
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
	protected class EvaluationTask implements Callable<Void> {
		private Chromosome			chromosome;
		private FitnessEvaluator	fitnessEvaluator;

		public EvaluationTask(Chromosome chromosome, FitnessEvaluator fitnessEvaluator) {
			this.chromosome = chromosome;
			this.fitnessEvaluator = fitnessEvaluator;
		}

		@Override
		public Void call() throws Exception {
			this.chromosome.setFitness(this.fitnessEvaluator.evaluate(this.chromosome));

			return null;
		}
	}

	/**
	 * This method executes all the fitness evaluations concurrently.
	 * 
	 * @throws InterruptedException
	 *             if stop is requested
	 */
	protected int doConcurrentFitnessEvaluations(FitnessEvaluator fitnessEvaluator, Integer top, Boolean force)
			throws InterruptedException {
		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		int evaluationCount = 0;

		int stop = (top == null || top <= 0) ? 0 : (this.individuals.size() - top);
		Chromosome individual;

		for (int i = this.individuals.size() - 1; i >= stop; i--) {
			individual = this.individuals.get(i);

			/*
			 * Only evaluate individuals that have changed since the last evaluation.
			 */
			if (individual.isEvaluationNeeded() || (force != null && force)) {
				evaluationCount++;
				futureTask = new FutureTask<Void>(new EvaluationTask(individual, fitnessEvaluator));
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
				log.error("Caught InterruptedException while waiting for EvaluationTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for EvaluationTask ", ee);
			}
		}

		return evaluationCount;
	}

	@Override
	public Chromosome evaluateFitness(GenerationStatistics generationStatistics) throws InterruptedException {
		generationStatistics.setNumberOfEvaluations(this.doConcurrentFitnessEvaluations(this.fitnessEvaluator, -1, false));

		return updateFitness(generationStatistics);
	}

	@Override
	public Chromosome performMajorEvaluation(GenerationStatistics generationStatistics, Double percentageToEvaluate)
			throws InterruptedException {
		this.sortIndividuals();

		int top = Math.round((int) (this.size() * percentageToEvaluate));

		generationStatistics.setNumberOfMajorEvaluations(this.doConcurrentFitnessEvaluations(this.majorFitnessEvaluator, top, true));

		return updateFitness(generationStatistics);
	}

	protected Chromosome updateFitness(GenerationStatistics generationStatistics) {
		this.totalFitness = BigDecimal.ZERO;

		Chromosome bestFitIndividual = null;

		for (Chromosome individual : individuals) {
			this.totalFitness = this.totalFitness.add(individual.getFitness());

			if (bestFitIndividual == null || individual.getFitness().compareTo(bestFitIndividual.getFitness()) > 0) {
				bestFitIndividual = individual;
			}
		}

		BigDecimal averageFitness = this.totalFitness.divide(BigDecimal.valueOf(individuals.size()));

		if (generationStatistics != null) {
			generationStatistics.setAverageFitness(averageFitness);
			generationStatistics.setBestFitness(bestFitIndividual.getFitness());

			if (this.compareToKnownSolution) {
				/*
				 * We have to clone the best fit individual since the knownSolutionFitnessEvaluator sets properties on
				 * the Chromosome, and we want it to do that in all other cases.
				 */
				Chromosome bestFitClone = bestFitIndividual.clone();
				generationStatistics.setKnownSolutionProximity(this.knownSolutionFitnessEvaluator.evaluate(bestFitClone).multiply(BigDecimal.valueOf(100.0)));
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

		this.totalFitness = this.totalFitness.subtract(this.individuals.get(indexToRemove).getFitness());

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

		this.totalFitness = BigDecimal.ZERO;
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

		this.totalFitness = this.totalFitness.add(individual.getFitness() == null ? BigDecimal.ZERO : individual.getFitness());

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
			log.info("Chromosome " + (i + 1) + ": " + chromosomePrinter.print(this.individuals.get(i)));
		}
	}

	@Override
	public void reIndexSelector() {
		this.selector.reIndex(this.individuals);
	}

	/**
	 * @return the totalFitness
	 */
	public BigDecimal getTotalFitness() {
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

		this.majorFitnessEvaluator.setGeneticStructure(obj);
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

	@Override
	public void setMajorFitnessEvaluator(FitnessEvaluator majorFitnessEvaluator) {
		this.majorFitnessEvaluator = majorFitnessEvaluator;
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
