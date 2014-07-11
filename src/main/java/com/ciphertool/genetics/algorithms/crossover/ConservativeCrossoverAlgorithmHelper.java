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

import com.ciphertool.genetics.entities.Chromosome;

public class ConservativeCrossoverAlgorithmHelper {

	protected static void advanceIndexes(CrossoverProgressDto crossoverProgressDto,
			Chromosome child, Chromosome parent) {

		if (crossoverProgressDto.getFirstChromosomeGeneIndex() >= child.getGenes().size()
				|| crossoverProgressDto.getSecondChromosomeGeneIndex() >= parent.getGenes().size()) {
			// Nothing to do
			return;
		}

		int childGeneSize = child.getGenes().get(crossoverProgressDto.getFirstChromosomeGeneIndex()).size();
		int parentGeneSize = parent.getGenes().get(crossoverProgressDto.getSecondChromosomeGeneIndex())
				.size();

		if (crossoverProgressDto.getFirstChromosomeSequencePosition() == crossoverProgressDto
				.getSecondChromosomeSequencePosition()) {
			crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(childGeneSize);
			crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(parentGeneSize);

			crossoverProgressDto.advanceFirstChromosomeGeneIndexBy(1);
			crossoverProgressDto.advanceSecondChromosomeGeneIndexBy(1);
		} else if (crossoverProgressDto.getFirstChromosomeSequencePosition() > crossoverProgressDto
				.getSecondChromosomeSequencePosition()) {
			crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(parentGeneSize);
			crossoverProgressDto.advanceSecondChromosomeGeneIndexBy(1);
		} else {
			crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(childGeneSize);
			crossoverProgressDto.advanceFirstChromosomeGeneIndexBy(1);
		}
	}
}
