package com.ciphertool.genetics;

public interface GeneticAlgorithm {

	public Chromosome iterateUntilTermination();

	public void spawnInitialPopulation();

	public void crossover();

	public void mutate();

	public Population getPopulation();

	public void select();
}
