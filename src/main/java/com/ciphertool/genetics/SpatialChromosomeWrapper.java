/**
 * Copyright 2015 George Belden
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

package com.ciphertool.genetics;

import com.ciphertool.genetics.entities.Chromosome;

public class SpatialChromosomeWrapper {
	private int xPos;
	private int yPos;

	private Chromosome chromosome;

	/**
	 * @param xPos
	 * @param yPos
	 * @param chromosome
	 */
	public SpatialChromosomeWrapper(int xPos, int yPos, Chromosome chromosome) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.chromosome = chromosome;
	}

	/**
	 * @return the xPos
	 */
	public int getXPos() {
		return xPos;
	}

	/**
	 * @return the yPos
	 */
	public int getYPos() {
		return yPos;
	}

	/**
	 * @return the chromosome
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}
}
