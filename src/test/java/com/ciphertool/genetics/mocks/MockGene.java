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

package com.ciphertool.genetics.mocks;

import java.util.ArrayList;
import java.util.List;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Sequence;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class MockGene implements VariableLengthGene {

	private Chromosome		chromosome;
	private List<Sequence>	sequences	= new ArrayList<Sequence>();

	private boolean			hasMatch;

	@Override
	public int size() {
		return this.sequences.size();
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
		return this.sequences;
	}

	@Override
	public void addSequence(Sequence sequence) {
		sequence.setGene(this);
		sequences.add(sequence);
	}

	@Override
	public void insertSequence(int index, Sequence sequence) {
		sequence.setGene(this);
		sequences.add(index, sequence);
	}

	@Override
	public void removeSequence(Sequence sequence) {
		sequences.remove(sequence);
	}

	@Override
	public void replaceSequence(int index, Sequence newSequence) {
		this.removeSequence(sequences.get(index));
		this.insertSequence(index, newSequence);
	}

	@Override
	public MockGene clone() {
		MockGene clone;

		try {
			clone = (MockGene) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		// We intentionally do not override the cloning of the Chromosome
		clone.sequences = new ArrayList<Sequence>();

		for (Sequence sequence : this.sequences) {
			clone.addSequence(sequence.clone());
		}

		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MockGene other = (MockGene) obj;
		if (sequences == null) {
			if (other.sequences != null) {
				return false;
			}
		} else if (!sequences.equals(other.sequences)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MockGene [sequences=" + sequences + "]";
	}

	@Override
	public boolean hasMatch() {
		return this.hasMatch;
	}

	@Override
	public void setHasMatch(boolean hasMatch) {
		this.hasMatch = hasMatch;
	}
}
