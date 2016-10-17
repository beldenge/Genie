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

package com.ciphertool.genetics.entities.statistics;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "generationStats")
public class GenerationStatistics implements Serializable {
	private static final long	serialVersionUID	= 5751129649317222013L;

	@Id
	private String				id;

	@DBRef
	private ExecutionStatistics	executionStatistics;

	private int					generation;

	private long				executionTime;

	private double				bestFitness;

	private double				averageFitness;

	private double				entropy;

	private Double				knownSolutionProximity;

	private int					numberOfMutations;

	private int					numberOfCrossovers;

	private int					numberRandomlyGenerated;

	private int					numberSelectedOut;

	/**
	 * Default no-args constructor
	 */
	public GenerationStatistics() {
	}

	/**
	 * @param executionStatistics
	 *            the executionStatistics to set
	 * @param generation
	 *            the generation to set
	 */
	public GenerationStatistics(ExecutionStatistics executionStatistics, int generation) {
		this.executionStatistics = executionStatistics;
		this.generation = generation;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the executionStatistics
	 */
	public ExecutionStatistics getExecutionStatistics() {
		return executionStatistics;
	}

	/**
	 * @param executionStatistics
	 *            the executionStatistics to set
	 */
	public void setExecutionStatistics(ExecutionStatistics executionStatistics) {
		this.executionStatistics = executionStatistics;
	}

	/**
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * @param generation
	 *            the generation to set
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}

	/**
	 * @param executionTime
	 *            the executionTime to set
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * @return the bestFitness
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * @param bestFitness
	 *            the bestFitness to set
	 */
	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	/**
	 * @return the averageFitness
	 */
	public double getAverageFitness() {
		return averageFitness;
	}

	/**
	 * @param averageFitness
	 *            the averageFitness to set
	 */
	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

	/**
	 * @return the entropy
	 */
	public double getEntropy() {
		return entropy;
	}

	/**
	 * @param entropy
	 *            the entropy to set
	 */
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	/**
	 * @return the knownSolutionProximity
	 */
	public Double getKnownSolutionProximity() {
		return knownSolutionProximity;
	}

	/**
	 * @param knownSolutionProximity
	 *            the knownSolutionProximity to set
	 */
	public void setKnownSolutionProximity(Double knownSolutionProximity) {
		this.knownSolutionProximity = knownSolutionProximity;
	}

	/**
	 * @return the numberOfMutations
	 */
	protected int getNumberOfMutations() {
		return numberOfMutations;
	}

	/**
	 * @param numberOfMutations
	 *            the numberOfMutations to set
	 */
	public void setNumberOfMutations(int numberOfMutations) {
		this.numberOfMutations = numberOfMutations;
	}

	/**
	 * @return the numberOfCrossovers
	 */
	protected int getNumberOfCrossovers() {
		return numberOfCrossovers;
	}

	/**
	 * @param numberOfCrossovers
	 *            the numberOfCrossovers to set
	 */
	public void setNumberOfCrossovers(int numberOfCrossovers) {
		this.numberOfCrossovers = numberOfCrossovers;
	}

	/**
	 * @return the numberRandomlyGenerated
	 */
	protected int getNumberRandomlyGenerated() {
		return numberRandomlyGenerated;
	}

	/**
	 * @param numberRandomlyGenerated
	 *            the numberRandomlyGenerated to set
	 */
	public void setNumberRandomlyGenerated(int numberRandomlyGenerated) {
		this.numberRandomlyGenerated = numberRandomlyGenerated;
	}

	/**
	 * @return the numberSelectedOut
	 */
	protected int getNumberSelectedOut() {
		return numberSelectedOut;
	}

	/**
	 * @param numberSelectedOut
	 *            the numberSelectedOut to set
	 */
	public void setNumberSelectedOut(int numberSelectedOut) {
		this.numberSelectedOut = numberSelectedOut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(averageFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(bestFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(entropy);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((executionStatistics == null) ? 0 : executionStatistics.hashCode());
		result = prime * result + (int) (executionTime ^ (executionTime >>> 32));
		result = prime * result + generation;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((knownSolutionProximity == null) ? 0 : knownSolutionProximity.hashCode());
		result = prime * result + numberOfCrossovers;
		result = prime * result + numberOfMutations;
		result = prime * result + numberRandomlyGenerated;
		result = prime * result + numberSelectedOut;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GenerationStatistics)) {
			return false;
		}
		GenerationStatistics other = (GenerationStatistics) obj;
		if (Double.doubleToLongBits(averageFitness) != Double.doubleToLongBits(other.averageFitness)) {
			return false;
		}
		if (Double.doubleToLongBits(bestFitness) != Double.doubleToLongBits(other.bestFitness)) {
			return false;
		}
		if (Double.doubleToLongBits(entropy) != Double.doubleToLongBits(other.entropy)) {
			return false;
		}
		if (executionStatistics == null) {
			if (other.executionStatistics != null) {
				return false;
			}
		} else if (!executionStatistics.equals(other.executionStatistics)) {
			return false;
		}
		if (executionTime != other.executionTime) {
			return false;
		}
		if (generation != other.generation) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (knownSolutionProximity == null) {
			if (other.knownSolutionProximity != null) {
				return false;
			}
		} else if (!knownSolutionProximity.equals(other.knownSolutionProximity)) {
			return false;
		}
		if (numberOfCrossovers != other.numberOfCrossovers) {
			return false;
		}
		if (numberOfMutations != other.numberOfMutations) {
			return false;
		}
		if (numberRandomlyGenerated != other.numberRandomlyGenerated) {
			return false;
		}
		if (numberSelectedOut != other.numberSelectedOut) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String proximity = (this.knownSolutionProximity == null) ? "" : ", proximityToKnown="
				+ String.format("%1$,.2f", this.knownSolutionProximity) + "%";

		return "[generation=" + generation + ", executionTime=" + executionTime + ", averageFitness="
				+ String.format("%1$,.2f", averageFitness) + ", bestFitness=" + String.format("%1$,.2f", bestFitness)
				+ ", entropy=" + entropy + proximity + ", deaths=" + numberSelectedOut + ", crossovers="
				+ numberOfCrossovers + ", mutations=" + numberOfMutations + ", newSpawns=" + numberRandomlyGenerated
				+ "]";
	}
}
