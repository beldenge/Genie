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
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
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

import com.ciphertool.genetics.algorithms.mutation.keyless.LiberalMutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.dao.VariableLengthGeneDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.VariableLengthGene;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.KeylessChromosomeHelper;

public class LiberalMutationAlgorithmTest {
	private final static int				MAX_MUTATIONS	= 2;
	private static Logger					logMock;
	private static LiberalMutationAlgorithm	liberalMutationAlgorithm;
	private static VariableLengthGeneDao	geneDaoMock;
	private static VariableLengthGeneDao	geneDaoMockForChromosomeHelper;
	private static KeylessChromosomeHelper	chromosomeHelperSpy;

	@BeforeClass
	public static void setUp() {
		liberalMutationAlgorithm = new LiberalMutationAlgorithm();

		geneDaoMock = mock(VariableLengthGeneDao.class);
		liberalMutationAlgorithm.setGeneDao(geneDaoMock);
		chromosomeHelperSpy = spy(new KeylessChromosomeHelper());
		geneDaoMockForChromosomeHelper = mock(VariableLengthGeneDao.class);
		liberalMutationAlgorithm.setChromosomeHelper(chromosomeHelperSpy);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(LiberalMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, liberalMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(geneDaoMock);
		reset(geneDaoMockForChromosomeHelper);
		reset(logMock);
		reset(chromosomeHelperSpy);

		chromosomeHelperSpy.setGeneDao(geneDaoMockForChromosomeHelper);
	}

	@Test
	public void testSetGeneDao() {
		GeneDao geneDaoToSet = mock(GeneDao.class);

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(LiberalMutationAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(geneDaoField, liberalMutationAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetChromosomeHelper() {
		KeylessChromosomeHelper chromosomeHelperToSet = new KeylessChromosomeHelper();

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setChromosomeHelper(chromosomeHelperToSet);

		Field chromosomeHelperField = ReflectionUtils.findField(LiberalMutationAlgorithm.class, "keylessChromosomeHelper");
		ReflectionUtils.makeAccessible(chromosomeHelperField);
		KeylessChromosomeHelper chromosomeHelperFromObject = (KeylessChromosomeHelper) ReflectionUtils.getField(chromosomeHelperField, liberalMutationAlgorithm);

		assertSame(chromosomeHelperToSet, chromosomeHelperFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(LiberalMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(maxMutationsPerChromosomeField, liberalMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(null);

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

		liberalMutationAlgorithm.mutateChromosome(mockKeylessChromosome);
	}

	@Test
	public void testMutateChromosome() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);
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
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn.clone(), mockGeneToReturn.clone());

		liberalMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(2)).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateChromosomeGreaterThanTargetSize() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);
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
		mockGeneToReturn.addSequence(new MockSequence("w"));
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn.clone(), mockGeneToReturn.clone());

		assertEquals(3, mockGene1.size());
		assertEquals(3, mockGene2.size());
		assertEquals(4, mockGeneToReturn.size());

		liberalMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertEquals(2, mockKeylessChromosome.getGenes().size());
		assertEquals(new Integer(6), mockKeylessChromosome.actualSize());
		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(2)).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateChromosomeLessThanTargetSize() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);
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
		mockGeneToReturn.addSequence(new MockSequence("w"));
		mockGeneToReturn.addSequence(new MockSequence("x"));
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn.clone(), mockGeneToReturn.clone());

		MockGene fillerGeneToReturn = new MockGene();
		fillerGeneToReturn.addSequence(new MockSequence("y"));
		fillerGeneToReturn.addSequence(new MockSequence("z"));
		when(geneDaoMockForChromosomeHelper.findRandomGene(same(mockKeylessChromosome))).thenReturn(fillerGeneToReturn.clone(), fillerGeneToReturn.clone());

		assertEquals(2, mockKeylessChromosome.getGenes().size());

		liberalMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertTrue(mockKeylessChromosome.getGenes().size() >= 3);
		assertEquals(new Integer(6), mockKeylessChromosome.actualSize());
		// The last Sequence(s) should always be from the
		// KeylessChromosomeHelper
		assertEquals("y", ((VariableLengthGene) mockKeylessChromosome.getGenes().get(2)).getSequences().get(0).getValue());
		verify(geneDaoMock, atLeast(1)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(2)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMockForChromosomeHelper, atLeast(1)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMockForChromosomeHelper, atMost(2)).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn);

		liberalMutationAlgorithm.mutateGene(mockKeylessChromosome, 0);

		assertNotSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGeneToReturn, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verify(geneDaoMock, times(1)).findRandomGene(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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

		liberalMutationAlgorithm.mutateGene(mockKeylessChromosome, 5);

		assertSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verifyZeroInteractions(geneDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateGeneCannotFindDifferentGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenAnswer(new Answer<MockGene>() {
			public MockGene answer(InvocationOnMock invocation) throws Throwable {
				return mockGeneToReturn.clone();
			}
		});

		when(logMock.isDebugEnabled()).thenReturn(true);

		liberalMutationAlgorithm.mutateGene(mockKeylessChromosome, 0);

		assertSame(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertSame(mockGene2, mockKeylessChromosome.getGenes().get(1));
		verify(geneDaoMock, times(1000)).findRandomGene(same(mockKeylessChromosome));
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		liberalMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockKeylessChromosome.getGenes().get(1))
				|| (mockGeneToReturn == mockKeylessChromosome.getGenes().get(0)
						&& mockGene2 == mockKeylessChromosome.getGenes().get(1)));
		assertEquals(1, availableIndices.size());
		assertTrue(availableIndices.get(0) == 0 || availableIndices.get(0) == 1);
		verify(geneDaoMock, times(1)).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, times(1)).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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
		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(1);
		liberalMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * Only the second Gene should be mutated.
		 */
		assertTrue(mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockKeylessChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verify(geneDaoMock, times(1)).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, times(1)).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(6);

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

		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(null);

		List<Integer> availableIndices = new ArrayList<Integer>();
		liberalMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(mockGene1 == mockKeylessChromosome.getGenes().get(0)
				&& mockGene2 == mockKeylessChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verifyZeroInteractions(geneDaoMock);
		verify(chromosomeHelperSpy, never()).resizeChromosome(same(mockKeylessChromosome));
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneBoundaryConditions() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		mockKeylessChromosome.setTargetSize(2);
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("1"));
		mockGeneToReturn.addSequence(new MockSequence("2"));

		List<Integer> availableIndices = new ArrayList<Integer>();
		do {
			mockKeylessChromosome.setFitness(0.0);
			while (!mockKeylessChromosome.getGenes().isEmpty()) {
				mockKeylessChromosome.removeGene(mockKeylessChromosome.getGenes().size() - 1);
			}
			mockKeylessChromosome.addGene(mockGene1);
			mockKeylessChromosome.addGene(mockGene2);
			availableIndices.clear();
			availableIndices.add(0);
			availableIndices.add(1);
			when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(mockGeneToReturn.clone());
			liberalMutationAlgorithm.mutateRandomGene(mockKeylessChromosome, availableIndices);

			/*
			 * If the availableIndices List does not contain the last element, we need to repeat, because that does not
			 * trigger the specific scenario we are testing for.
			 */
		} while (availableIndices.size() > 0 && (availableIndices.get(availableIndices.size() - 1) == 0));

		/*
		 * Only one Gene should be mutated.
		 */
		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertEquals(0, availableIndices.size());
		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}
}
