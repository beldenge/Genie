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
import com.ciphertool.genetics.entities.SpatialChromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessComparator;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class LatticePopulation implements Population {
	private Logger					log									= LoggerFactory.getLogger(getClass());
	private Breeder					breeder;
	private SpatialChromosome[][]	individuals;
	private SpatialChromosome[][]	backup;
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
	private int						latticeRows;
	private int						latticeColumns;
	private int						maxToPrint;

	public LatticePopulation() {
	}

	/**
	 * A concurrent task for adding a brand new Chromosome to the population.
	 */
	protected class GeneratorTask implements Callable<SpatialChromosome> {
		private int	xPos;
		private int	yPos;

		public GeneratorTask(int xPos, int yPos) {
			this.xPos = xPos;
			this.yPos = yPos;
		}

		@Override
		public SpatialChromosome call() throws Exception {
			SpatialChromosome chromosome = (SpatialChromosome) breeder.breed();
			chromosome.setXPos(xPos);
			chromosome.setYPos(yPos);

			return chromosome;
		}
	}

	public int breed() {
		individuals = new SpatialChromosome[latticeRows][latticeColumns];

		List<FutureTask<SpatialChromosome>> futureTasks = new ArrayList<FutureTask<SpatialChromosome>>();
		FutureTask<SpatialChromosome> futureTask = null;

		int individualsAdded = 0;
		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				futureTask = new FutureTask<SpatialChromosome>(new GeneratorTask(x, y));
				futureTasks.add(futureTask);

				this.taskExecutor.execute(futureTask);
			}
		}

		for (FutureTask<SpatialChromosome> future : futureTasks) {
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
		private Chromosome chromosome;

		public EvaluationTask(Chromosome chromosome) {
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
					futureTask = new FutureTask<Void>(new EvaluationTask(individual));
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
				generationStatistics.setKnownSolutionProximity(this.knownSolutionFitnessEvaluator.evaluate(bestFitClone)
						* 100.0);
			}
		}

		return bestFitIndividual;
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

	public List<SpatialChromosome> selectIndices(int row, int column) {
		List<SpatialChromosome> selectedIndividuals = new ArrayList<SpatialChromosome>();
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

		selectedIndividuals.add((SpatialChromosome) nearbyIndividuals.get(index));

		subsetFitness -= nearbyIndividuals.get(index).getFitness();
		nearbyIndividuals.remove(index);

		index = this.selector.getNextIndex(nearbyIndividuals, subsetFitness);
		selectedIndividuals.add((SpatialChromosome) nearbyIndividuals.get(index));

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
		this.backup = new SpatialChromosome[latticeRows][latticeColumns];

		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				backup[x][y] = this.individuals[x][y];
			}
		}
	}

	@Override
	public void clearIndividuals() {
		this.individuals = new SpatialChromosome[latticeRows][latticeColumns];

		this.totalFitness = 0.0;
	}

	public void addAllIndividuals(SpatialChromosome[][] individuals) {
		for (int x = 0; x < latticeRows; x++) {
			for (int y = 0; y < latticeColumns; y++) {
				addIndividual(individuals[x][y]);
			}
		}
	}

	/**
	 * @param individual
	 */
	public boolean addIndividual(SpatialChromosome individual) {
		this.individuals[individual.getXPos()][individual.getYPos()] = individual;

		individual.setPopulation(this);

		this.totalFitness += individual.getFitness();

		return individual.isEvaluationNeeded();
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

		int size = sortedIndividuals.size();

		for (int i = size - maxToPrint; i < size; i++) {
			log.info("Chromosome " + (i + 1) + ": " + chromosomePrinter.print(sortedIndividuals.get(i)));
		}
	}

	@Override
	public void reIndexSelector() {
		// Nothing to do
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

	@Override
	public void setTargetSize(int targetSize) {
		int sqrt = (int) Math.sqrt(targetSize);

		if ((sqrt * sqrt) != targetSize) {
			throw new IllegalArgumentException("The target population size must have an integer square root for "
					+ getClass().getSimpleName());
		}

		this.latticeColumns = sqrt;
		this.latticeRows = sqrt;
	}

	/**
	 * @param maxToPrint
	 *            the maxToPrint to set
	 */
	@Required
	public void setMaxToPrint(int maxToPrint) {
		this.maxToPrint = maxToPrint;
	}

	@Override
	public Chromosome performMajorEvaluation(GenerationStatistics generationStatistics, Double percentageToEvaluate)
			throws InterruptedException {
		throw new UnsupportedOperationException("Method not yet implemented");
	}

	@Override
	public void setMajorFitnessEvaluator(FitnessEvaluator majorFitnessEvaluator) {
		// Nothing to do
	}
}
