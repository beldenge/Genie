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

package com.ciphertool.genetics;

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

import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessComparator;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class LatticePopulation implements Population {
	private Logger log = LoggerFactory.getLogger(getClass());
	private Breeder breeder;
	private Chromosome[][] individuals;
	private Chromosome[][] backup;
	private FitnessEvaluator fitnessEvaluator;
	private FitnessComparator fitnessComparator;
	private Selector selector;
	private Double totalFitness = 0.0;
	private TaskExecutor taskExecutor;
	private ChromosomePrinter chromosomePrinter;
	private int lifespan;
	private FitnessEvaluator knownSolutionFitnessEvaluator;
	private static final boolean COMPARE_TO_KNOWN_SOLUTION_DEFAULT = false;
	private Boolean compareToKnownSolution = COMPARE_TO_KNOWN_SOLUTION_DEFAULT;
	private boolean stopRequested;
	private int latticeRows;
	private int latticeColumns;

	public LatticePopulation() {
	}

	/**
	 * A concurrent task for adding a brand new Chromosome to the population.
	 */
	protected class GeneratorTask implements Callable<SpatialChromosomeWrapper> {
		private int xPos;
		private int yPos;

		public GeneratorTask(int xPos, int yPos) {
			this.xPos = xPos;
			this.yPos = yPos;
		}

		@Override
		public SpatialChromosomeWrapper call() throws Exception {
			return new SpatialChromosomeWrapper(xPos, yPos, breeder.breed());
		}
	}

	public int breed() {
		individuals = new Chromosome[latticeRows][latticeColumns];

		List<FutureTask<SpatialChromosomeWrapper>> futureTasks = new ArrayList<FutureTask<SpatialChromosomeWrapper>>();
		FutureTask<SpatialChromosomeWrapper> futureTask = null;

		int individualsAdded = 0;
		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				futureTask = new FutureTask<SpatialChromosomeWrapper>(new GeneratorTask(x, y));
				futureTasks.add(futureTask);

				this.taskExecutor.execute(futureTask);
			}
		}

		for (FutureTask<SpatialChromosomeWrapper> future : futureTasks) {
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
	 * This method executes all the fitness evaluations concurrently.
	 * 
	 * @throws InterruptedException
	 *             if stop is requested
	 */
	protected void doConcurrentFitnessEvaluations() throws InterruptedException {
		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		int evaluationCount = 0;
		Chromosome individual = null;
		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				individual = this.individuals[x][y];
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
		}

		if (log.isDebugEnabled()) {
			log.debug("Evaluations carried out: " + evaluationCount);
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
	}

	@Override
	public Chromosome evaluateFitness(GenerationStatistics generationStatistics) throws InterruptedException {
		this.doConcurrentFitnessEvaluations();

		this.totalFitness = 0.0;

		Chromosome bestFitIndividual = null;
		Chromosome individual = null;

		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				individual = this.individuals[x][y];

				this.totalFitness += individual.getFitness();

				if (bestFitIndividual == null || individual.getFitness() > bestFitIndividual.getFitness()) {
					bestFitIndividual = individual;
				}
			}
		}

		Double averageFitness = Double.valueOf(this.totalFitness) / Double.valueOf(latticeRows * latticeColumns);

		if (generationStatistics != null) {
			generationStatistics.setAverageFitness(averageFitness);
			generationStatistics.setBestFitness(bestFitIndividual.getFitness());

			if (this.compareToKnownSolution) {
				/*
				 * We have to clone the best fit individual since the knownSolutionFitnessEvaluator sets properties on
				 * the Chromosome, and we want it to do that in all other cases.
				 */
				Chromosome bestFitClone = bestFitIndividual.clone();
				generationStatistics.setKnownSolutionProximity(this.knownSolutionFitnessEvaluator
						.evaluate(bestFitClone));
			}
		}

		return bestFitIndividual;
	}

	@Override
	public int increaseAge() throws InterruptedException {
		Chromosome individual = null;
		int individualsRemoved = 0;

		/*
		 * We have to iterate backwards since the size will decrement each time an individual is removed.
		 */
		for (int x = latticeRows - 1; x >= 0; x--) {
			for (int y = latticeColumns - 1; y >= 0; y--) {
				if (stopRequested) {
					throw new InterruptedException("Stop requested during age increase.");
				}

				individual = this.individuals[x][y];

				/*
				 * A value less than zero represents immortality, so always increase the age in that case. Otherwise,
				 * only increase the age if this individual has more generations to live.
				 */
				if (this.lifespan < 0 || individual.getAge() < this.lifespan) {
					individual.increaseAge();
				} else {
					/*
					 * We have to remove by index in case there is more than one Chromosome that is equal, since more
					 * than likely the unique key will not have been generated from database yet.
					 */
					this.removeIndividual(x, y);
					individualsRemoved++;
				}
			}
		}

		return individualsRemoved;
	}

	/*
	 * This method depends on the totalFitness and individuals' fitness being accurately maintained. Returns the chosen
	 * Chromosome.
	 */
	public Chromosome selectIndex(int row, int column) {
		List<Chromosome> nearbyIndividuals = new ArrayList<Chromosome>();

		// immediately above
		if (row > 0) {
			nearbyIndividuals.add(this.individuals[row - 1][column]);
		}

		// top-right diagonal
		if (row > 0 && column < this.individuals[row - 1].length - 1) {
			nearbyIndividuals.add(this.individuals[row - 1][column + 1]);
		}

		// immediate right
		if (column < this.individuals[row].length - 1) {
			nearbyIndividuals.add(this.individuals[row][column + 1]);
		}

		// bottom-right diagonal
		if (row < this.individuals.length - 1 && column < this.individuals[row + 1].length - 1) {
			nearbyIndividuals.add(this.individuals[row + 1][column + 1]);
		}

		// immediately below
		if (row < this.individuals.length - 1) {
			nearbyIndividuals.add(this.individuals[row + 1][column]);
		}

		// bottom-left diagonal
		if (row < this.individuals.length - 1 && column > 0) {
			nearbyIndividuals.add(this.individuals[row + 1][column - 1]);
		}

		// immediate left
		if (column > 0) {
			nearbyIndividuals.add(this.individuals[row][column - 1]);
		}

		// top-left diagonal
		if (row > 0 && column > 0) {
			nearbyIndividuals.add(this.individuals[row - 1][column - 1]);
		}

		Double subsetFitness = 0.0;
		for (Chromosome individual : nearbyIndividuals) {
			subsetFitness += individual.getFitness();
		}

		int index = this.selector.getNextIndex(nearbyIndividuals, subsetFitness);

		return nearbyIndividuals.get(index);
	}

	public List<Chromosome> selectIndices(int row, int column) {
		List<Chromosome> selectedIndividuals = new ArrayList<Chromosome>();
		List<Chromosome> nearbyIndividuals = new ArrayList<Chromosome>();

		// immediately above
		if (row > 0) {
			nearbyIndividuals.add(this.individuals[row - 1][column]);
		}

		// top-right diagonal
		if (row > 0 && column < this.individuals[row - 1].length - 1) {
			nearbyIndividuals.add(this.individuals[row - 1][column + 1]);
		}

		// immediate right
		if (column < this.individuals[row].length - 1) {
			nearbyIndividuals.add(this.individuals[row][column + 1]);
		}

		// bottom-right diagonal
		if (row < this.individuals.length - 1 && column < this.individuals[row + 1].length - 1) {
			nearbyIndividuals.add(this.individuals[row + 1][column + 1]);
		}

		// immediately below
		if (row < this.individuals.length - 1) {
			nearbyIndividuals.add(this.individuals[row + 1][column]);
		}

		// bottom-left diagonal
		if (row < this.individuals.length - 1 && column > 0) {
			nearbyIndividuals.add(this.individuals[row + 1][column - 1]);
		}

		// immediate left
		if (column > 0) {
			nearbyIndividuals.add(this.individuals[row][column - 1]);
		}

		// top-left diagonal
		if (row > 0 && column > 0) {
			nearbyIndividuals.add(this.individuals[row - 1][column - 1]);
		}

		Double subsetFitness = 0.0;
		for (Chromosome individual : nearbyIndividuals) {
			subsetFitness += individual.getFitness();
		}

		int index = this.selector.getNextIndex(nearbyIndividuals, subsetFitness);

		selectedIndividuals.add(nearbyIndividuals.get(index));

		subsetFitness -= nearbyIndividuals.get(index).getFitness();
		nearbyIndividuals.remove(index);

		index = this.selector.getNextIndex(nearbyIndividuals, subsetFitness);
		selectedIndividuals.add(nearbyIndividuals.get(index));

		return selectedIndividuals;
	}

	/**
	 * @return the individuals
	 */
	public Chromosome[][] getLatticeIndividuals() {
		return individuals;
	}

	/**
	 * Removes an individual from the population based on its index. This is much more efficient than removing by
	 * equality.
	 * 
	 * @param individual
	 */
	public Chromosome removeIndividual(int row, int column) {
		if (row < 0 || row > this.individuals.length - 1) {
			log.error("Tried to remove individual by invalid row index " + row + " from population with "
					+ this.individuals.length + " rows.  Returning.");

			return null;
		}

		if (column < 0 || column > this.individuals[0].length - 1) {
			log.error("Tried to remove individual by invalid column index " + column + " from population with "
					+ this.individuals[0].length + " columns.  Returning.");

			return null;
		}

		this.totalFitness -= this.individuals[row][column].getFitness();

		return this.individuals[row][column] = null;
	}

	@Override
	public void recoverFromBackup() {
		if (this.backup == null || this.backup.length == 0 || this.backup[0].length == 0) {
			log.info("Attempted to recover from backup, but backup was empty.  Nothing to do.");

			return;
		}

		clearIndividuals();

		addAllIndividuals(this.backup);
	}

	@Override
	public void backupIndividuals() {
		this.backup = new Chromosome[latticeRows][latticeColumns];

		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				backup[x][y] = this.individuals[x][y];
			}
		}
	}

	@Override
	public void clearIndividuals() {
		this.individuals = new Chromosome[latticeRows][latticeColumns];

		this.totalFitness = 0.0;
	}

	public void addAllIndividuals(Chromosome[][] individuals) {
		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				addIndividual(new SpatialChromosomeWrapper(x, y, individuals[x][y]));
			}
		}
	}

	/**
	 * @param individual
	 */
	public boolean addIndividual(SpatialChromosomeWrapper wrappedIndividual) {
		Chromosome individual = wrappedIndividual.getChromosome();

		this.individuals[wrappedIndividual.getXPos()][wrappedIndividual.getYPos()] = individual;

		individual.setPopulation(this);

		/*
		 * Only evaluate this individual if it hasn't been evaluated yet by some other process.
		 */
		boolean needsEvaluation = individual.isEvaluationNeeded();
		if (needsEvaluation) {
			individual.setFitness(fitnessEvaluator.evaluate(individual));
		}

		this.totalFitness += individual.getFitness();

		return needsEvaluation;
	}

	@Override
	public int size() {
		return latticeRows * latticeColumns;
	}

	public List<Chromosome> getSortedIndividuals() {
		List<Chromosome> individualsAsList = new ArrayList<Chromosome>();

		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				individualsAsList.add(individuals[x][y]);
			}
		}

		Collections.sort(individualsAsList, this.fitnessComparator);

		return individualsAsList;
	}

	@Override
	public List<Chromosome> getIndividuals() {
		List<Chromosome> individualsAsList = new ArrayList<Chromosome>();

		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				individualsAsList.add(individuals[x][y]);
			}
		}

		Collections.sort(individualsAsList, this.fitnessComparator);

		return individualsAsList;
	}

	public Chromosome[][] getIndividualsAsArray() {
		return this.individuals;
	}

	/**
	 * Prints every Chromosome in this population in ascending order by fitness. Note that a lower fitness value can be
	 * a better value depending on the strategy.
	 */
	@Override
	public void printAscending() {
		List<Chromosome> sortedIndividuals = this.getSortedIndividuals();

		int fitnessIndex = this.size();
		for (Chromosome individual : sortedIndividuals) {
			log.info("Chromosome " + fitnessIndex + ": " + chromosomePrinter.print(individual));
			fitnessIndex--;
		}
	}

	/**
	 * @return the totalFitness
	 */
	public Double getTotalFitness() {
		return totalFitness;
	}

	public void requestStop() {
		this.stopRequested = true;
	}

	/**
	 * @param stopRequested
	 *            the stopRequested to set
	 */
	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
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
	 * @param selector
	 *            the selector to set
	 */
	@Required
	public void setSelector(Selector selector) {
		this.selector = selector;
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
	 * This is NOT required. We will not always know the solution. In fact, that should be the rare case.
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

	/**
	 * @param chromosomePrinter
	 *            the chromosomePrinter to set
	 */
	@Required
	public void setChromosomePrinter(ChromosomePrinter chromosomePrinter) {
		this.chromosomePrinter = chromosomePrinter;
	}

	/**
	 * @return the latticeRows
	 */
	public int getLatticeRows() {
		return latticeRows;
	}

	/**
	 * @return the latticeColumns
	 */
	public int getLatticeColumns() {
		return latticeColumns;
	}

	/**
	 * @param latticeRows
	 *            the latticeRows to set
	 */
	@Required
	public void setLatticeRows(int latticeRows) {
		this.latticeRows = latticeRows;
	}

	/**
	 * @param latticeColumns
	 *            the latticeColumns to set
	 */
	@Required
	public void setLatticeColumns(int latticeColumns) {
		this.latticeColumns = latticeColumns;
	}
}
