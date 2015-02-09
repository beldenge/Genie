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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.ComplexGene;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.ChromosomeHelper;

public class LiberalMutationAlgorithmTest {
	private final static int MAX_MUTATIONS = 2;
	private static Logger logMock;
	private static LiberalMutationAlgorithm liberalMutationAlgorithm;
	private static GeneListDao geneListDaoMock;
	private static GeneListDao geneListDaoMockForChromosomeHelper;
	private static ChromosomeHelper chromosomeHelperSpy;

	@BeforeClass
	public static void setUp() {
		liberalMutationAlgorithm = new LiberalMutationAlgorithm();

		geneListDaoMock = mock(GeneListDao.class);
		liberalMutationAlgorithm.setGeneListDao(geneListDaoMock);
		chromosomeHelperSpy = spy(new ChromosomeHelper());
		geneListDaoMockForChromosomeHelper = mock(GeneListDao.class);
		liberalMutationAlgorithm.setChromosomeHelper(chromosomeHelperSpy);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(LiberalMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, liberalMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(geneListDaoMock);
		reset(geneListDaoMockForChromosomeHelper);
		reset(logMock);
		reset(chromosomeHelperSpy);

		chromosomeHelperSpy.setGeneListDao(geneListDaoMockForChromosomeHelper);
	}

	@Test
	public void testSetGeneListDao() {
		GeneListDao geneListDaoToSet = mock(GeneListDao.class);

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setGeneListDao(geneListDaoToSet);

		Field geneListDaoField = ReflectionUtils.findField(LiberalMutationAlgorithm.class,
				"geneListDao");
		ReflectionUtils.makeAccessible(geneListDaoField);
		GeneListDao geneListDaoFromObject = (GeneListDao) ReflectionUtils.getField(
				geneListDaoField, liberalMutationAlgorithm);

		assertSame(geneListDaoToSet, geneListDaoFromObject);
	}

	@Test
	public void testSetChromosomeHelper() {
		ChromosomeHelper chromosomeHelperToSet = new ChromosomeHelper();

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setChromosomeHelper(chromosomeHelperToSet);

		Field chromosomeHelperField = ReflectionUtils.findField(LiberalMutationAlgorithm.class,
				"chromosomeHelper");
		ReflectionUtils.makeAccessible(chromosomeHelperField);
		ChromosomeHelper chromosomeHelperFromObject = (ChromosomeHelper) ReflectionUtils.getField(
				chromosomeHelperField, liberalMutationAlgorithm);

		assertSame(chromosomeHelperToSet, chromosomeHelperFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		LiberalMutationAlgorithm liberalMutationAlgorithm = new LiberalMutationAlgorithm();
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(
				LiberalMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(
				maxMutationsPerChromosomeField, liberalMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(null);

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

		liberalMutationAlgorithm.mutateChromosome(mockChromosome);
	}

	@Test
	public void testMutateChromosome() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenReturn(
				mockGeneToReturn.clone(), mockGeneToReturn.clone());

		liberalMutationAlgorithm.mutateChromosome(mockChromosome);

		assertFalse(originalGenes.equals(mockChromosome.getGenes()));
		verify(geneListDaoMock, atLeastOnce()).findRandomGene(same(mockChromosome));
		verify(geneListDaoMock, atMost(2)).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateChromosomeGreaterThanTargetSize() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("w"));
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenReturn(
				mockGeneToReturn.clone(), mockGeneToReturn.clone());

		assertEquals(3, mockGene1.size());
		assertEquals(3, mockGene2.size());
		assertEquals(4, mockGeneToReturn.size());

		liberalMutationAlgorithm.mutateChromosome(mockChromosome);

		assertFalse(originalGenes.equals(mockChromosome.getGenes()));
		assertEquals(2, mockChromosome.getGenes().size());
		assertEquals(new Integer(6), mockChromosome.actualSize());
		verify(geneListDaoMock, atLeastOnce()).findRandomGene(same(mockChromosome));
		verify(geneListDaoMock, atMost(2)).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateChromosomeLessThanTargetSize() {
		liberalMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("a"));
		mockGene1.addSequence(new MockSequence("b"));
		mockGene1.addSequence(new MockSequence("c"));
		mockChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("1"));
		mockGene2.addSequence(new MockSequence("2"));
		mockGene2.addSequence(new MockSequence("3"));
		mockChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("w"));
		mockGeneToReturn.addSequence(new MockSequence("x"));
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenReturn(
				mockGeneToReturn.clone(), mockGeneToReturn.clone());

		MockGene fillerGeneToReturn = new MockGene();
		fillerGeneToReturn.addSequence(new MockSequence("y"));
		fillerGeneToReturn.addSequence(new MockSequence("z"));
		when(geneListDaoMockForChromosomeHelper.findRandomGene(same(mockChromosome))).thenReturn(
				fillerGeneToReturn.clone(), fillerGeneToReturn.clone());

		assertEquals(2, mockChromosome.getGenes().size());

		liberalMutationAlgorithm.mutateChromosome(mockChromosome);

		assertFalse(originalGenes.equals(mockChromosome.getGenes()));
		assertTrue(mockChromosome.getGenes().size() >= 3);
		assertEquals(new Integer(6), mockChromosome.actualSize());
		// The last Sequence(s) should always be from the ChromosomeHelper
		assertEquals("y", ((ComplexGene) mockChromosome.getGenes().get(2)).getSequences().get(0).getValue());
		verify(geneListDaoMock, atLeast(1)).findRandomGene(same(mockChromosome));
		verify(geneListDaoMock, atMost(2)).findRandomGene(same(mockChromosome));
		verify(geneListDaoMockForChromosomeHelper, atLeast(1)).findRandomGene(same(mockChromosome));
		verify(geneListDaoMockForChromosomeHelper, atMost(2)).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockChromosome));
		verify(chromosomeHelperSpy, atMost(2)).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenReturn(mockGeneToReturn);

		liberalMutationAlgorithm.mutateGene(mockChromosome, 0);

		assertNotSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGeneToReturn, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verify(geneListDaoMock, times(1)).findRandomGene(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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

		liberalMutationAlgorithm.mutateGene(mockChromosome, 5);

		assertSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verifyZeroInteractions(geneListDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateGeneCannotFindDifferentGene() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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

		final MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("a"));
		mockGeneToReturn.getSequences().get(0).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("b"));
		mockGeneToReturn.getSequences().get(1).setGene(mockGene1);
		mockGeneToReturn.addSequence(new MockSequence("c"));
		mockGeneToReturn.getSequences().get(2).setGene(mockGene1);
		mockGeneToReturn.setChromosome(mockChromosome);
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenAnswer(
				new Answer<MockGene>() {
					public MockGene answer(InvocationOnMock invocation) throws Throwable {
						return mockGeneToReturn.clone();
					}
				});

		when(logMock.isDebugEnabled()).thenReturn(true);

		liberalMutationAlgorithm.mutateGene(mockChromosome, 0);

		assertSame(mockGene1, mockChromosome.getGenes().get(0));
		assertSame(mockGene2, mockChromosome.getGenes().get(1));
		verify(geneListDaoMock, times(1000)).findRandomGene(same(mockChromosome));
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		liberalMutationAlgorithm.mutateRandomGene(mockChromosome, availableIndices);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((mockGene1 == mockChromosome.getGenes().get(0) && mockGeneToReturn == mockChromosome
				.getGenes().get(1))
				|| (mockGeneToReturn == mockChromosome.getGenes().get(0) && mockGene2 == mockChromosome
						.getGenes().get(1)));
		assertEquals(1, availableIndices.size());
		assertTrue(availableIndices.get(0) == 0 || availableIndices.get(0) == 1);
		verify(geneListDaoMock, times(1)).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, times(1)).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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
		when(geneListDaoMock.findRandomGene(same(mockChromosome))).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(1);
		liberalMutationAlgorithm.mutateRandomGene(mockChromosome, availableIndices);

		/*
		 * Only the second Gene should be mutated.
		 */
		assertTrue(mockGene1 == mockChromosome.getGenes().get(0)
				&& mockGeneToReturn == mockChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verify(geneListDaoMock, times(1)).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, times(1)).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(6);

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

		when(geneListDaoMock.findRandomGeneOfLength(same(mockChromosome), anyInt())).thenReturn(
				null);

		List<Integer> availableIndices = new ArrayList<Integer>();
		liberalMutationAlgorithm.mutateRandomGene(mockChromosome, availableIndices);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(mockGene1 == mockChromosome.getGenes().get(0)
				&& mockGene2 == mockChromosome.getGenes().get(1));
		assertTrue(availableIndices.isEmpty());
		verifyZeroInteractions(geneListDaoMock);
		verify(chromosomeHelperSpy, never()).resizeChromosome(same(mockChromosome));
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneBoundaryConditions() {
		MockChromosome mockChromosome = new MockChromosome();
		mockChromosome.setTargetSize(2);
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
			mockChromosome.setFitness(0.0);
			while (!mockChromosome.getGenes().isEmpty()) {
				mockChromosome.removeGene(mockChromosome.getGenes().size() - 1);
			}
			mockChromosome.addGene(mockGene1);
			mockChromosome.addGene(mockGene2);
			availableIndices.clear();
			availableIndices.add(0);
			availableIndices.add(1);
			when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(
					mockGeneToReturn.clone());
			liberalMutationAlgorithm.mutateRandomGene(mockChromosome, availableIndices);

			/*
			 * If the availableIndices List does not contain the last element,
			 * we need to repeat, because that does not trigger the specific
			 * scenario we are testing for.
			 */
		} while (availableIndices.size() > 0
				&& (availableIndices.get(availableIndices.size() - 1) == 0));

		/*
		 * Only one Gene should be mutated.
		 */
		assertFalse(originalGenes.equals(mockChromosome.getGenes()));
		assertEquals(0, availableIndices.size());
		verify(geneListDaoMock, atLeastOnce()).findRandomGene(same(mockChromosome));
		verify(chromosomeHelperSpy, atLeastOnce()).resizeChromosome(same(mockChromosome));
		verifyZeroInteractions(logMock);
	}
}
