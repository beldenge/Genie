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

package com.ciphertool.genetics.entities;

import org.springframework.data.annotation.Transient;

import com.ciphertool.genetics.population.Population;

public interface Chromosome extends Cloneable {
	public String getId();

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

	/**
	 * @return the solutionSetId
	 */
	public Integer getSolutionSetId();

	/**
	 * @param solutionSetId
	 *            the solutionSetId to set
	 */
	public void setSolutionSetId(Integer solutionSetId);

	/**
	 * @param other
	 *            the other Chromosome
	 * @return the percentage similarity between this Chromosome and other
	 */
	public double similarityTo(Chromosome other);

	/**
	 * @return this Chromosome's Population
	 */
	@Transient
	public Population getPopulation();

	/**
	 * @param population
	 *            the population to set
	 */
	public void setPopulation(Population population);

	/**
	 * @return the ancestry
	 */
	@Transient
	public Ancestry getAncestry();

	/**
	 * @param ancestry
	 *            the ancestry to set
	 */
	public void setAncestry(Ancestry ancestry);
}
