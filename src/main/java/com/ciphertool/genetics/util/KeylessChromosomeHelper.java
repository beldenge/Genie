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

package com.ciphertool.genetics.util;

import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class KeylessChromosomeHelper {
	private GeneListDao geneListDao;

	/**
	 * Trims or pads the Chromosome in case the new Gene is respectively longer
	 * or shorter than the old Gene.
	 * 
	 * @param chromosomeToResize
	 *            the Chromosome to resize
	 */
	public void resizeChromosome(KeylessChromosome chromosomeToResize) {
		/*
		 * Pad the end with more Genes in case the new Gene was shorter, causing
		 * the sequence length to fall below the target length. This should be
		 * done before truncating in case the Gene we add causes the sequence
		 * length to fall outside the target length.
		 */
		while (chromosomeToResize.actualSize() < chromosomeToResize.targetSize()) {
			chromosomeToResize.addGene(geneListDao.findRandomGene(chromosomeToResize));
		}

		/*
		 * Similarly, truncate the last Gene in case the new Gene was longer,
		 * causing the sequence length to exceed the target length.
		 */
		while (chromosomeToResize.actualSize() > chromosomeToResize.targetSize()) {
			VariableLengthGene lastGene = (VariableLengthGene) chromosomeToResize.getGenes().get(
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
