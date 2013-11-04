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

import java.util.ArrayList;
import java.util.List;

import com.ciphertool.genetics.annotations.Clean;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public class MockChromosome implements Chromosome {
	private boolean needsEvaluation;
	private Double fitness;
	private List<Gene> genes = new ArrayList<Gene>();
	private static final Integer TARGET_SIZE = 25;

	@Override
	public List<Gene> getGenes() {
		return this.genes;
	}

	@Override
	public void addGene(Gene gene) {
		gene.setChromosome(this);
		this.genes.add(gene);
	}

	@Override
	public void insertGene(int index, Gene gene) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public Gene removeGene(int index) {
		return this.genes.remove(index);
	}

	@Override
	public void replaceGene(int index, Gene newGene) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void resetGenes() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public Double getFitness() {
		return this.fitness;
	}

	@Override
	@Clean
	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	@Override
	public int getAge() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void setAge(int age) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void increaseAge() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public int getNumberOfChildren() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void setNumberOfChildren(int numberOfChildren) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void increaseNumberOfChildren() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public Integer actualSize() {
		int size = 0;

		for (Gene gene : this.genes) {
			size += gene.size();
		}

		return size;
	}

	@Override
	public Integer targetSize() {
		return TARGET_SIZE;
	}

	@Override
	public boolean isEvaluationNeeded() {
		return this.needsEvaluation;
	}

	@Override
	public void setEvaluationNeeded(boolean needsEvaluation) {
		this.needsEvaluation = needsEvaluation;
	}

	@Override
	public MockChromosome clone() {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public String toString() {
		return "MockChromosome [needsEvaluation=" + needsEvaluation + ", fitness=" + fitness
				+ ", genes=" + genes + "]";
	}
}
