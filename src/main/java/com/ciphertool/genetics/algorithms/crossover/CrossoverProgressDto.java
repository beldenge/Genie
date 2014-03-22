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

package com.ciphertool.genetics.algorithms.crossover;

/**
 * This class is a Data Transformation Object used to hold the index values
 * needed during crossover. This allows these values to be passed in methods and
 * incremented by reference.
 */
public class CrossoverProgressDto {
	private int childSequencePosition = 0;
	private int parentSequencePosition = 0;
	private int childGeneIndex = 0;
	private int parentGeneIndex = 0;

	public CrossoverProgressDto() {
	}

	public void advanceChildSequencePositionBy(int amountToAdvance) {
		this.childSequencePosition += amountToAdvance;
	}

	public void advanceParentSequencePositionBy(int amountToAdvance) {
		this.parentSequencePosition += amountToAdvance;
	}

	public void advanceChildGeneIndexBy(int amountToAdvance) {
		this.childGeneIndex += amountToAdvance;
	}

	public void advanceParentGeneIndexBy(int amountToAdvance) {
		this.parentGeneIndex += amountToAdvance;
	}

	/**
	 * @return the childSequencePosition
	 */
	public int getChildSequencePosition() {
		return childSequencePosition;
	}

	/**
	 * @return the parentSequencePosition
	 */
	public int getParentSequencePosition() {
		return parentSequencePosition;
	}

	/**
	 * @return the childGeneIndex
	 */
	public int getChildGeneIndex() {
		return childGeneIndex;
	}

	/**
	 * @return the parentGeneIndex
	 */
	public int getParentGeneIndex() {
		return parentGeneIndex;
	}
}