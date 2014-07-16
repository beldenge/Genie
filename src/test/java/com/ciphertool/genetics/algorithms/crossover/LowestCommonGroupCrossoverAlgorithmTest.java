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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;

public class LowestCommonGroupCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {
	@Test
	public void testSetFitnessEvaluator() {
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		LowestCommonGroupCrossoverAlgorithm lowestCommonGroupCrossoverAlgorithm = new LowestCommonGroupCrossoverAlgorithm();
		lowestCommonGroupCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorToSet);

		Field fitnessEvaluatorField = ReflectionUtils.findField(
				LowestCommonGroupCrossoverAlgorithm.class, "fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		FitnessEvaluator fitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(
				fitnessEvaluatorField, lowestCommonGroupCrossoverAlgorithm);

		assertSame(fitnessEvaluatorToSet, fitnessEvaluatorFromObject);
	}

	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		LowestCommonGroupCrossoverAlgorithm lowestCommonGroupCrossoverAlgorithm = new LowestCommonGroupCrossoverAlgorithm();
		lowestCommonGroupCrossoverAlgorithm.setMutationAlgorithm(mutationAlgorithmToSet);

		Field mutationAlgorithmField = ReflectionUtils.findField(
				LowestCommonGroupCrossoverAlgorithm.class, "mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils
				.getField(mutationAlgorithmField, lowestCommonGroupCrossoverAlgorithm);

		assertSame(mutationAlgorithmToSet, mutationAlgorithmFromObject);
	}

	@Test
	public void testSetMutateDuringCrossover() {
		boolean mutateDuringCrossoverToSet = true;

		LowestCommonGroupCrossoverAlgorithm lowestCommonGroupCrossoverAlgorithm = new LowestCommonGroupCrossoverAlgorithm();
		lowestCommonGroupCrossoverAlgorithm.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		Field mutateDuringCrossoverField = ReflectionUtils.findField(
				LowestCommonGroupCrossoverAlgorithm.class, "mutateDuringCrossover");
		ReflectionUtils.makeAccessible(mutateDuringCrossoverField);
		boolean mutateDuringCrossoverFromObject = (boolean) ReflectionUtils.getField(
				mutateDuringCrossoverField, lowestCommonGroupCrossoverAlgorithm);

		assertEquals(mutateDuringCrossoverToSet, mutateDuringCrossoverFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testCrossoverWithIllegalState() {
		LowestCommonGroupCrossoverAlgorithm lowestCommonGroupCrossoverAlgorithm = new LowestCommonGroupCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		lowestCommonGroupCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		lowestCommonGroupCrossoverAlgorithm.setMutationAlgorithm(null);
		lowestCommonGroupCrossoverAlgorithm.setMutateDuringCrossover(true);

		Chromosome mom = getMom();
		Chromosome dad = getDad();
		lowestCommonGroupCrossoverAlgorithm.crossover(mom, dad);
	}
}
