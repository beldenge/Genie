package com.ciphertool.genetics.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NaturalId;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;

@Entity
@Table(name = "execution_stats")
public class ExecutionStatistics implements Serializable {
	private static final long serialVersionUID = 8148209145996293339L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NaturalId
	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDateTime;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDateTime;

	@Column(name = "population_size")
	private Integer populationSize;

	@Column(name = "lifespan")
	private Integer lifespan;

	@Column(name = "survival_rate")
	private Double survivalRate;

	@Column(name = "mutation_rate")
	private Double mutationRate;

	@Column(name = "crossover_rate")
	private Double crossoverRate;

	@Column(name = "crossover_algorithm")
	@Enumerated(EnumType.STRING)
	private String crossoverAlgorithm;

	@Column(name = "fitness_evaluator")
	private String fitnessEvaluator;

	@Column(name = "mutation_algorithm")
	@Enumerated(EnumType.STRING)
	private String mutationAlgorithm;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "executionStatistics", cascade = CascadeType.ALL)
	private List<GenerationStatistics> generationStatisticsList = new ArrayList<GenerationStatistics>();

	/**
	 * Default no-args constructor
	 */
	public ExecutionStatistics() {
	}

	public ExecutionStatistics(Date startDateTime, GeneticAlgorithmStrategy strategy) {
		this.startDateTime = startDateTime;
		this.populationSize = strategy.getPopulationSize();
		this.lifespan = strategy.getLifespan();
		this.survivalRate = strategy.getSurvivalRate();
		this.mutationRate = strategy.getMutationRate();
		this.crossoverRate = strategy.getCrossoverRate();
		this.crossoverAlgorithm = strategy.getCrossoverAlgorithm().getClass().getSimpleName();
		this.fitnessEvaluator = strategy.getFitnessEvaluator().getClass().getSimpleName();
		this.mutationAlgorithm = strategy.getMutationAlgorithm().getClass().getSimpleName();
	}

	/**
	 * @return the id
	 */
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
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param startDateTime
	 *            the startDateTime to set
	 */
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @return the populationSize
	 */
	public Integer getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the lifespan
	 */
	public Integer getLifespan() {
		return lifespan;
	}

	/**
	 * @param lifespan
	 *            the lifespan to set
	 */
	public void setLifespan(Integer lifespan) {
		this.lifespan = lifespan;
	}

	/**
	 * @return the survivalRate
	 */
	public Double getSurvivalRate() {
		return survivalRate;
	}

	/**
	 * @param survivalRate
	 *            the survivalRate to set
	 */
	public void setSurvivalRate(Double survivalRate) {
		this.survivalRate = survivalRate;
	}

	/**
	 * @return the mutationRate
	 */
	public Double getMutationRate() {
		return mutationRate;
	}

	/**
	 * @param mutationRate
	 *            the mutationRate to set
	 */
	public void setMutationRate(Double mutationRate) {
		this.mutationRate = mutationRate;
	}

	/**
	 * @return the crossoverRate
	 */
	public Double getCrossoverRate() {
		return crossoverRate;
	}

	/**
	 * @param crossoverRate
	 *            the crossoverRate to set
	 */
	public void setCrossoverRate(Double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	/**
	 * @return the crossoverAlgorithm
	 */
	public String getCrossoverAlgorithm() {
		return crossoverAlgorithm;
	}

	/**
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	public void setCrossoverAlgorithm(String crossoverAlgorithm) {
		this.crossoverAlgorithm = crossoverAlgorithm;
	}

	/**
	 * @return the fitnessEvaluator
	 */
	public String getFitnessEvaluator() {
		return fitnessEvaluator;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	public void setFitnessEvaluator(String fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @return the mutationAlgorithm
	 */
	public String getMutationAlgorithm() {
		return mutationAlgorithm;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	public void setMutationAlgorithm(String mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @return an unmodifiable List of GenerationStatistics
	 */
	public List<GenerationStatistics> getGenerationStatisticsList() {
		return Collections.unmodifiableList(this.generationStatisticsList);
	}

	/**
	 * @param generationStatistics
	 *            the GenerationStatistics to add
	 */
	public void addGenerationStatistics(GenerationStatistics generationStatistics) {
		this.generationStatisticsList.add(generationStatistics);
	}

	/**
	 * @param generationStatistics
	 *            the GenerationStatistics to remove
	 */
	public void removeGenerationStatistics(GenerationStatistics generationStatistics) {
		this.generationStatisticsList.remove(generationStatistics);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((crossoverAlgorithm == null) ? 0 : crossoverAlgorithm.hashCode());
		result = prime * result + ((crossoverRate == null) ? 0 : crossoverRate.hashCode());
		result = prime * result + ((endDateTime == null) ? 0 : endDateTime.hashCode());
		result = prime * result + ((fitnessEvaluator == null) ? 0 : fitnessEvaluator.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lifespan == null) ? 0 : lifespan.hashCode());
		result = prime * result + ((mutationAlgorithm == null) ? 0 : mutationAlgorithm.hashCode());
		result = prime * result + ((mutationRate == null) ? 0 : mutationRate.hashCode());
		result = prime * result + ((populationSize == null) ? 0 : populationSize.hashCode());
		result = prime * result + ((startDateTime == null) ? 0 : startDateTime.hashCode());
		result = prime * result + ((survivalRate == null) ? 0 : survivalRate.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExecutionStatistics other = (ExecutionStatistics) obj;
		if (crossoverAlgorithm == null) {
			if (other.crossoverAlgorithm != null) {
				return false;
			}
		} else if (!crossoverAlgorithm.equals(other.crossoverAlgorithm)) {
			return false;
		}
		if (crossoverRate == null) {
			if (other.crossoverRate != null) {
				return false;
			}
		} else if (!crossoverRate.equals(other.crossoverRate)) {
			return false;
		}
		if (endDateTime == null) {
			if (other.endDateTime != null) {
				return false;
			}
		} else if (!endDateTime.equals(other.endDateTime)) {
			return false;
		}
		if (fitnessEvaluator == null) {
			if (other.fitnessEvaluator != null) {
				return false;
			}
		} else if (!fitnessEvaluator.equals(other.fitnessEvaluator)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (lifespan == null) {
			if (other.lifespan != null) {
				return false;
			}
		} else if (!lifespan.equals(other.lifespan)) {
			return false;
		}
		if (mutationAlgorithm == null) {
			if (other.mutationAlgorithm != null) {
				return false;
			}
		} else if (!mutationAlgorithm.equals(other.mutationAlgorithm)) {
			return false;
		}
		if (mutationRate == null) {
			if (other.mutationRate != null) {
				return false;
			}
		} else if (!mutationRate.equals(other.mutationRate)) {
			return false;
		}
		if (populationSize == null) {
			if (other.populationSize != null) {
				return false;
			}
		} else if (!populationSize.equals(other.populationSize)) {
			return false;
		}
		if (startDateTime == null) {
			if (other.startDateTime != null) {
				return false;
			}
		} else if (!startDateTime.equals(other.startDateTime)) {
			return false;
		}
		if (survivalRate == null) {
			if (other.survivalRate != null) {
				return false;
			}
		} else if (!survivalRate.equals(other.survivalRate)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ExecutionStatistics [id=" + id + ", startDateTime=" + startDateTime
				+ ", endDateTime=" + endDateTime + ", populationSize=" + populationSize
				+ ", lifespan=" + lifespan + ", survivalRate=" + survivalRate + ", mutationRate="
				+ mutationRate + ", crossoverRate=" + crossoverRate + ", crossoverAlgorithm="
				+ crossoverAlgorithm + ", fitnessEvaluator=" + fitnessEvaluator
				+ ", mutationAlgorithm=" + mutationAlgorithm + "]";
	}
}