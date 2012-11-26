package com.ciphertool.genetics.util;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public class ChromosomeHelper {
	private GeneListDao geneListDao;

	/**
	 * Trims or pads the Chromosome in case the new Gene is respectively longer
	 * or shorter than the old Gene.
	 * 
	 * @param chromosomeToResize
	 *            the Chromosome to resize
	 */
	public void resizeChromosome(Chromosome chromosomeToResize) {
		/*
		 * Pad the end with more Genes in case the new Gene was shorter, causing
		 * the sequence length to fall below the target length. This should be
		 * done before truncating in case the Gene we add causes the sequence
		 * length to fall outside the target length.
		 */
		while (chromosomeToResize.actualSize() < chromosomeToResize.targetSize()) {
			chromosomeToResize.addGene(geneListDao.findRandomGene(chromosomeToResize,
					chromosomeToResize.actualSize() - 1));
		}

		/*
		 * Similarly, truncate the last Gene in case the new Gene was longer,
		 * causing the sequence length to exceed the target length.
		 */
		while (chromosomeToResize.actualSize() > chromosomeToResize.targetSize()) {
			Gene lastGene = chromosomeToResize.getGenes().get(
					chromosomeToResize.getGenes().size() - 1);

			if (chromosomeToResize.actualSize() - lastGene.size() >= chromosomeToResize
					.targetSize()) {
				/*
				 * If the last Gene is entirely outside the range of this
				 * Chromosome's target size, just remove it altogether.
				 */
				chromosomeToResize.removeGene(chromosomeToResize.getGenes().size() - 1);
			} else {
				/*
				 * Assign actual size to a variable so it doesn't have to be
				 * calculated on every iteration of the for-loop
				 */
				int actualSize = chromosomeToResize.actualSize();

				for (int i = actualSize; i > chromosomeToResize.targetSize(); i--) {
					lastGene.removeSequence(lastGene.getSequences().get(lastGene.size() - 1));
				}
			}
		}
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
