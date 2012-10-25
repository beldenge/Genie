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

import java.util.List;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.entities.Chromosome;

public interface GeneticAlgorithm {

	public void iterateUntilTermination();

	public List<Chromosome> getBestFitIndividuals();

	public void spawnInitialPopulation();

	public void crossover();

	public void mutate();

	public Population getPopulation();

	public void select();

	public void requestStop();

	/**
	 * @param populationSize
	 *            the population size
	 * @param numGenerations
	 *            the number of generations to iterate. A negative number is
	 *            interpreted as infinity.
	 * @param survivalRate
	 *            the percentage of individuals that survive each generation.
	 * @param mutationRate
	 *            This is a percentage chance that individuals will be selected
	 *            for mutation.
	 * @param crossoverRate
	 *            This is a percentage of the population to be selected for
	 *            crossover in pairs.
	 */
	public void setParameters(int populationSize, int numGenerations, double survivalRate,
			double mutationRate, double crossoverRate);

	/**
	 * @param populationSize
	 *            the population size
	 */
	public void setPopulationSize(int populationSize);

	/**
	 * @param numGenerations
	 *            the number of generations to iterate. A negative number is
	 *            interpreted as infinity.
	 */
	public void setNumGenerations(int numGenerations);

	/**
	 * @param survivalRate
	 *            the percentage of individuals that survive each generation.
	 */
	/**
	 * @param survivalRate
	 */
	public void setSurvivalRate(double survivalRate);

	/**
	 * @param mutationRate
	 *            This is a percentage chance that individuals will be selected
	 *            for mutation.
	 */
	public void setMutationRate(double mutationRate);

	/**
	 * @param crossoverRate
	 *            This is a percentage of the population to be selected for
	 *            crossover in pairs.
	 */
	public void setCrossoverRate(double crossoverRate);
}
