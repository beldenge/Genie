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

package com.ciphertool.genetics.mocks;

import java.util.ArrayList;
import java.util.List;

import com.ciphertool.genetics.annotations.Clean;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class MockKeylessChromosome implements KeylessChromosome {
	private boolean needsEvaluation;
	private Double fitness = 0.0;
	private List<Gene> genes = new ArrayList<Gene>();
	private Integer targetSize = 0;
	private int age = 0;
	private int numberOfChildren = 0;

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
		gene.setChromosome(this);
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
		return this.numberOfChildren;
	}

	@Override
	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	@Override
	public void increaseNumberOfChildren() {
		this.numberOfChildren++;
	}

	@Override
	public Integer actualSize() {
		int size = 0;

		for (Gene gene : this.genes) {
			size += ((VariableLengthGene) gene).size();
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
	public MockKeylessChromosome clone() {
		MockKeylessChromosome clone;

		try {
			clone = (MockKeylessChromosome) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		clone.setFitness(this.fitness.doubleValue());
		clone.setTargetSize(this.targetSize.intValue());

		clone.genes = new ArrayList<Gene>();

		for (Gene gene : this.genes) {
			clone.addGene(gene.clone());
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
		MockKeylessChromosome other = (MockKeylessChromosome) obj;
		if (genes == null) {
			if (other.genes != null) {
				return false;
			}
		} else if (!genes.equals(other.genes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MockKeylessChromosome [needsEvaluation=" + needsEvaluation + ", fitness=" + fitness + ", genes="
				+ genes + "]";
	}
}
