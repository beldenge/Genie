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

public class MockGene implements Gene {

	private Chromosome chromosome;

	@Override
	public int size() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}

	@Override
	public Chromosome getChromosome() {
		return this.chromosome;
	}

	@Override
	public List<Sequence> getSequences() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void resetSequences() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void addSequence(Sequence sequence) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void insertSequence(int index, Sequence sequence) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void removeSequence(Sequence sequence) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void replaceSequence(int index, Sequence newSequence) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public MockGene clone() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}
}
