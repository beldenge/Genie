package com.ciphertool.genetics.algorithms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class LiberalCrossoverAlgorithm implements CrossoverAlgorithm {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	private FitnessEvaluator fitnessEvaluator;
	private GeneListDao geneListDao;

	/**
	 * This crossover algorithm does a liberal amount of changes since it
	 * replaces genes regardless of their begin and end sequence positions
	 * 
	 * @see com.ciphertool.genetics.algorithms.zodiacengine.genetic.CrossoverAlgorithm#crossover(com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome,
	 *      com.ciphertool.genetics.entities.zodiacengine.genetic.Chromosome)
	 */
	@Override
	public Chromosome crossover(Chromosome parentA, Chromosome parentB) {
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
			 * Revert to the original gene if this did not increase fitness
			 */
			if (child.getFitness() <= originalFitness) {
				child.replaceGene(childGeneIndex, geneCopy);

				while (child.getGenes().size() > genesBefore) {
					child.removeGene(child.getGenes().size() - 1);
				}

				/*
				 * Reset the fitness to what it was before the replacement.
				 */
				fitnessEvaluator.evaluate(child);
			}

			childGeneIndex++;
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
