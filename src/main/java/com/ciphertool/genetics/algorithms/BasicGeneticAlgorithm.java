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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.dao.ExecutionStatisticsDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.ExecutionStatistics;
import com.ciphertool.genetics.entities.GenerationStatistics;

public class BasicGeneticAlgorithm implements GeneticAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	protected GeneticAlgorithmStrategy strategy;
	protected Population population;
	protected CrossoverAlgorithm crossoverAlgorithm;
	protected MutationAlgorithm mutationAlgorithm;
	protected SelectionAlgorithm selectionAlgorithm;
	private boolean stopRequested;
	private ExecutionStatisticsDao executionStatisticsDao;

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
	public void evolve() {
		List<String> validationErrors = validateParameters();
		if (validationErrors.size() > 0) {
			log.warn("Unable to execute genetic algorithm because one or more of the required parameters are missing.  The fields that failed validation are "
					+ validationErrors);

			return;
		}

		long start = System.currentTimeMillis();

		this.spawnInitialPopulation();

		log.info("Took " + (System.currentTimeMillis() - start)
				+ "ms to spawn initial population of size " + this.population.size());

		stopRequested = false;

		Date startDate = new Date();
		ExecutionStatistics executionStatistics = new ExecutionStatistics(startDate, strategy);
		long genesis = System.currentTimeMillis();
		long generationStart = 0;
		int i = 0;
		long executionTime = 0;
		GenerationStatistics generationStatistics = null;
		do {
			i++;
			generationStart = System.currentTimeMillis();

			/*
			 * Doing the select first improves performance by a ratio of up to
			 * (1 - survivalRate). It makes more sense as well since only
			 * survivors can reproduce.
			 */
			long startSelect = System.currentTimeMillis();
			select();
			if (log.isDebugEnabled()) {
				log.debug("Selection took " + (System.currentTimeMillis() - startSelect) + "ms.");
			}

			/*
			 * We want to increase age before children are produced in following
			 * steps so that the children do not age before having a chance to
			 * do anything.
			 */
			long startAging = System.currentTimeMillis();
			population.increaseAge();
			if (log.isDebugEnabled()) {
				log.debug("Aging took " + (System.currentTimeMillis() - startAging) + "ms.");
			}

			int populationSizeBeforeReproduction = this.population.size();

			long startCrossover = System.currentTimeMillis();
			crossover(populationSizeBeforeReproduction);
			if (log.isDebugEnabled()) {
				log.debug("Crossover took " + (System.currentTimeMillis() - startCrossover) + "ms.");
			}

			long startMutation = System.currentTimeMillis();
			mutate(populationSizeBeforeReproduction);
			if (log.isDebugEnabled()) {
				log.debug("Mutation took " + (System.currentTimeMillis() - startMutation) + "ms.");
			}

			population.resetEligibility();

			/*
			 * Adds new random solutions to the population to fill back to the
			 * population size. Ultimately, this should not be necessary, but it
			 * is here as a failsafe in crossover and mutation do not produce
			 * enough children.
			 */
			long startBreeding = System.currentTimeMillis();
			population.breed(strategy.getPopulationSize());
			if (log.isDebugEnabled()) {
				log.debug("Breeding took " + (System.currentTimeMillis() - startBreeding) + "ms.");
			}

			long startEvaluation = System.currentTimeMillis();
			generationStatistics = new GenerationStatistics(executionStatistics, i);
			population.evaluateFitness(generationStatistics);
			if (log.isDebugEnabled()) {
				log.debug("Evaluation took " + (System.currentTimeMillis() - startEvaluation)
						+ "ms.");
			}

			executionTime = (System.currentTimeMillis() - generationStart);
			generationStatistics.setExecutionTime(executionTime);

			log.info(generationStatistics);

			executionStatistics.addGenerationStatistics(generationStatistics);
		} while (!stopRequested
				&& (strategy.getMaxGenerations() < 0 || i < strategy.getMaxGenerations()));

		log.info("Average generation time is " + ((System.currentTimeMillis() - genesis) / i)
				+ "ms.");

		Date endDate = new Date();
		executionStatistics.setEndDateTime(endDate);

		persistStatistics(executionStatistics);
	}

	private List<String> validateParameters() {
		List<String> validationErrors = new ArrayList<String>();

		if (strategy.getGeneticStructure() == null) {
			validationErrors.add("geneticStructure");
		}

		if (strategy.getPopulationSize() == null || strategy.getPopulationSize() <= 0) {
			validationErrors.add("populationSize");
		}

		if (strategy.getMaxGenerations() == null || strategy.getMaxGenerations() == 0) {
			validationErrors.add("maxGenerations");
		}

		if (strategy.getSurvivalRate() == 0) {
			validationErrors.add("survivalRate");
		}

		if (strategy.getMutationRate() == null || strategy.getMutationRate() < 0) {
			validationErrors.add("mutationRate");
		}

		if (strategy.getCrossoverRate() == null || strategy.getCrossoverRate() < 0) {
			validationErrors.add("crossoverRate");
		}

		if (strategy.getFitnessEvaluator() == null) {
			validationErrors.add("fitnessEvaluator");
		}

		if (strategy.getCrossoverAlgorithm() == null) {
			validationErrors.add("crossoverAlgorithm");
		}

		return validationErrors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.genetics.algorithms.GeneticAlgorithm#select()
	 */
	@Override
	public void select() {
		selectionAlgorithm.select(this.population, this.strategy.getPopulationSize(), this.strategy
				.getSurvivalRate());
	}

	/*
	 * Crossover algorithm utilizing Roulette Wheel Selection
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#crossover()
	 */
	@Override
	public void crossover(int initialPopulationSize) {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return;
		}

		Chromosome mom = null;
		Chromosome dad = null;
		List<Chromosome> children = null;
		int momIndex = -1;
		int dadIndex = -1;

		/*
		 * We have to round down to protect against
		 * ArrayIndexOutOfBoundsException in edge cases. Also choose the minimum
		 * between the current population size and the calculated number of
		 * pairs in case there are not enough eligible individuals.
		 */
		long pairsToCrossover = Math.min((long) (initialPopulationSize * strategy
				.getCrossoverRate()), ((long) (this.population.size() / 2)));

		log.debug("Pairs to crossover: " + pairsToCrossover);

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();
		for (int i = 0; i < pairsToCrossover; i++) {
			momIndex = this.population.selectIndex();
			mom = this.population.getIndividuals().get(momIndex);

			/*
			 * Remove mom from the population to prevent parents from
			 * reproducing more than one time per generation.
			 */
			this.population.makeIneligibleForReproduction(momIndex);

			dadIndex = this.population.selectIndex();
			dad = this.population.getIndividuals().get(dadIndex);

			/*
			 * Remove dad from the population to prevent parents from
			 * reproducing more than one time per generation.
			 */
			this.population.makeIneligibleForReproduction(dadIndex);

			children = crossoverAlgorithm.crossover(mom, dad);

			/*
			 * Add children after all crossover operations are completed so that
			 * children are not inadvertently breeding immediately after birth.
			 */
			childrenToAdd.addAll(children);
		}

		for (Chromosome child : childrenToAdd) {
			this.population.addIndividualAsIneligible(child);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#mutate()
	 */
	@Override
	public void mutate(int initialPopulationSize) {
		int mutantIndex = -1;

		/*
		 * Set the number of mutations to perform to be the lesser of the
		 * current eligible population size and the total initial population
		 * size (before any other operations made individuals ineligible for
		 * reproduction) multiplied by the configured mutation rate.
		 */
		long mutations = Math.min(Math.round((initialPopulationSize * strategy.getMutationRate())),
				this.population.size());

		log.debug("Chromosomes to mutate: " + mutations);

		List<Chromosome> children = new ArrayList<Chromosome>();

		for (int i = 0; i < mutations; i++) {
			mutantIndex = this.population.selectIndex();
			Chromosome chromosomeToMutate = this.population.getIndividuals().get(mutantIndex)
					.clone();

			/*
			 * Remove the Chromosome from the population temporarily so that it
			 * is not re-selected by the next spin of the roulette wheel. Add it
			 * to a List to be re-added after all mutations are complete.
			 */
			this.population.makeIneligibleForReproduction(mutantIndex);

			/*
			 * Mutate a gene within the Chromosome. The original Chromosome has
			 * been cloned.
			 */
			mutationAlgorithm.mutateChromosome(chromosomeToMutate);
			children.add(chromosomeToMutate);
		}

		/*
		 * Re-add the original (now mutated) Chromosomes
		 */
		for (Chromosome chromosome : children) {
			this.population.addIndividualAsIneligible(chromosome);
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
		this.population.clearIndividuals();

		this.population.breed(strategy.getPopulationSize());

		this.population.evaluateFitness(null);
	}

	/**
	 * @param executionStatistics
	 *            the ExecutionStatistics to persist
	 */
	private void persistStatistics(final ExecutionStatistics executionStatistics) {
		log.info("Persisting statistics to database.");
		long startInsert = System.currentTimeMillis();
		executionStatisticsDao.insert(executionStatistics);
		log.info("Took " + (System.currentTimeMillis() - startInsert)
				+ "ms to persist statistics to database.");
	}

	@Override
	public void requestStop() {
		this.stopRequested = true;
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
	@Override
	public void setPopulation(Population population) {
		this.population = population;
	}

	/**
	 * @return the strategy
	 */
	public GeneticAlgorithmStrategy getStrategy() {
		return strategy;
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
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Required
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param selectionAlgorithm
	 *            the selectionAlgorithm to set
	 */
	@Required
	public void setSelectionAlgorithm(SelectionAlgorithm selectionAlgorithm) {
		this.selectionAlgorithm = selectionAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.genetics.algorithms.GeneticAlgorithm#setParameters(int,
	 * int, double, double, double)
	 */
	@Override
	public void setStrategy(GeneticAlgorithmStrategy geneticAlgorithmStrategy) {
		this.population.setGeneticStructure(geneticAlgorithmStrategy.getGeneticStructure());
		this.population.setFitnessEvaluator(geneticAlgorithmStrategy.getFitnessEvaluator());
		this.population.setLifespan(geneticAlgorithmStrategy.getLifespan());
		this.population.setKnownSolutionFitnessEvaluator(geneticAlgorithmStrategy
				.getKnownSolutionFitnessEvaluator());
		this.population.setCompareToKnownSolution(geneticAlgorithmStrategy
				.getCompareToKnownSolution());

		this.crossoverAlgorithm = geneticAlgorithmStrategy.getCrossoverAlgorithm();
		this.crossoverAlgorithm.setMutationAlgorithm(geneticAlgorithmStrategy
				.getMutationAlgorithm());

		this.mutationAlgorithm = geneticAlgorithmStrategy.getMutationAlgorithm();

		this.selectionAlgorithm = geneticAlgorithmStrategy.getSelectionAlgorithm();

		this.strategy = geneticAlgorithmStrategy;
	}

	/**
	 * @param executionStatisticsDao
	 *            the executionStatisticsDao to set
	 */
	@Required
	public void setExecutionStatisticsDao(ExecutionStatisticsDao executionStatisticsDao) {
		this.executionStatisticsDao = executionStatisticsDao;
	}
}
