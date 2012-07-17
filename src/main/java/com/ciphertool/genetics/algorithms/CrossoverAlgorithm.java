package com.ciphertool.genetics.algorithms;

import com.ciphertool.genetics.entities.Chromosome;

public interface CrossoverAlgorithm {

	/*
	 * Performs crossover to a single child by cloning parentA and then
	 * selectively replacing Genes from parentB if they increase the fitness of
	 * the Chromosome.
	 * 
	 * Crossover from parentB to parentA can be achieved simply by reversing the
	 * supplied arguments.
	 */
	public Chromosome crossover(Chromosome parentA, Chromosome parentB);
}
