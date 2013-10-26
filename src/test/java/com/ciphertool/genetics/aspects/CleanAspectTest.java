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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.Test;

import com.ciphertool.genetics.mocks.MockChromosome;

public class CleanAspectTest {
	@Test
	public void TestJoinPoint() {
		CleanAspect cleanAspect = new CleanAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		MockChromosome chromosome = new MockChromosome();
		chromosome.setEvaluationNeeded(true);
		when(mockJoinPoint.getTarget()).thenReturn(chromosome);

		try {
			cleanAspect.afterMethodsMarkedWithAtClean(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		assertFalse(chromosome.isEvaluationNeeded());
	}

	@Test
	public void TestJoinPointNotAnInstanceOfChromosome() {
		CleanAspect cleanAspect = new CleanAspect();
		JoinPoint mockJoinPoint = mock(JoinPoint.class);

		String notAnInstanceOfChromosome = "notAnInstanceOfChromosome";
		when(mockJoinPoint.getTarget()).thenReturn(notAnInstanceOfChromosome);

		try {
			cleanAspect.afterMethodsMarkedWithAtClean(mockJoinPoint);
		} catch (Throwable e) {
			fail(e.getMessage());
		}

		/*
		 * No assertions necessary. We just need to ensure that no exception is
		 * thrown
		 */
	}
}
