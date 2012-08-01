package com.ciphertool.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.util.ChromosomeGenerator;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class Population {
	private Logger log = Logger.getLogger(getClass());
	private ChromosomeGenerator chromosomeGenerator;
	private List<Chromosome> individuals;
	private FitnessEvaluator fitnessEvaluator;
	private long totalFitness;
	private TaskExecutor taskExecutor;

	public Population() {
	}

	public void populateIndividuals(Integer numIndividuals) {
		if (this.individuals == null) {
			this.individuals = new ArrayList<Chromosome>();
		}

		int individualsAdded = 0;

		for (int i = this.individuals.size(); i < numIndividuals; i++) {
			this.individuals.add((Chromosome) chromosomeGenerator.generateChromosome());

			individualsAdded++;
		}

		log.debug("Added " + individualsAdded + " individuals to the population.");
	}

	private class EvaluatorTask implements Callable<Integer> {

		private Chromosome chromosome;

		public EvaluatorTask(Chromosome chromosome) {
			this.chromosome = chromosome;
		}

		@Override
		public Integer call() throws Exception {
			return fitnessEvaluator.evaluate(this.chromosome);
		}
	}

	public void evaluateAllFitness() {
		List<FutureTask<Integer>> futureTasks = new ArrayList<FutureTask<Integer>>();
		FutureTask<Integer> futureTask = null;

		for (Chromosome individual : individuals) {
			futureTask = new FutureTask<Integer>(new EvaluatorTask(individual));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
		}

		for (FutureTask<Integer> future : futureTasks) {
			try {
				/*
				 * TODO it may be more efficient to keep a list of all the
				 * futures that are finished by calling the isDone() method,
				 * since get() blocks.
				 */
				future.get();
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for EvaluatorTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for EvaluatorTask ", ee);
			}
		}
	}

	public Chromosome evaluateFitness() {
		this.evaluateAllFitness();

		this.totalFitness = 0;

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

		log.info("Population of size " + individuals.size() + " has an average fitness of "
				+ String.format("%1$,.2f", averageFitness));

		log.info("Best fitness in population is " + bestFitIndividual.getFitness());

		return bestFitIndividual;
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
		return this.evaluateFitness();
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
		return individuals;
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

		Chromosome chromosomeToRemove = individuals.get(indexToRemove);
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

	/**
	 * @param individual
	 */
	public void addIndividual(Chromosome individual) {
		this.individuals.add(individual);

		fitnessEvaluator.evaluate(individual);

		this.totalFitness += individual.getFitness();
	}

	public int size() {
		return this.individuals.size();
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
	 * @param taskExecutor
	 *            the taskExecutor to set
	 */
	@Required
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}
