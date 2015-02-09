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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.entities.VariableLengthGene;

public class ConservativeCrossoverAlgorithmHelperTest extends CrossoverAlgorithmTestBase {

	@Test
	public void testAdvanceIndexes() {
		KeylessChromosome child = getMom();
		KeylessChromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(((VariableLengthGene) child.getGenes().get(0)).size(), crossoverProgressDto
				.getFirstChromosomeSequencePosition());
		assertEquals(((VariableLengthGene) parent.getGenes().get(0)).size(), crossoverProgressDto
				.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(1, crossoverProgressDto.getFirstChromosomeGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(6, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(2, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(2, crossoverProgressDto.getFirstChromosomeGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(11, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(2, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(3, crossoverProgressDto.getFirstChromosomeGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(11, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(11, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(3, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(3, crossoverProgressDto.getFirstChromosomeGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenChildReachesEnd() {
		KeylessChromosome child = getMom();
		KeylessChromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();
		crossoverProgressDto.advanceFirstChromosomeGeneIndexBy(child.getGenes().size());
		crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(child.actualSize());

		assertEquals(child.actualSize().intValue(), crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getFirstChromosomeGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		// None of the indexes should have advanced
		assertEquals(child.actualSize().intValue(), crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getFirstChromosomeGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenParentReachesEnd() {
		KeylessChromosome child = getMom();
		KeylessChromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();
		crossoverProgressDto.advanceSecondChromosomeGeneIndexBy(parent.getGenes().size());
		crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(parent.actualSize());

		assertEquals(0, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getSecondChromosomeSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		// None of the indexes should have advanced
		assertEquals(0, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getSecondChromosomeSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto.getSecondChromosomeGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeGeneIndex());
	}
}
