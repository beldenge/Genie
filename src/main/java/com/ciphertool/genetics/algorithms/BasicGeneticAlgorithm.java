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

		stopRequested = false;

		if (this.population == null) {
			log.info("Attempted to start algorithm with a null population.  Spawning population of size "
					+ strategy.getPopulationSize() + ".");

			this.spawnInitialPopulation();
		}

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

			long startCrossover = System.currentTimeMillis();
			crossover();
			if (log.isDebugEnabled()) {
				log.debug("Crossover took " + (System.currentTimeMillis() - startCrossover) + "ms.");
			}

			long startMutation = System.currentTimeMillis();
			mutate();
			if (log.isDebugEnabled()) {
				log.debug("Mutation took " + (System.currentTimeMillis() - startMutation) + "ms.");
			}

			long startAging = System.currentTimeMillis();
			population.increaseAge();
			if (log.isDebugEnabled()) {
				log.debug("Aging took " + (System.currentTimeMillis() - startAging) + "ms.");
			}

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

		if (strategy.getPopulationSize() == 0) {
			validationErrors.add("populationSize");
		}

		if (strategy.getMaxGenerations() == 0) {
			validationErrors.add("maxGenerations");
		}

		if (strategy.getSurvivalRate() == 0) {
			validationErrors.add("survivalRate");
		}

		if (strategy.getMutationRate() == 0) {
			validationErrors.add("mutationRate");
		}

		if (strategy.getCrossoverRate() == 0) {
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
	public void crossover() {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return;
		}

		Chromosome mom = null;
		Chromosome dad = null;
		List<Chromosome> children = null;
		int momIndex = -1;
		int dadIndex = -1;

		List<Chromosome> moms = new ArrayList<Chromosome>();
		List<Chromosome> dads = new ArrayList<Chromosome>();

		int initialPopulationSize = this.population.size();

		/*
		 * We have to round down to protect against
		 * ArrayIndexOutOfBoundsException in edge cases
		 */
		long pairsToCrossover = (long) ((initialPopulationSize * strategy.getCrossoverRate()) / 2);

		log.debug("Pairs to crossover: " + pairsToCrossover);

		List<Chromosome> childrenToAdd = new ArrayList<Chromosome>();
		for (int i = 0; i < pairsToCrossover; i++) {
			momIndex = this.population.spinIndexRouletteWheel();
			mom = this.population.getIndividuals().get(momIndex);
			moms.add(mom);

			/*
			 * Remove mom from the population to prevent parents from
			 * reproducing more than one time per generation.
			 */
			this.population.removeIndividual(momIndex);

			dadIndex = this.population.spinIndexRouletteWheel();
			dad = this.population.getIndividuals().get(dadIndex);
			dads.add(dad);

			/*
			 * Remove dad from the population to prevent parents from
			 * reproducing more than one time per generation.
			 */
			this.population.removeIndividual(dadIndex);

			children = crossoverAlgorithm.crossover(mom, dad);

			/*
			 * Add children after all crossover operations are completed so that
			 * children are not inadvertently breeding immediately after birth.
			 */
			childrenToAdd.addAll(children);
		}

		for (Chromosome child : childrenToAdd) {
			this.population.addIndividual(child);
		}

		/*
		 * Re-add all the parents since we wanted them removed from the roulette
		 * pool temporarily, but they shouldn't die off immediately after
		 * producing children.
		 */
		for (Chromosome parent : moms) {
			this.population.addIndividual(parent);
		}
		for (Chromosome parent : dads) {
			this.population.addIndividual(parent);
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

		long mutations = Math.round((initialPopulationSize * strategy.getMutationRate()));

		log.debug("Mutations to perform: " + mutations);

		List<Chromosome> mutatedChromosomes = new ArrayList<Chromosome>();

		for (int i = 0; i < mutations; i++) {
			mutantIndex = this.population.spinIndexRouletteWheel();
			Chromosome mutatedChromosome = this.population.getIndividuals().get(mutantIndex);

			/*
			 * Remove the Chromosome from the population temporarily so that it
			 * is not re-selected by the next spin of the roulette wheel. Add it
			 * to a List to be re-added after all mutations are complete.
			 */
			this.population.removeIndividual(mutantIndex);
			mutatedChromosomes.add(mutatedChromosome);

			/*
			 * Mutate a gene within the Chromosome. The original Chromosome is
			 * not cloned. Natural selection will weed this out if it is
			 * unfavorable.
			 */
			mutationAlgorithm.mutateChromosome(mutatedChromosome);
		}

		/*
		 * Re-add the original (now mutated) Chromosomes
		 */
		for (Chromosome chromosome : mutatedChromosomes) {
			this.population.addIndividual(chromosome);
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
