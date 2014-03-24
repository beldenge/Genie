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

import com.ciphertool.genetics.entities.Chromosome;

public class ConservativeCrossoverAlgorithmHelperTest extends CrossoverAlgorithmTestBase {

	@Test
	public void testAdvanceIndexes() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(child.getGenes().get(0).size(), crossoverProgressDto
				.getChildSequencePosition());
		assertEquals(parent.getGenes().get(0).size(), crossoverProgressDto
				.getParentSequencePosition());
		assertEquals(1, crossoverProgressDto.getParentGeneIndex());
		assertEquals(1, crossoverProgressDto.getChildGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(6, crossoverProgressDto.getChildSequencePosition());
		assertEquals(8, crossoverProgressDto.getParentSequencePosition());
		assertEquals(2, crossoverProgressDto.getParentGeneIndex());
		assertEquals(2, crossoverProgressDto.getChildGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(11, crossoverProgressDto.getChildSequencePosition());
		assertEquals(8, crossoverProgressDto.getParentSequencePosition());
		assertEquals(2, crossoverProgressDto.getParentGeneIndex());
		assertEquals(3, crossoverProgressDto.getChildGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		assertEquals(11, crossoverProgressDto.getChildSequencePosition());
		assertEquals(11, crossoverProgressDto.getParentSequencePosition());
		assertEquals(3, crossoverProgressDto.getParentGeneIndex());
		assertEquals(3, crossoverProgressDto.getChildGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenChildReachesEnd() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();
		crossoverProgressDto.advanceChildGeneIndexBy(child.getGenes().size());
		crossoverProgressDto.advanceChildSequencePositionBy(child.actualSize());

		assertEquals(child.actualSize().intValue(), crossoverProgressDto.getChildSequencePosition());
		assertEquals(0, crossoverProgressDto.getParentSequencePosition());
		assertEquals(0, crossoverProgressDto.getParentGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getChildGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		// None of the indexes should have advanced
		assertEquals(child.actualSize().intValue(), crossoverProgressDto.getChildSequencePosition());
		assertEquals(0, crossoverProgressDto.getParentSequencePosition());
		assertEquals(0, crossoverProgressDto.getParentGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getChildGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenParentReachesEnd() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();
		crossoverProgressDto.advanceParentGeneIndexBy(parent.getGenes().size());
		crossoverProgressDto.advanceParentSequencePositionBy(parent.actualSize());

		assertEquals(0, crossoverProgressDto.getChildSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getParentSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto.getParentGeneIndex());
		assertEquals(0, crossoverProgressDto.getChildGeneIndex());

		ConservativeCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child, parent);

		// None of the indexes should have advanced
		assertEquals(0, crossoverProgressDto.getChildSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getParentSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto.getParentGeneIndex());
		assertEquals(0, crossoverProgressDto.getChildGeneIndex());
	}
}
