package com.ciphertool.genetics.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "generation_stats")
public class GenerationStatistics implements Serializable {
	private static final long serialVersionUID = 5751129649317222013L;

	private Integer id;
	private ExecutionStatistics executionStatistics;
	private int generation;
	private long executionTime;
	private double bestFitness;
	private double averageFitness;
	private Double knownSolutionProximity;

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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the executionStatistics
	 */
	@NaturalId
	@ManyToOne
	@JoinColumn(name = "execution_id")
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
	@NaturalId
	@Column(name = "generation")
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
	@Column(name = "execution_time")
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
	@Column(name = "best_fitness")
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
	@Column(name = "average_fitness")
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
	 * @return the knownSolutionProximity
	 */
	@Column(name = "known_solution_proximity", nullable = true)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(averageFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(bestFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((executionStatistics == null) ? 0 : executionStatistics.hashCode());
		result = prime * result + (int) (executionTime ^ (executionTime >>> 32));
		result = prime * result + generation;
		result = prime * result
				+ ((knownSolutionProximity == null) ? 0 : knownSolutionProximity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		GenerationStatistics other = (GenerationStatistics) obj;
		if (Double.doubleToLongBits(averageFitness) != Double
				.doubleToLongBits(other.averageFitness)) {
			return false;
		}
		if (Double.doubleToLongBits(bestFitness) != Double.doubleToLongBits(other.bestFitness)) {
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
		if (knownSolutionProximity == null) {
			if (other.knownSolutionProximity != null) {
				return false;
			}
		} else if (!knownSolutionProximity.equals(other.knownSolutionProximity)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String proximity = (this.knownSolutionProximity == null) ? ""
				: ", with proximity to known solution of "
						+ String.format("%1$,.2f", this.knownSolutionProximity) + "%";

		return "Generation " + generation + " finished in " + executionTime
				+ "ms with an average fitness of " + String.format("%1$,.2f", averageFitness)
				+ " and best fitness of " + String.format("%1$,.2f", bestFitness) + proximity;
	}
}
