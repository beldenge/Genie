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

package com.ciphertool.genetics.algorithms.mutation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ciphertool.genetics.algorithms.mutation.cipherkey.RandomValueMutationAlgorithm;

public class MutationAlgorithmTypeTest {

	@Test
	public void testTypes() {
		assertEquals(5, MutationAlgorithmType.values().length);

		assertEquals(ConservativeMutationAlgorithm.class, MutationAlgorithmType.CONSERVATIVE.getType());
		assertEquals(GroupMutationAlgorithm.class, MutationAlgorithmType.GROUP.getType());
		assertEquals(LiberalMutationAlgorithm.class, MutationAlgorithmType.LIBERAL.getType());
		assertEquals(SingleSequenceMutationAlgorithm.class, MutationAlgorithmType.SINGLE_SEQUENCE.getType());
		assertEquals(RandomValueMutationAlgorithm.class, MutationAlgorithmType.RANDOM_VALUE.getType());
	}
}
