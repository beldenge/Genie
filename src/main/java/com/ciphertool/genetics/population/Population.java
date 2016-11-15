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

package com.ciphertool.genetics.population;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.entities.statistics.GenerationStatistics;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public interface Population {
	public Chromosome evaluateFitness(GenerationStatistics generationStatistics) throws InterruptedException;

	public int breed();

	public void recoverFromBackup();

	public void backupIndividuals();

	public void clearIndividuals();

	public void printAscending();

	public int size();

	public List<Chromosome> getIndividuals();

	public void requestStop();

	public void reIndexSelector();

	/**
	 * @param geneticStructure
	 *            the geneticStructure to set
	 */
	public void setGeneticStructure(Object geneticStructure);

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator);

	/**
	 * This is NOT required. We will not always know the solution. In fact, that should be the rare case.
	 * 
	 * @param knownSolutionFitnessEvaluator
	 *            the knownSolutionFitnessEvaluator to set
	 */
	public void setKnownSolutionFitnessEvaluator(FitnessEvaluator knownSolutionFitnessEvaluator);

	/**
	 * This is NOT required.
	 * 
	 * @param compareToKnownSolution
	 *            the compareToKnownSolution to set
	 */
	public void setCompareToKnownSolution(Boolean compareToKnownSolution);

	/**
	 * @param stopRequested
	 *            the stopRequested to set
	 */
	public void setStopRequested(boolean stopRequested);

	/**
	 * @param targetSize
	 *            the targetSize to set
	 */
	public void setTargetSize(int targetSize);

	/**
	 * @param selector
	 *            the Selector to set
	 */
	public void setSelector(Selector selector);

	@SuppressWarnings({ "unchecked" })
	default double calculateEntropy() {
		if (!(this.getIndividuals().get(0) instanceof KeyedChromosome)) {
			throw new UnsupportedOperationException(
					"Calculation of entropy is currently only supported for KeyedChromosome types.");
		}

		Map<Object, Map<Object, Integer>> symbolCounts = new HashMap<Object, Map<Object, Integer>>();

		Object geneKey;
		Object geneValue;
		Integer currentCount;
		Map<Object, Integer> symbolCountMap;

		// Count occurrences of each Gene value
		for (Chromosome chromosome : this.getIndividuals()) {
			for (Map.Entry<Object, Gene> entry : ((KeyedChromosome<Object>) chromosome).getGenes().entrySet()) {
				geneKey = entry.getKey();

				symbolCountMap = symbolCounts.get(geneKey);

				if (symbolCountMap == null) {
					symbolCounts.put(geneKey, new HashMap<Object, Integer>());

					symbolCountMap = symbolCounts.get(geneKey);
				}

				geneValue = entry.getValue();
				currentCount = symbolCountMap.get(geneValue);

				symbolCountMap.put(geneValue, (currentCount != null) ? (currentCount + 1) : 1);
			}
		}

		Map<Object, Map<Object, Double>> symbolProbabilities = new HashMap<Object, Map<Object, Double>>();

		double populationSize = (double) this.size();

		Map<Object, Double> probabilityMap;

		// Calculate probability of each Gene value
		for (Map.Entry<Object, Map<Object, Integer>> entry : symbolCounts.entrySet()) {
			probabilityMap = new HashMap<Object, Double>();

			symbolProbabilities.put(entry.getKey(), probabilityMap);

			for (Map.Entry<Object, Integer> entryInner : entry.getValue().entrySet()) {
				probabilityMap.put(entryInner.getKey(), ((double) entryInner.getValue() / (double) populationSize));
			}
		}

		int base = symbolCounts.size();

		double totalEntropy = 0.0;

		// Calculate the entropy of each Gene independently, and add it to the total entropy value
		for (Map.Entry<Object, Map<Object, Double>> entry : symbolProbabilities.entrySet()) {
			for (Map.Entry<Object, Double> entryInner : entry.getValue().entrySet()) {
				totalEntropy += (entryInner.getValue() * logBase(entryInner.getValue(), base));
			}
		}

		totalEntropy *= -1.0;

		// return the average entropy among the symbols
		return totalEntropy / (double) symbolProbabilities.size();
	}

	static double logBase(double num, int base) {
		return (Math.log(num) / Math.log(base));
	}
}
