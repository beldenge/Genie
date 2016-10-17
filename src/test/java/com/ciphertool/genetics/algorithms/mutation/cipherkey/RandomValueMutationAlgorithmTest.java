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

package com.ciphertool.genetics.algorithms.mutation.cipherkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.dao.VariableLengthGeneDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeyedChromosome;

public class RandomValueMutationAlgorithmTest {
	private final static int					MAX_MUTATIONS	= 2;
	private static Logger						logMock;
	private static RandomValueMutationAlgorithm	randomValueMutationAlgorithm;
	private static GeneDao						geneDaoMock;

	@BeforeClass
	public static void setUp() {
		randomValueMutationAlgorithm = new RandomValueMutationAlgorithm();

		geneDaoMock = mock(GeneDao.class);
		randomValueMutationAlgorithm.setGeneDao(geneDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(RandomValueMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, randomValueMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(logMock);
		reset(geneDaoMock);
	}

	@Test
	public void testSetGeneDao() {
		VariableLengthGeneDao geneDaoToSet = mock(VariableLengthGeneDao.class);

		RandomValueMutationAlgorithm randomValueMutationAlgorithm = new RandomValueMutationAlgorithm();
		randomValueMutationAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(RandomValueMutationAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(geneDaoField, randomValueMutationAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		RandomValueMutationAlgorithm randomValueMutationAlgorithm = new RandomValueMutationAlgorithm();
		randomValueMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(RandomValueMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(maxMutationsPerChromosomeField, randomValueMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		randomValueMutationAlgorithm.setMaxMutationsPerChromosome(null);

		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();

		MockGene mockGene1 = new MockGene();
		mockKeyedChromosome.putGene("1", mockGene1);

		MockGene mockGene2 = new MockGene();
		mockKeyedChromosome.putGene("2", mockGene2);

		randomValueMutationAlgorithm.mutateChromosome(mockKeyedChromosome);
	}

	@Test
	public void testMutateChromosome() {
		randomValueMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();
		List<Gene> originalGenes = new ArrayList<Gene>();

		MockGene mockGene1 = new MockGene();
		mockKeyedChromosome.putGene("1", mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockKeyedChromosome.putGene("2", mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		when(geneDaoMock.findRandomGene(same(mockKeyedChromosome))).thenReturn(mockGeneToReturn);

		randomValueMutationAlgorithm.mutateChromosome(mockKeyedChromosome);

		assertFalse(originalGenes.equals(mockKeyedChromosome.getGenes()));
		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeyedChromosome));
		verify(geneDaoMock, atMost(2)).findRandomGene(same(mockKeyedChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();

		MockGene mockGene1 = new MockGene();
		mockKeyedChromosome.putGene("1", mockGene1);

		MockGene mockGene2 = new MockGene();
		mockKeyedChromosome.putGene("2", mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		when(geneDaoMock.findRandomGene(same(mockKeyedChromosome))).thenReturn(mockGeneToReturn);

		Set<Object> availableIndices = new HashSet<Object>();
		availableIndices.add("1");
		availableIndices.add("2");
		randomValueMutationAlgorithm.mutateRandomGene(mockKeyedChromosome, availableIndices);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((mockGene1 == mockKeyedChromosome.getGenes().get("1")
				&& mockGeneToReturn == mockKeyedChromosome.getGenes().get("2"))
				|| (mockGeneToReturn == mockKeyedChromosome.getGenes().get("1")
						&& mockGene2 == mockKeyedChromosome.getGenes().get("2")));
		assertEquals(1, availableIndices.size());
		assertTrue(availableIndices.toArray()[0] == "1" || availableIndices.toArray()[0] == "2");
		verify(geneDaoMock, times(1)).findRandomGene(same(mockKeyedChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();

		MockGene mockGene1 = new MockGene();
		mockKeyedChromosome.putGene("1", mockGene1);

		MockGene mockGene2 = new MockGene();
		mockKeyedChromosome.putGene("2", mockGene2);

		MockGene mockGeneToReturn = new MockGene();
		when(geneDaoMock.findRandomGene(same(mockKeyedChromosome))).thenReturn(mockGeneToReturn);

		Set<Object> availableIndices = new HashSet<Object>();
		availableIndices.add("2");
		randomValueMutationAlgorithm.mutateRandomGene(mockKeyedChromosome, availableIndices);

		/*
		 * Only the second Gene should be mutated.
		 */
		assertTrue(mockGene1 == mockKeyedChromosome.getGenes().get("1")
				&& mockGeneToReturn == mockKeyedChromosome.getGenes().get("2"));
		assertTrue(availableIndices.isEmpty());
		verify(geneDaoMock, times(1)).findRandomGene(same(mockKeyedChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockKeyedChromosome mockKeyedChromosome = new MockKeyedChromosome();

		MockGene mockGene1 = new MockGene();
		mockKeyedChromosome.putGene("1", mockGene1);

		MockGene mockGene2 = new MockGene();
		mockKeyedChromosome.putGene("2", mockGene2);

		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(null);

		Set<Object> availableIndices = new HashSet<Object>();
		randomValueMutationAlgorithm.mutateRandomGene(mockKeyedChromosome, availableIndices);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(mockGene1 == mockKeyedChromosome.getGenes().get("1")
				&& mockGene2 == mockKeyedChromosome.getGenes().get("2"));
		assertTrue(availableIndices.isEmpty());
		verifyZeroInteractions(geneDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}
}
