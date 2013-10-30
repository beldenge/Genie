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

package com.ciphertool.genetics.algorithms.crossover;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.util.ChromosomeHelper;

public class LiberalCrossoverAlgorithm implements CrossoverAlgorithm {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	private FitnessEvaluator fitnessEvaluator;
	private GeneListDao geneListDao;
	private ChromosomeHelper chromosomeHelper;
	private MutationAlgorithm mutationAlgorithm;
	private boolean mutateDuringCrossover;

	@Override
	public List<Chromosome> crossover(Chromosome parentA, Chromosome parentB) {
		List<Chromosome> children = new ArrayList<Chromosome>();

		Chromosome firstChild = performCrossover(parentA, parentB);
		// The chromosome will be null if it's identical to one of its parents
		if (firstChild != null) {
			children.add(firstChild);
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	/**
	 * This crossover algorithm does a liberal amount of changes since it
	 * replaces genes regardless of their begin and end sequence positions
	 */
	public Chromosome performCrossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = parentA.clone();

		int genesBefore = 0;
		int childGeneIndex = 0;
		Gene geneCopy = null;
		Double originalFitness = 0.0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childGeneIndex < child.getGenes().size()
				&& childGeneIndex < parentB.getGenes().size()) {
			/*
			 * Replace from parentB and reevaluate to see if it improves.
			 */
			geneCopy = child.getGenes().get(childGeneIndex).clone();

			originalFitness = child.getFitness();

			genesBefore = child.getGenes().size();

			child.replaceGene(childGeneIndex, parentB.getGenes().get(childGeneIndex).clone());

			while (child.actualSize() < child.targetSize()) {
				child.addGene(geneListDao.findRandomGene(child, child.actualSize() - 1));
			}

			fitnessEvaluator.evaluate(child);

			/*
			 * Revert to the original gene if this decreased fitness. It's ok to
			 * let non-beneficial changes progress, as long as they are not
			 * detrimental.
			 */
			if (child.getFitness() < originalFitness) {
				child.replaceGene(childGeneIndex, geneCopy);

				while (child.getGenes().size() > genesBefore) {
					child.removeGene(child.getGenes().size() - 1);
				}

				/*
				 * Reset the fitness to what it was before the replacement.
				 */
				child.setFitness(originalFitness);
			}

			childGeneIndex++;
		}

		/*
		 * Trim the Chromosome in case it ends with too many sequences due to
		 * the nature of this algorithm
		 */
		chromosomeHelper.resizeChromosome(child);

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		// Don't return this child if it's identical to one of its parents
		if (child.equals(parentA) || child.equals(parentB)) {
			return null;
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
	@Override
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

	/**
	 * @param chromosomeHelper
	 *            the chromosomeHelper to set
	 */
	@Required
	public void setChromosomeHelper(ChromosomeHelper chromosomeHelper) {
		this.chromosomeHelper = chromosomeHelper;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	@Required
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param mutateDuringCrossover
	 *            the mutateDuringCrossover to set
	 */
	@Override
	public void setMutateDuringCrossover(boolean mutateDuringCrossover) {
		this.mutateDuringCrossover = mutateDuringCrossover;
	}
}
