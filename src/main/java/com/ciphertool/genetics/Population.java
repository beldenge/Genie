package com.ciphertool.genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class Population {
	private Logger log = Logger.getLogger(getClass());
	private ChromosomeGenerator chromosomeGenerator;
	private List<Chromosome> individuals;
	private FitnessEvaluator fitnessEvaluator;
	private long totalFitness;

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

	public long evaluateFitness() {
		this.totalFitness = 0;

		for (Chromosome individual : individuals) {
			this.totalFitness += fitnessEvaluator.evaluate(individual);
		}

		return this.totalFitness;
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
		this.evaluateFitness();

		Chromosome bestFitIndividual = null;

		for (Chromosome individual : individuals) {
			if (bestFitIndividual == null
					|| individual.getFitness() > bestFitIndividual.getFitness()) {
				bestFitIndividual = individual;
			}
		}

		return bestFitIndividual;
	}

	/*
	 * This method depends on the totalFitness and individuals' fitness being
	 * accurately maintained.
	 */
	public Chromosome spinRouletteWheel() {
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

	/**
	 * @return the individuals
	 */
	public List<Chromosome> getIndividuals() {
		return individuals;
	}

	/**
	 * @param individual
	 */
	public void removeIndividual(Chromosome individual) {
		this.individuals.remove(individual);

		this.totalFitness -= individual.getFitness();
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
}
