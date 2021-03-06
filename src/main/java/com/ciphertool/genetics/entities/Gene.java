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

public interface Gene extends Cloneable {

	public Gene clone();

	/**
	 * Sets the Chromosome that this Gene is a part of.
	 * 
	 * @param chromosome
	 */
	public void setChromosome(Chromosome chromosome);

	/**
	 * @return the Chromosome that this Gene is a part of
	 */
	public Chromosome getChromosome();

	/**
	 * @return whether this Gene matches that of a known solution
	 */
	public boolean hasMatch();

	/**
	 * @param hasMatch
	 *            the hasMatch to set
	 */
	public void setHasMatch(boolean hasMatch);
}
