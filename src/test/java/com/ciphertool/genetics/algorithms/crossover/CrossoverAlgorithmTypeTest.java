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

package com.ciphertool.genetics.algorithms.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.ciphertool.genetics.algorithms.crossover.cipherkey.RandomCentromereCrossoverAlgorithm;

public class CrossoverAlgorithmTypeTest {
	@Test
	public void testTypes() {
		assertEquals(8, CrossoverAlgorithmType.values().length);

		assertSame(ConservativeCrossoverAlgorithm.class, CrossoverAlgorithmType.CONSERVATIVE.getType());
		assertSame(ConservativeSinglePointCrossoverAlgorithm.class, CrossoverAlgorithmType.CONSERVATIVE_SINGLE_POINT
				.getType());
		assertSame(ConservativeUnevaluatedCrossoverAlgorithm.class, CrossoverAlgorithmType.CONSERVATIVE_UNEVALUATED
				.getType());
		assertSame(LiberalCrossoverAlgorithm.class, CrossoverAlgorithmType.LIBERAL.getType());
		assertSame(LiberalUnevaluatedCrossoverAlgorithm.class, CrossoverAlgorithmType.LIBERAL_UNEVALUATED.getType());
		assertSame(LowestCommonGroupCrossoverAlgorithm.class, CrossoverAlgorithmType.LOWEST_COMMON_GROUP.getType());
		assertSame(LowestCommonGroupUnevaluatedCrossoverAlgorithm.class,
				CrossoverAlgorithmType.LOWEST_COMMON_GROUP_UNEVALUATED.getType());
		assertSame(RandomCentromereCrossoverAlgorithm.class, CrossoverAlgorithmType.RANDOM_CENTROMERE.getType());
	}
}
