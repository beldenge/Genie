package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class LowestCommonGroupCrossoverAlgorithm implements CrossoverAlgorithm {
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

		int parentBSize = parentB.getGenes().size();
		int childSequencePosition = 0;
		int parentSequencePosition = 0;
		int childBeginGeneIndex = 0;
		int childEndGeneIndex = 0;
		int parentBeginGeneIndex = 0;
		int parentEndGeneIndex = 0;
		int insertCount = 0;
		int geneOffset = 0;
		Integer originalFitness = 0;
		List<Gene> childGeneCopies;

		childSequencePosition += child.getGenes().get(childBeginGeneIndex).size();
		parentSequencePosition += parentB.getGenes().get(parentBeginGeneIndex).size();

		/*
		 * Make sure we don't exceed parentB's index, or else we will get an
		 * IndexOutOfBoundsException
		 */
		while (childEndGeneIndex < child.getGenes().size() && parentEndGeneIndex < parentBSize) {
			/*
			 * Replace from parentB and reevaluate to see if it improves. We are
			 * extra careful here since genes won't match exactly with sequence
			 * position.
			 */
			if (childSequencePosition == parentSequencePosition) {
				originalFitness = child.getFitness();

				/*
				 * Pull out Genes from child into a temporary List.
				 */
				childGeneCopies = new ArrayList<Gene>();
				for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
					childGeneCopies.add(child.getGenes().get(childBeginGeneIndex).clone());

					child.removeGene(childBeginGeneIndex);
				}

				/*
				 * Insert cloned parent Genes into child. insertCount works as
				 * an offset so that the Genes are inserted in the correct
				 * order.
				 */
				insertCount = 0;
				for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
					child.insertGene(childBeginGeneIndex + insertCount, parentB.getGenes().get(j)
							.clone());

					insertCount++;
				}

				fitnessEvaluator.evaluate(child);

				/*
				 * Revert to the original gene if this did not increase fitness
				 */
				if (child.getFitness() <= originalFitness) {
					/*
					 * Remove the parent Genes from the child.
					 */
					for (int j = parentBeginGeneIndex; j <= parentEndGeneIndex; j++) {
						child.removeGene(childBeginGeneIndex);
					}

					/*
					 * Insert the child Gene copies back into the child.
					 */
					for (int i = childBeginGeneIndex; i <= childEndGeneIndex; i++) {
						child.insertGene(i, childGeneCopies.remove(0));
					}

					/*
					 * Reset the fitness to what it was before the replacement.
					 */
					fitnessEvaluator.evaluate(child);

					geneOffset = 0;
				} else {
					/*
					 * Offset child gene indices by the number of Genes inserted
					 * from parentB, since the number of Genes inserted from
					 * parentB could be different than the number of Genes
					 * removed from child. The result can be either positive or
					 * negative.
					 */
					geneOffset = (parentEndGeneIndex - parentBeginGeneIndex)
							- (childEndGeneIndex - childBeginGeneIndex);
				}

				childEndGeneIndex += geneOffset + 1;
				parentEndGeneIndex++;

				childBeginGeneIndex = childEndGeneIndex;
				parentBeginGeneIndex = parentEndGeneIndex;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (childEndGeneIndex < child.getGenes().size()) {
					childSequencePosition += child.getGenes().get(childBeginGeneIndex).size();
				}

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (parentEndGeneIndex < parentBSize) {
					parentSequencePosition += parentB.getGenes().get(parentBeginGeneIndex).size();
				}
			} else if (childSequencePosition > parentSequencePosition) {
				parentEndGeneIndex++;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (parentEndGeneIndex < parentBSize) {
					parentSequencePosition += parentB.getGenes().get(parentEndGeneIndex).size();
				}
			} else { // (childSequencePosition < parentSequencePosition)
				childEndGeneIndex++;

				/*
				 * To avoid IndexOutOfBoundsException, first check that the Gene
				 * index hasn't been exceeded.
				 */
				if (childEndGeneIndex < child.getGenes().size()) {
					childSequencePosition += child.getGenes().get(childEndGeneIndex).size();
				}
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
