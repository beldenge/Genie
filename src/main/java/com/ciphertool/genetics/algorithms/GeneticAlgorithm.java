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

package com.ciphertool.genetics.algorithms;

import java.util.List;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.population.Population;

public interface GeneticAlgorithm {
	public void evolveAutonomously() throws InterruptedException;

	public void initialize() throws InterruptedException;

	public void finish();

	public void proceedWithNextGeneration() throws InterruptedException;

	public void select(int initialPopulationSize, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException;

	public int crossover(int pairsToCrossover, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException;

	public int mutate(int populationSizeBeforeReproduction) throws InterruptedException;

	public Population getPopulation();

	/**
	 * @param population
	 *            the Population to set
	 */
	public void setPopulation(Population population);

	public void requestStop();

	/**
	 * @return the GeneticAlgorithmStrategy
	 */
	public GeneticAlgorithmStrategy getStrategy();

	/**
	 * @param geneticAlgorithmStrategy
	 *            the GeneticAlgorithmStrategy to set
	 */
	public void setStrategy(GeneticAlgorithmStrategy geneticAlgorithmStrategy);

	public void spawnInitialPopulation() throws InterruptedException;
}
