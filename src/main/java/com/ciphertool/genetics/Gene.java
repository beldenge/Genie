package com.ciphertool.genetics;

import java.util.List;

public interface Gene extends Cloneable {

	/*
	 * Return the size of this Gene, measured as the number of sequences making
	 * up this Gene.
	 */
	public int size();

	public Gene clone();

	/**
	 * Sets the Chromosome that this Gene is a part of.
	 * 
	 * @param chromosome
	 */
	public void setChromosome(Chromosome chromosome);

	/**
	 * Returns the Chromosome that this Gene is a part of.
	 * 
	 * @return
	 */
	public Chromosome getChromosome();

	public List<Sequence> getSequences();

	/**
	 * @param sequences
	 */
	public void setSequences(List<Sequence> sequences);

	/**
	 * Adds a Sequence to the end of the Gene.
	 * 
	 * @param sequence
	 */
	public void addSequence(Sequence sequence);

	/**
	 * Inserts a Sequence at the specified index. Care must be taken to update
	 * any Sequence indexes which follow the inserted Sequence.
	 * 
	 * @param index
	 * @param sequence
	 */
	public void insertSequence(int index, Sequence sequence);

	/**
	 * Removes a Sequence at the specified index. Care must be taken to update
	 * any Sequence indexes which follow the removed Sequence.
	 * 
	 * @param sequence
	 */
	public void removeSequence(Sequence sequence);

	/**
	 * Replaces a Sequence at the specified index.
	 * 
	 * @param index
	 * @param newGene
	 */
	public void replaceSequence(int index, Sequence newSequence);

	/**
	 * Mutates a Sequence at the specified index.
	 * 
	 * @param index
	 */
	public void mutateSequence(int index);

	/*
	 * Mutate a random sequence
	 */
	public void mutateRandomSequence();
}
