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

package com.ciphertool.genetics.entities;

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
