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

package com.ciphertool.genetics.algorithms.mutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class ConservativeMutationAlgorithmTest {
	private final static int MAX_MUTATIONS = 1;
	private static Logger logMock;
	private static ConservativeMutationAlgorithm conservativeMutationAlgorithm;
	private static GeneListDao geneListDaoMock;

	@BeforeClass
	public static void setUp() {
		conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();

		geneListDaoMock = mock(GeneListDao.class);
		conservativeMutationAlgorithm.setGeneListDao(geneListDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(ConservativeMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, conservativeMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		reset(logMock);
	}

	@Test
	public void testSetGeneListDao() {
		GeneListDao geneListDaoToSet = mock(GeneListDao.class);

		ConservativeMutationAlgorithm conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();
		conservativeMutationAlgorithm.setGeneListDao(geneListDaoToSet);

		Field geneListDaoField = ReflectionUtils.findField(ConservativeMutationAlgorithm.class,
				"geneListDao");
		ReflectionUtils.makeAccessible(geneListDaoField);
		GeneListDao geneListDaoFromObject = (GeneListDao) ReflectionUtils.getField(
				geneListDaoField, conservativeMutationAlgorithm);

		assertSame(geneListDaoToSet, geneListDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		ConservativeMutationAlgorithm conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(
				ConservativeMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(
				maxMutationsPerChromosomeField, conservativeMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(null);

		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		conservativeMutationAlgorithm.mutateChromosome(mockChromosome);
	}

	@Test
	public void testMutateChromosome() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				mockGeneToReturn);

		conservativeMutationAlgorithm.mutateChromosome(mockChromosome);

		assertTrue((mockGene1 == mockChromosome.getGenes().get(0) && mockGeneToReturn == mockChromosome
				.getGenes().get(1))
				|| (mockGeneToReturn == mockChromosome.getGenes().get(0) && mockGene2 == mockChromosome
						.getGenes().get(1)));
		verify(geneListDaoMock, times(1)).findRandomGeneOfLength(same(mockChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				mockGeneToReturn);

		conservativeMutationAlgorithm.mutateGene(mockChromosome, 0);

		assertNotSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGeneToReturn, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verify(geneListDaoMock, times(1)).findRandomGeneOfLength(same(mockChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		conservativeMutationAlgorithm.mutateGene(mockChromosome, 2);

		assertSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verifyZeroInteractions(geneListDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateGeneCannotFindDifferentGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("a"));
		mockGeneToReturn.getSequences().get(0).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("b"));
		mockGeneToReturn.getSequences().get(1).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("c"));
		mockGeneToReturn.getSequences().get(2).setGene(mockGene1);
		mockGeneToReturn.setChromosome(mockChromosome);
		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				mockGeneToReturn);

		when(logMock.isDebugEnabled()).thenReturn(true);

		conservativeMutationAlgorithm.mutateGene(mockChromosome, 0);

		assertSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verify(geneListDaoMock, times(1000)).findRandomGeneOfLength(same(mockChromosome), anyInt());
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				mockGeneToReturn);

		Integer mutatedIndex = conservativeMutationAlgorithm.mutateRandomGene(mockChromosome,
				Arrays.asList(0, 1));

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((mockGene1 == mockChromosome.getGenes().get(0) && mockGeneToReturn == mockChromosome
				.getGenes().get(1))
				|| (mockGeneToReturn == mockChromosome.getGenes().get(0) && mockGene2 == mockChromosome
						.getGenes().get(1)));
		assertTrue(mutatedIndex == 0 || mutatedIndex == 1);
		verify(geneListDaoMock, times(1)).findRandomGeneOfLength(same(mockChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				mockGeneToReturn);

		Integer mutatedIndex = conservativeMutationAlgorithm.mutateRandomGene(mockChromosome,
				Arrays.asList(1));

		/*
		 * Only the second Gene should be mutated.
		 */
		assertTrue(mockGene1 == mockChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockChromosome.getGenes().get(1));
		assertEquals(mutatedIndex, new Integer(1));
		verify(geneListDaoMock, times(1)).findRandomGeneOfLength(same(mockChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);

		when(geneListDaoMock.findRandomGeneOfLength(any(Chromosome.class), anyInt())).thenReturn(
				null);

		Integer mutatedIndex = conservativeMutationAlgorithm.mutateRandomGene(mockChromosome,
				new ArrayList<Integer>());

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(mockGene1 == mockChromosome.getGenes().get(0)
				&& mockGene2 == mockChromosome.getGenes().get(1));
		assertNull(mutatedIndex);
		verifyZeroInteractions(geneListDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}
}
