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

package com.ciphertool.genetics.mocks;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class MockFitnessEvaluator implements FitnessEvaluator {
	@SuppressWarnings("unused")
	private Object geneticStructure;

	private static final double DEFAULT_FITNESS_VALUE = 100.0;

	@Override
	public Double evaluate(Chromosome chromosome) {
		chromosome.setFitness(DEFAULT_FITNESS_VALUE);

		return DEFAULT_FITNESS_VALUE;
	}

	@Override
	public void setGeneticStructure(Object geneticStructure) {
		this.geneticStructure = geneticStructure;
	}
}
