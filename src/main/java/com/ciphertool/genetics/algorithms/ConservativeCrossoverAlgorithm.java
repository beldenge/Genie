/**
 * Copyright 2012 George Belden
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

package com.ciphertool.genetics.algorithms;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class ConservativeCrossoverAlgorithm implements CrossoverAlgorithm {
	private FitnessEvaluator fitnessEvaluator;
	/*
	 * geneListDao is required by other crossover algorithms, so this is just
	 * for spring bean consistency.
	 */
	@SuppressWarnings("unused")
	private GeneListDao geneListDao;

	/**
	 * This crossover algorithm does a conservative amount of changes since it
	 * only replaces genes that begin and end at the exact same sequence
	 * positions
	 * 
	 * @see com.ciphertool.genetics.algorithms.zodiacengine.genetic.CrossoverAlgorithm#crossover(com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome,
	 *      com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome)
	 */
	@Override
	public Chromosome crossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = (Chromosome) parentA.clone();

		int childSequencePosition = 0;
		int parentSequencePosition = 0;
		int childGeneIndex = 0;
		int parentGeneIndex = 0;
		Gene geneCopy = null;
		Double originalFitness = 0.0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childGeneIndex < child.getGenes().size()
				&& childGeneIndex < parentB.getGenes().size()) {
			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (childSequencePosition == parentSequencePosition) {
				if (child.getGenes().get(childGeneIndex).size() == parentB.getGenes().get(
						parentGeneIndex).size()) {
					geneCopy = child.getGenes().get(childGeneIndex).clone();

					originalFitness = child.getFitness();

					child.replaceGene(childGeneIndex, parentB.getGenes().get(parentGeneIndex)
							.clone());

					fitnessEvaluator.evaluate(child);

					/*
					 * Revert to the original gene if this did not increase
					 * fitness
					 */
					if (child.getFitness() <= originalFitness) {
						child.replaceGene(childGeneIndex, geneCopy);

						/*
						 * Reset the fitness to what it was before the
						 * replacement.
						 */
						fitnessEvaluator.evaluate(child);
					}
				}

				childSequencePosition += child.getGenes().get(childGeneIndex).size();
				parentSequencePosition += parentB.getGenes().get(parentGeneIndex).size();

				childGeneIndex++;
				parentGeneIndex++;
			} else if (childSequencePosition > parentSequencePosition) {
				parentSequencePosition += parentB.getGenes().get(parentGeneIndex).size();
				parentGeneIndex++;
			} else {
				childSequencePosition += child.getGenes().get(childGeneIndex).size();
				childGeneIndex++;
			}
		}

		/*
		 * Child is guaranteed to have at least as good fitness as its parent
		 */
		return child;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Required
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @param geneListDao
	 *            the geneListDao to set
	 */
	@Required
	public void setGeneListDao(GeneListDao geneListDao) {
		this.geneListDao = geneListDao;
	}
}
