package com.ciphertool.genetics.algorithms;

import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.entities.Chromosome;

public interface GeneticAlgorithm {

	public Chromosome iterateUntilTermination();

	public void spawnInitialPopulation();

	public void crossover();

	public void mutate();

	public Population getPopulation();

	public void select();
}
