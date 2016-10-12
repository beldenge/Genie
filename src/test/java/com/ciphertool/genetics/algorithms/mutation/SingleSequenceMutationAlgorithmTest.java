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

package com.ciphertool.genetics.algorithms.mutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.SequenceDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class SingleSequenceMutationAlgorithmTest {
	private final static int MAX_MUTATIONS = 2;
	private static Logger logMock;
	private static SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm;
	private static SequenceDao sequenceDaoMock;

	@BeforeClass
	public static void setUp() {
		singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();

		sequenceDaoMock = mock(SequenceDao.class);
		singleSequenceMutationAlgorithm.setSequenceDao(sequenceDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(SingleSequenceMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, singleSequenceMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
		reset(sequenceDaoMock);
	}

	@Test
	public void testSetSequenceDao() {
		SequenceDao sequenceDaoToSet = mock(SequenceDao.class);

		SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();
		singleSequenceMutationAlgorithm.setSequenceDao(sequenceDaoToSet);

		Field sequenceDaoField = ReflectionUtils.findField(SingleSequenceMutationAlgorithm.class, "sequenceDao");
		ReflectionUtils.makeAccessible(sequenceDaoField);
		SequenceDao sequenceDaoFromObject = (SequenceDao) ReflectionUtils.getField(sequenceDaoField,
				singleSequenceMutationAlgorithm);

		assertSame(sequenceDaoToSet, sequenceDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();
		singleSequenceMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(SingleSequenceMutationAlgorithm.class,
				"maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(
				maxMutationsPerChromosomeField, singleSequenceMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		singleSequenceMutationAlgorithm.setMaxMutationsPerChromosome(null);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);

		singleSequenceMutationAlgorithm.mutateChromosome(mockKeylessChromosome);
	}

	@Test
	public void testMutateChromosome() {
		singleSequenceMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockKeylessChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome));
		verify(sequenceDaoMock, atLeastOnce()).findRandomSequence(any(Gene.class), anyInt());
		verify(sequenceDaoMock, atMost(2)).findRandomSequence(any(Gene.class), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateGene(mockKeylessChromosome, 0);

		// Only one of the letters from the first Gene should be changed
		assertTrue(("w".equals(mockGene1.getSequences().get(0).getValue()) && !"e".equals(mockGene1.getSequences().get(
				1).getValue()))
				|| (!"w".equals(mockGene1.getSequences().get(0).getValue()) && "e".equals(mockGene1.getSequences().get(
						1).getValue())));
		assertTrue("s".equals(mockGene2.getSequences().get(0).getValue())
				&& "m".equals(mockGene2.getSequences().get(1).getValue())
				&& "i".equals(mockGene2.getSequences().get(2).getValue())
				&& "l".equals(mockGene2.getSequences().get(3).getValue())
				&& "e".equals(mockGene2.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene1), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene);

		singleSequenceMutationAlgorithm.mutateGene(mockKeylessChromosome, 1);

		// Nothing should be changed
		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		verifyZeroInteractions(sequenceDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(new MockSequence("x"));

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		singleSequenceMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockKeylessChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockKeylessChromosome);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((originalMockGene1.equals(mockGene1) && !originalMockGene2.equals(mockGene2))
				|| (!originalMockGene1.equals(mockGene1) && originalMockGene2.equals(mockGene2)));
		assertEquals(1, availableIndices.size());
		assertTrue(availableIndices.get(0) == 0 || availableIndices.get(0) == 1);
		verify(sequenceDaoMock, times(1)).findRandomSequence(any(Gene.class), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(new MockSequence("x"));

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(1);
		singleSequenceMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockKeylessChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockKeylessChromosome);

		/*
		 * Only one Gene should be mutated.
		 */
		assertEquals(originalMockGene1, mockGene1);
		assertFalse(originalMockGene2.equals(mockGene2));
		assertTrue(availableIndices.isEmpty());
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene2), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene2);

		List<Integer> availableIndices = new ArrayList<Integer>();
		singleSequenceMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockKeylessChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockKeylessChromosome);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(originalMockGene1.equals(mockGene1) && originalMockGene2.equals(mockGene2));
		assertTrue(availableIndices.isEmpty());
		verifyZeroInteractions(sequenceDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenReturn(new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 4);

		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertFalse("e".equals(mockGene.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 5);

		// No sequences should be changed
		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateSequenceCannotFindDifferentSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		MockSequence sequenceToReplace = new MockSequence("e");
		mockGene.addSequence(sequenceToReplace);

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenAnswer(new Answer<MockSequence>() {
			public MockSequence answer(InvocationOnMock invocation) throws Throwable {
				return new MockSequence("e");
			}
		});
		when(logMock.isDebugEnabled()).thenReturn(true);

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 4);

		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		assertSame(sequenceToReplace, mockGene.getSequences().get(4));
		verify(sequenceDaoMock, times(1000)).findRandomSequence(same(mockGene), anyInt());
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenReturn(new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateRandomSequence(mockGene);

		assertFalse("s".equals(mockGene.getSequences().get(0).getValue())
				&& "m".equals(mockGene.getSequences().get(1).getValue())
				&& "i".equals(mockGene.getSequences().get(2).getValue())
				&& "l".equals(mockGene.getSequences().get(3).getValue())
				&& "e".equals(mockGene.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene), anyInt());
		verifyZeroInteractions(logMock);
	}
}
