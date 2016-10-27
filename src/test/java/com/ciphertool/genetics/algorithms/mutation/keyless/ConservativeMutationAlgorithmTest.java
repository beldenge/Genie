/**
 * Copyright 2015 George Belden
 * 
 * This file is part of Genie.
 * 
 * Genie is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Genie is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Genie. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.mutation.keyless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.mutation.keyless.ConservativeMutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.dao.VariableLengthGeneDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;

public class ConservativeMutationAlgorithmTest {
	private final static int						MAX_MUTATIONS	= 2;
	private static Logger							logMock;
	private static ConservativeMutationAlgorithm	conservativeMutationAlgorithm;
	private static VariableLengthGeneDao			geneDaoMock;

	@BeforeClass
	public static void setUp() {
		conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();

		geneDaoMock = mock(VariableLengthGeneDao.class);
		conservativeMutationAlgorithm.setGeneDao(geneDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(ConservativeMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, conservativeMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
		reset(geneDaoMock);
	}

	@Test
	public void testSetGeneDao() {
		VariableLengthGeneDao geneDaoToSet = mock(VariableLengthGeneDao.class);

		ConservativeMutationAlgorithm conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();
		conservativeMutationAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(ConservativeMutationAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(geneDaoField, conservativeMutationAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		ConservativeMutationAlgorithm conservativeMutationAlgorithm = new ConservativeMutationAlgorithm();
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(ConservativeMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(maxMutationsPerChromosomeField, conservativeMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(null);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		conservativeMutationAlgorithm.mutateChromosome(mockKeylessChromosome);
	}

	@Test
	public void testMutateChromosome() {
		conservativeMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(mockGeneToReturn);

		conservativeMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		verify(geneDaoMock, atLeastOnce()).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verify(geneDaoMock, atMost(2)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(mockGeneToReturn);

		conservativeMutationAlgorithm.mutateGene(mockKeylessChromosome, 0);

		assertNotSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGeneToReturn, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verify(geneDaoMock, times(1)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		conservativeMutationAlgorithm.mutateGene(mockKeylessChromosome, 2);

		assertSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verifyZeroInteractions(geneDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateGeneCannotFindDifferentGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		final MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("a"));
		mockGeneToReturn.getSequences().get(0).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("b"));
		mockGeneToReturn.getSequences().get(1).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("c"));
		mockGeneToReturn.getSequences().get(2).setGene(mockGene1);
		mockGeneToReturn.setChromosome(mockKeylessChromosome);
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenAnswer(new Answer<MockGene>() {
			public MockGene answer(InvocationOnMock invocation) throws Throwable {
				return mockGeneToReturn.clone();
			}
		});

		when(logMock.isDebugEnabled()).thenReturn(true);

		conservativeMutationAlgorithm.mutateGene(mockKeylessChromosome, 0);

		assertSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verify(geneDaoMock, times(1000)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		conservativeMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockKeylessChromosome.getGenes().get(1))
				|| (mockGeneToReturn == mockKeylessChromosome.getGenes().get(0)
						&& mockGene2 == mockKeylessChromosome.getGenes().get(1)));
		assertEquals(1, availableIndices.size());
		assertTrue(availableIndices.get(0) == 0 || availableIndices.get(0) == 1);
		verify(geneDaoMock, times(1)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(1);
		conservativeMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * Only the second Gene should be mutated.
		 */
		assertTrue(mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockKeylessChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verify(geneDaoMock, times(1)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockKeylessChromosome.addGene(mockGene2);

		when(geneDaoMock.findRandomGeneOfLength(any(Chromosome.class), anyInt())).thenReturn(null);

		List<Integer> availableIndices = new ArrayList<Integer>();
		conservativeMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGene2 == mockKeylessChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verifyZeroInteractions(geneDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}
}
