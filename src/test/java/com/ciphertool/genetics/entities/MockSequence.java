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

public class MockSequence implements Sequence {

	private Gene gene;

	@Override
	public Integer getSequenceId() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public Gene getGene() {
		return this.gene;
	}

	@Override
	public void setGene(Gene gene) {
		this.gene = gene;
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void setValue(Object obj) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public MockSequence clone() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}
}
