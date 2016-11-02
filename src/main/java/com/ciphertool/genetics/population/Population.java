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

import java.util.List;

import com.ciphertool.genetics.algorithms.selection.modes.Selector;
import com.ciphertool.genetics.entities.Chromosome;
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
}
