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

package com.ciphertool.genetics.algorithms.crossover.keyless;

import com.ciphertool.genetics.algorithms.crossover.LowestCommonGroupCrossoverProgressDto;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class LowestCommonGroupCrossoverAlgorithmHelper {

	protected static void advanceIndexes(LowestCommonGroupCrossoverProgressDto crossoverProgressDto, KeylessChromosome first, KeylessChromosome second, int geneOffset) {

		if (crossoverProgressDto.getFirstChromosomeEndGeneIndex() >= first.getGenes().size()
				|| crossoverProgressDto.getSecondChromosomeEndGeneIndex() >= second.getGenes().size()) {
			// Nothing to do
			return;
		}

		int firstChromosomeSize = first.getGenes().size();
		int secondChromosomeSize = second.getGenes().size();

		if (crossoverProgressDto.getFirstChromosomeSequencePosition() == crossoverProgressDto.getSecondChromosomeSequencePosition()) {
			crossoverProgressDto.advanceFirstChromosomeEndGeneIndexBy(geneOffset + 1);
			crossoverProgressDto.advanceSecondChromosomeEndGeneIndexBy(1);

			crossoverProgressDto.setFirstChromosomeBeginGeneIndex(crossoverProgressDto.getFirstChromosomeEndGeneIndex());
			crossoverProgressDto.setSecondChromosomeBeginGeneIndex(crossoverProgressDto.getSecondChromosomeEndGeneIndex());

			/*
			 * To avoid IndexOutOfBoundsException, first check that the Gene index hasn't been exceeded.
			 */
			if (crossoverProgressDto.getFirstChromosomeEndGeneIndex() < firstChromosomeSize) {
				crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(((VariableLengthGene) first.getGenes().get(crossoverProgressDto.getFirstChromosomeBeginGeneIndex())).size());
			}

			/*
			 * To avoid IndexOutOfBoundsException, first check that the Gene index hasn't been exceeded.
			 */
			if (crossoverProgressDto.getSecondChromosomeEndGeneIndex() < secondChromosomeSize) {
				crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(((VariableLengthGene) second.getGenes().get(crossoverProgressDto.getSecondChromosomeBeginGeneIndex())).size());
			}
		} else if (crossoverProgressDto.getFirstChromosomeSequencePosition() > crossoverProgressDto.getSecondChromosomeSequencePosition()) {
			crossoverProgressDto.advanceSecondChromosomeEndGeneIndexBy(1);

			/*
			 * To avoid IndexOutOfBoundsException, first check that the Gene index hasn't been exceeded.
			 */
			if (crossoverProgressDto.getSecondChromosomeEndGeneIndex() < secondChromosomeSize) {
				crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(((VariableLengthGene) second.getGenes().get(crossoverProgressDto.getSecondChromosomeEndGeneIndex())).size());
			}
		} else {
			crossoverProgressDto.advanceFirstChromosomeEndGeneIndexBy(1);

			/*
			 * To avoid IndexOutOfBoundsException, first check that the Gene index hasn't been exceeded.
			 */
			if (crossoverProgressDto.getFirstChromosomeEndGeneIndex() < firstChromosomeSize) {
				crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(((VariableLengthGene) first.getGenes().get(crossoverProgressDto.getFirstChromosomeEndGeneIndex())).size());
			}
		}
	}
}
