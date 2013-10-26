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

package com.ciphertool.genetics.aspects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.Test;

import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class DirtyAspectTest {
	@Test
	public void TestJoinPointSequence() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockChromosome chromosome = new MockChromosome();
		chromosome.setEvaluationNeeded(false);
		MockGene gene = new MockGene();
		gene.setChromosome(chromosome);
		MockSequence sequence = new MockSequence();
		sequence.setGene(gene);

		when(mockJoinPoint.getTarget()).thenReturn(sequence);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		assertTrue(chromosome.isEvaluationNeeded());
	}

	@Test
	public void TestJoinPointGene() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockChromosome chromosome = new MockChromosome();
		chromosome.setEvaluationNeeded(false);
		MockGene gene = new MockGene();
		gene.setChromosome(chromosome);

		when(mockJoinPoint.getTarget()).thenReturn(gene);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		assertTrue(chromosome.isEvaluationNeeded());
	}

	@Test
	public void TestJoinPointChromosome() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockChromosome chromosome = new MockChromosome();
		chromosome.setEvaluationNeeded(false);
		when(mockJoinPoint.getTarget()).thenReturn(chromosome);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		assertTrue(chromosome.isEvaluationNeeded());
	}

	@Test
	public void TestJoinPointGeneWithNullChromosome() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockGene gene = new MockGene();
		gene.setChromosome(null);

		when(mockJoinPoint.getTarget()).thenReturn(gene);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		/*
		 * No assertions necessary. We just need to ensure that no exception is
		 * thrown
		 */
	}

	@Test
	public void TestJoinPointSequenceNullGene() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockSequence sequence = new MockSequence();
		sequence.setGene(null);

		when(mockJoinPoint.getTarget()).thenReturn(sequence);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		/*
		 * No assertions necessary. We just need to ensure that no exception is
		 * thrown
		 */
	}

	@Test
	public void TestJoinPointSequenceNullChromosome() {
		DirtyAspect dirtyAspect = new DirtyAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockGene gene = new MockGene();
		gene.setChromosome(null);
		MockSequence sequence = new MockSequence();
		sequence.setGene(gene);

		when(mockJoinPoint.getTarget()).thenReturn(sequence);

		try {
			dirtyAspect.beforeMethodsMarkedWithAtDirty(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		/*
		 * No assertions necessary. We just need to ensure that no exception is
		 * thrown
		 */
	}
}
