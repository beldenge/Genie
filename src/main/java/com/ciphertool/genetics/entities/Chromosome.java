/**
 * Copyright 2013 George Belden
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

public interface Chromosome extends Cloneable {

	/**
	 * @return an unmodifiable List of this Chromosome's Genes
	 */
	public List<Gene> getGenes();

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
	 * Re-initializes the Collection of Genes.
	 */
	public void resetGenes();

	/**
	 * @return
	 */
	public Double getFitness();

	/**
	 * @param fitness
	 */
	public void setFitness(Double fitness);

	/**
	 * @return the age of this individual Chromosome
	 */
	public int getAge();

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age);

	public void increaseAge();

	/**
	 * @return the number of children this Chromosome has procreated
	 */
	public int getNumberOfChildren();

	/**
	 * @param numberOfChildren
	 *            the numberOfChildren to set
	 */
	public void setNumberOfChildren(int numberOfChildren);

	public void increaseNumberOfChildren();

	/*
	 * Returns the size as the number of gene sequences
	 */
	public Integer actualSize();

	public Integer targetSize();

	public Chromosome clone();

	/*
	 * Whether this Chromosome has changed since it was last evaluated.
	 */
	public boolean isEvaluationNeeded();

	/**
	 * @param evaluationNeeded
	 *            the evaluationNeeded value to set
	 */
	public void setEvaluationNeeded(boolean evaluationNeeded);
}
