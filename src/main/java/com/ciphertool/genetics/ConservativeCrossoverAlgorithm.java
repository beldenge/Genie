package com.ciphertool.genetics;

import org.springframework.beans.factory.annotation.Required;

public class ConservativeCrossoverAlgorithm implements CrossoverAlgorithm {
	private FitnessEvaluator fitnessEvaluator;
	@SuppressWarnings("unused")
	private GeneListDao geneListDao;

	/**
	 * This crossover algorithm does a conservative amount of changes since it
	 * only replaces genes that begin and end at the exact same sequence
	 * positions
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.CrossoverAlgorithm#crossover(com.ciphertool.zodiacengine.genetic.Chromosome,
	 *      com.ciphertool.zodiacengine.genetic.Chromosome)
	 */
	@Override
	public Chromosome crossover(Chromosome parentA, Chromosome parentB) {
		Chromosome child = (Chromosome) parentA.clone();

		int childSequencePosition = 0;
		int parentSequencePosition = 0;
		int childGeneIndex = 0;
		int parentGeneIndex = 0;
		Gene geneCopy = null;
		Integer originalFitness = 0;

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
					geneCopy = child.getGenes().get(childGeneIndex);

					originalFitness = child.getFitness();

					/*
					 * TODO: Does this actually change anything, since the
					 * Plaintext characters for the Solution are not updated?
					 */
					child.getGenes().set(childGeneIndex,
							parentB.getGenes().get(parentGeneIndex).clone());

					fitnessEvaluator.evaluate(child);

					/*
					 * Revert to the original gene if this did not increase
					 * fitness
					 */
					if (child.getFitness() <= originalFitness) {
						child.getGenes().set(childGeneIndex, geneCopy);
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
