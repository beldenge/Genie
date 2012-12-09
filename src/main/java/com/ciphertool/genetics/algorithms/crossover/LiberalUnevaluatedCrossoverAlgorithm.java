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

package com.ciphertool.genetics.algorithms.crossover;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.util.ChromosomeHelper;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class LiberalUnevaluatedCrossoverAlgorithm implements CrossoverAlgorithm {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	private GeneListDao geneListDao;
	private ChromosomeHelper chromosomeHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.genetics.algorithms.CrossoverAlgorithm#crossover(com.
	 * ciphertool.genetics.entities.Chromosome,
	 * com.ciphertool.genetics.entities.Chromosome)
	 */
	@Override
	public List<Chromosome> crossover(Chromosome parentA, Chromosome parentB) {
		List<Chromosome> children = new ArrayList<Chromosome>();
		children.add(performCrossover(parentA, parentB));
		children.add(performCrossover(parentB, parentA));

		return children;
	}

	/**
	 * This crossover algorithm does a liberal amount of changes since it
	 * replaces genes regardless of their begin and end sequence positions
	 */
	public Chromosome performCrossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = parentA.clone();

		int childGeneIndex = 0;

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childGeneIndex < child.getGenes().size()
				&& childGeneIndex < parentB.getGenes().size()) {
			/*
			 * Flip a coin to see if the current Gene should be replaced
			 */
			if (((int) (Math.random() * 2)) == 0) {
				child.replaceGene(childGeneIndex, parentB.getGenes().get(childGeneIndex).clone());

				while (child.actualSize() < child.targetSize()) {
					child.addGene(geneListDao.findRandomGene(child, child.actualSize() - 1));
				}
			}

			childGeneIndex++;
		}

		/*
		 * Trim the Chromosome in case it ends with too many sequences due to
		 * the nature of this algorithm
		 */
		chromosomeHelper.resizeChromosome(child);

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
		/*
		 * fitnessEvaluator is required by other crossover algorithms, so this
		 * is just to satisfy the interface.
		 */
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
}