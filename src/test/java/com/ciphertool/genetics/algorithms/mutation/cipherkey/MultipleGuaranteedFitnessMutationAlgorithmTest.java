/**
 * Copyright 2015 George Belden
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

package com.ciphertool.genetics.algorithms.mutation.cipherkey;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;

import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.KeyedChromosome;

public class MultipleGuaranteedFitnessMutationAlgorithmTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testGetNumMutations() {
		int maxMutations = 10;
		double mutationCountFactor = 0.2;
		int size = 20;

		MultipleGuaranteedFitnessMutationAlgorithm multipleGuaranteedFitnessMutationAlgorithm = new MultipleGuaranteedFitnessMutationAlgorithm();
		multipleGuaranteedFitnessMutationAlgorithm.setMaxMutations(maxMutations);
		multipleGuaranteedFitnessMutationAlgorithm.setMutationCountFactor(mutationCountFactor);

		Map<Object, Gene> genesMock = mock(Map.class);
		KeyedChromosome<Object> chromosomeMock = mock(KeyedChromosome.class);
		when(chromosomeMock.getGenes()).thenReturn(genesMock);
		when(genesMock.size()).thenReturn(size);

		for (int i = 0; i < 100; i++) {
			int numMutations = multipleGuaranteedFitnessMutationAlgorithm.getNumMutations(chromosomeMock);
			assertTrue(numMutations >= 1 && numMutations <= maxMutations);
		}
	}
}
