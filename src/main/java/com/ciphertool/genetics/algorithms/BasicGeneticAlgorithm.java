package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.util.FitnessComparator;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class BasicGeneticAlgorithm implements GeneticAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	private Integer populationSize;
	private Double survivalRate;
	private Double mutationRate;
	protected Double crossoverRate;
	private Integer maxGenerations;
	private Integer finalSurvivorCount;
	protected Population population;
	protected CrossoverAlgorithm crossoverAlgorithm;
	private FitnessEvaluator fitnessEvaluator;
	private FitnessComparator fitnessComparator;

	public BasicGeneticAlgorithm() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#iterateUntilTermination
	 * ()
	 */
	@Override
	public List<Chromosome> iterateUntilTermination() {
		if (this.population == null) {
			log.info("Attempted to start algorithm with a null population.  Spawning population of size "
					+ populationSize + ".");

			this.spawnInitialPopulation();
		}

		long genesis = System.currentTimeMillis();
		long generationStart = 0;
		int i;
		for (i = 1; i <= maxGenerations; i++) {
			generationStart = System.currentTimeMillis();

			log.info("Starting generation " + i);

			/*
			 * Doing the select first improves performance by a ratio of up to
			 * (1 - survivalRate). It makes more sense as well since only
			 * survivors can reproduce.
			 */
			select();

			crossover();

			mutate();

			population.populateIndividuals(populationSize);

			population.evaluateFitness();

			log.info("Generation " + i + " finished in "
					+ (System.currentTimeMillis() - generationStart) + "ms.");
		}
		log.info("Average generation time is " + ((System.currentTimeMillis() - genesis) / (i - 1))
				+ "ms.");

		List<Chromosome> individuals = this.population.getIndividuals();
		Collections.sort(individuals, fitnessComparator);

		List<Chromosome> bestFitIndividuals = new ArrayList<Chromosome>();
		int chromosomeIndex = (individuals.size() - finalSurvivorCount);

		for (int finalSurvivorIndex = ((chromosomeIndex < 0) ? 0 : chromosomeIndex); finalSurvivorIndex < individuals
				.size(); finalSurvivorIndex++) {
			bestFitIndividuals.add(individuals.get(finalSurvivorIndex));
		}

		return bestFitIndividuals;
	}

	@Override
	public void select() {
		List<Chromosome> individuals = this.population.getIndividuals();

		Collections.sort(individuals, fitnessComparator);

		int initialPopulationSize = this.population.size();

		long survivorIndex = Math.round((initialPopulationSize * (1 - survivalRate)));
		log.debug(survivorIndex + " individuals to be removed from population of size "
				+ this.population.size() + " and survival rate of " + survivalRate + ".");

		for (int i = 0; i < survivorIndex; i++) {
			/*
			 * We must remove the first element every time, since the List is
			 * sorted in ascending order.
			 */
			this.population.removeIndividual(0);
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
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return;
		}

		Chromosome mom = null;
		Chromosome dad = null;
		Chromosome child1 = null;
		Chromosome child2 = null;
		int momIndex = -1;
		int dadIndex = -1;

		int initialPopulationSize = this.population.size();

		long pairsToCrossover = Math.round((initialPopulationSize * crossoverRate) / 2);

		log.debug("Pairs to crossover: " + pairsToCrossover);

		for (int i = 0; i < pairsToCrossover; i++) {
			momIndex = this.population.spinIndexRouletteWheel();
			mom = this.population.getIndividuals().get(momIndex);

			/*
			 * Keep retrying until we find a different Chromosome.
			 */
			do {
				dadIndex = this.population.spinIndexRouletteWheel();
				dad = this.population.getIndividuals().get(dadIndex);
			} while (mom == dad);

			child1 = crossoverAlgorithm.crossover(mom, dad);

			child2 = crossoverAlgorithm.crossover(dad, mom);

			/*
			 * Remove the parents from the population and add the children since
			 * they are guaranteed to be at least as fit as their parents
			 */
			this.population.removeIndividual(momIndex);
			if (dadIndex > momIndex) {
				/*
				 * We have to decrease the index for dad since it was shifted
				 * left after mom was removed.
				 */
				dadIndex--;
			}
			this.population.removeIndividual(dadIndex);

			this.population.addIndividual(child1);
			this.population.addIndividual(child2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#mutate()
	 */
	@Override
	public void mutate() {
		int mutantIndex = -1;

		int initialPopulationSize = this.population.size();

		long mutations = Math.round((initialPopulationSize * mutationRate));

		log.debug("Mutations to perform: " + mutations);

		for (int i = 0; i < mutations; i++) {
			/*
			 * Mutate a gene within a Chromosome
			 */
			mutantIndex = this.population.spinIndexRouletteWheel();
			Chromosome original = this.population.getIndividuals().get(mutantIndex);

			Chromosome mutation = original.clone();

			mutation.mutateRandomGene();

			fitnessEvaluator.evaluate(mutation);

			if (mutation.getFitness() > original.getFitness()) {
				this.population.removeIndividual(mutantIndex);
				this.population.addIndividual(mutation);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#spawnInitialPopulation
	 * ()
	 */
	@Override
	public void spawnInitialPopulation() {
		this.population.populateIndividuals(populationSize);

		this.population.evaluateFitness();
	}

	/**
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	@Required
	public void setPopulation(Population population) {
		this.population = population;
	}

	/**
	 * @param populationSize
	 */
	@Required
	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * This is a percentage chance that individuals will be selected for
	 * mutation.
	 * 
	 * @param mutationRate
	 *            the mutationRate to set
	 */
	@Required
	public void setMutationRate(Double mutationRate) {
		this.mutationRate = mutationRate;
	}

	/**
	 * This is a percentage of the population to be selected for crossover in
	 * pairs.
	 * 
	 * @param crossoverRate
	 *            the crossoverRate to set
	 */
	@Required
	public void setCrossoverRate(Double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	@Required
	public void setMaxGenerations(Integer maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	@Required
	public void setCrossoverAlgorithm(CrossoverAlgorithm crossoverAlgorithm) {
		this.crossoverAlgorithm = crossoverAlgorithm;
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
	 * This is the percentage of individuals that survive each generation.
	 * 
	 * @param survivalRate
	 *            the survivalRate to set
	 */
	@Required
	public void setSurvivalRate(Double survivalRate) {
		this.survivalRate = survivalRate;
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
	 * @param finalSurvivorCount
	 *            the finalSurvivorCount to set
	 */
	@Required
	public void setFinalSurvivorCount(Integer finalSurvivorCount) {
		this.finalSurvivorCount = finalSurvivorCount;
	}
}
