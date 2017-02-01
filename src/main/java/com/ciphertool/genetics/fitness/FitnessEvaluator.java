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

package com.ciphertool.genetics.fitness;

import java.math.BigDecimal;

import com.ciphertool.genetics.Selectable;
import com.ciphertool.genetics.entities.Chromosome;

public interface FitnessEvaluator extends Selectable {
	public BigDecimal evaluate(Chromosome chromosome);

	/**
	 * The source structure against which this genetic algorithm should evaluate chromosomes.
	 * 
	 * @param obj
	 *            the Object
	 */
	public void setGeneticStructure(Object obj);
}
