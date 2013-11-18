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
	private Double fitness = 0.0;
	private List<Gene> genes = new ArrayList<Gene>();
	private Integer targetSize = 25;
	private int age = 0;

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
		this.genes.add(index, gene);
	}

	@Override
	public Gene removeGene(int index) {
		return this.genes.remove(index);
	}

	@Override
	public void replaceGene(int index, Gene newGene) {
		this.removeGene(index);
		this.insertGene(index, newGene);
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
		return this.age;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public void increaseAge() {
		this.age++;
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
		return targetSize;
	}

	/**
	 * Convenience method for unit tests. This will not normally be implemented
	 * this way.
	 * 
	 * @param targetSize
	 *            the targetSize to set
	 */
	public void setTargetSize(int targetSize) {
		this.targetSize = targetSize;
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
		try {
			return (MockChromosome) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "MockChromosome [needsEvaluation=" + needsEvaluation + ", fitness=" + fitness
				+ ", genes=" + genes + "]";
	}
}
