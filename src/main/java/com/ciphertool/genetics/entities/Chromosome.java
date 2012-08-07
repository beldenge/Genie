package com.ciphertool.genetics.entities;

import java.util.List;

public interface Chromosome extends Cloneable {

	public List<Gene> getGenes();

	/**
	 * @param genes
	 */
	public void setGenes(List<Gene> genes);

	/**
	 * Adds a Gene to the end of the Chromosome.
	 * 
	 * @param gene
	 */
	public void addGene(Gene gene);

	/**
	 * Inserts a Gene at the specified index. Care must be taken to update any
	 * Sequence indexes which follow the inserted Gene.
	 * 
	 * @param index
	 * @param gene
	 */
	public void insertGene(int index, Gene gene);

	/**
	 * Removes a Gene at the specified index. Care must be taken to update any
	 * Sequence indexes which follow the removed Gene.
	 * 
	 * @param index
	 * @return
	 */
	public Gene removeGene(int index);

	/**
	 * Replaces a Gene at the specified index. Care must be taken to update any
	 * Sequence indexes which follow the inserted Gene in case there are a
	 * different number of Sequences.
	 * 
	 * @param index
	 * @param newGene
	 */
	public void replaceGene(int index, Gene newGene);

	/**
	 * @return
	 */
	public Double getFitness();

	/**
	 * @param fitness
	 */
	public void setFitness(Double fitness);

	/*
	 * Returns the size as the number of gene sequences
	 */
	public Integer actualSize();

	public Integer targetSize();

	public Chromosome clone();

	/**
	 * Mutates a Gene at the specified index.
	 * 
	 * @param index
	 */
	public void mutateGene(int index);

	/*
	 * Mutates a random Gene
	 */
	public void mutateRandomGene();
}
