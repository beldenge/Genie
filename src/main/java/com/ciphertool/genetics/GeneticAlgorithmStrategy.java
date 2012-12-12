package com.ciphertool.genetics;

import com.ciphertool.genetics.algorithms.crossover.CrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.algorithms.selection.SelectionAlgorithm;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class GeneticAlgorithmStrategy {
	private Object geneticStructure;
	private Integer populationSize;
	private Integer lifespan;
	private Double survivalRate;
	private Double mutationRate;
	private Double crossoverRate;
	private Integer maxGenerations;
	private CrossoverAlgorithm crossoverAlgorithm;
	private FitnessEvaluator fitnessEvaluator;
	private MutationAlgorithm mutationAlgorithm;
	private SelectionAlgorithm selectionAlgorithm;
	private FitnessEvaluator knownSolutionFitnessEvaluator;
	private Boolean compareToKnownSolution;
	private static final Boolean COMPARE_TO_KNOWN_SOLUTION_DEFAULT = false;

	/**
	 * Default no-args constructor
	 */
	public GeneticAlgorithmStrategy() {
	}

	/**
	 * Most-args constructor
	 * 
	 * @param geneticStructure
	 *            the geneticStructure to set
	 * @param populationSize
	 *            the populationSize to set
	 * @param maxGenerations
	 *            the maxGenerations to set
	 * @param survivalRate
	 *            the survivalRate to set
	 * @param mutationRate
	 *            the mutationRate to set
	 * @param crossoverRate
	 *            the crossoverRate to set
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	public GeneticAlgorithmStrategy(Object geneticStructure, int populationSize, int lifespan,
			int maxGenerations, double survivalRate, double mutationRate, double crossoverRate,
			FitnessEvaluator fitnessEvaluator, CrossoverAlgorithm crossoverAlgorithm,
			MutationAlgorithm mutationAlgorithm, SelectionAlgorithm selectionAlgorithm) {
		this.geneticStructure = geneticStructure;
		this.populationSize = populationSize;
		this.lifespan = lifespan;
		this.maxGenerations = maxGenerations;
		this.survivalRate = survivalRate;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;

		this.fitnessEvaluator = fitnessEvaluator;
		this.fitnessEvaluator.setGeneticStructure(geneticStructure);

		this.crossoverAlgorithm = crossoverAlgorithm;
		this.crossoverAlgorithm.setFitnessEvaluator(this.fitnessEvaluator);

		this.mutationAlgorithm = mutationAlgorithm;

		this.selectionAlgorithm = selectionAlgorithm;

		this.knownSolutionFitnessEvaluator = null;
		this.compareToKnownSolution = COMPARE_TO_KNOWN_SOLUTION_DEFAULT;
	}

	/**
	 * Full-args constructor
	 * 
	 * @param geneticStructure
	 *            the geneticStructure to set
	 * @param populationSize
	 *            the populationSize to set
	 * @param maxGenerations
	 *            the maxGenerations to set
	 * @param survivalRate
	 *            the survivalRate to set
	 * @param mutationRate
	 *            the mutationRate to set
	 * @param crossoverRate
	 *            the crossoverRate to set
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 * @param useKnownSolutionFitnessEvaluator
	 *            the useKnownSolutionFitnessEvaluator to set
	 */
	public GeneticAlgorithmStrategy(Object geneticStructure, int populationSize, int lifespan,
			int maxGenerations, double survivalRate, double mutationRate, double crossoverRate,
			FitnessEvaluator fitnessEvaluator, CrossoverAlgorithm crossoverAlgorithm,
			MutationAlgorithm mutationAlgorithm, SelectionAlgorithm selectionAlgorithm,
			FitnessEvaluator knownSolutionFitnessEvaluator, Boolean compareToKnownSolution) {
		this.geneticStructure = geneticStructure;
		this.populationSize = populationSize;
		this.lifespan = lifespan;
		this.maxGenerations = maxGenerations;
		this.survivalRate = survivalRate;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;

		this.fitnessEvaluator = fitnessEvaluator;
		this.fitnessEvaluator.setGeneticStructure(geneticStructure);

		this.crossoverAlgorithm = crossoverAlgorithm;
		this.crossoverAlgorithm.setFitnessEvaluator(this.fitnessEvaluator);

		this.mutationAlgorithm = mutationAlgorithm;

		this.selectionAlgorithm = selectionAlgorithm;

		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
		if (knownSolutionFitnessEvaluator != null) {
			this.knownSolutionFitnessEvaluator.setGeneticStructure(geneticStructure);
		}
		this.compareToKnownSolution = compareToKnownSolution;
	}

	/**
	 * @return the geneticStructure
	 */
	public Object getGeneticStructure() {
		return geneticStructure;
	}

	/**
	 * @param geneticStructure
	 *            the geneticStructure to set
	 */
	public void setGeneticStructure(Object geneticStructure) {
		this.geneticStructure = geneticStructure;
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
	 * @return the maxGenerations
	 */
	public Integer getMaxGenerations() {
		return maxGenerations;
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	public void setMaxGenerations(Integer maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @return the crossoverAlgorithm
	 */
	public CrossoverAlgorithm getCrossoverAlgorithm() {
		return crossoverAlgorithm;
	}

	/**
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	public void setCrossoverAlgorithm(CrossoverAlgorithm crossoverAlgorithm) {
		this.crossoverAlgorithm = crossoverAlgorithm;
	}

	/**
	 * @return the fitnessEvaluator
	 */
	public FitnessEvaluator getFitnessEvaluator() {
		return fitnessEvaluator;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @return the mutationAlgorithm
	 */
	public MutationAlgorithm getMutationAlgorithm() {
		return mutationAlgorithm;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @return the selectionAlgorithm
	 */
	public SelectionAlgorithm getSelectionAlgorithm() {
		return selectionAlgorithm;
	}

	/**
	 * @param selectionAlgorithm
	 *            the selectionAlgorithm to set
	 */
	public void setSelectionAlgorithm(SelectionAlgorithm selectionAlgorithm) {
		this.selectionAlgorithm = selectionAlgorithm;
	}

	/**
	 * @return the knownSolutionFitnessEvaluator
	 */
	public FitnessEvaluator getKnownSolutionFitnessEvaluator() {
		return knownSolutionFitnessEvaluator;
	}

	/**
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 */
	public void setKnownSolutionFitnessEvaluator(FitnessEvaluator knownSolutionFitnessEvaluator) {
		this.knownSolutionFitnessEvaluator = knownSolutionFitnessEvaluator;
	}

	/**
	 * @return the compareToKnownSolution
	 */
	public Boolean getCompareToKnownSolution() {
		return compareToKnownSolution;
	}

	/**
	 * @param compareToKnownSolution
	 *            the compareToKnownSolution to set
	 */
	public void setCompareToKnownSolution(Boolean compareToKnownSolution) {
		this.compareToKnownSolution = compareToKnownSolution;
	}
}
