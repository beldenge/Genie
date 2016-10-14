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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.dao.VariableLengthGeneDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.VariableLengthGene;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;

public class GroupMutationAlgorithmTest {
	private final static int MAX_MUTATIONS = 2;
	private static Logger logMock;
	private static GroupMutationAlgorithm groupMutationAlgorithm;
	private static VariableLengthGeneDao geneDaoMock;

	@BeforeClass
	public static void setUp() throws IllegalArgumentException, IllegalAccessException {
		Field maxGenesPerGroupField = ReflectionUtils.findField(GroupMutationAlgorithm.class, "MAX_GENES_PER_GROUP");
		ReflectionUtils.makeAccessible(maxGenesPerGroupField);
		ReflectionUtils.setField(maxGenesPerGroupField, null, 2);

		groupMutationAlgorithm = new GroupMutationAlgorithm();

		geneDaoMock = mock(VariableLengthGeneDao.class);
		groupMutationAlgorithm.setGeneDao(geneDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(GroupMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, groupMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		reset(geneDaoMock);
		reset(logMock);
	}

	@Test
	public void testSetGeneDao() {
		VariableLengthGeneDao geneDaoToSet = mock(VariableLengthGeneDao.class);

		GroupMutationAlgorithm groupMutationAlgorithm = new GroupMutationAlgorithm();
		groupMutationAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(GroupMutationAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(geneDaoField, groupMutationAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		GroupMutationAlgorithm groupMutationAlgorithm = new GroupMutationAlgorithm();
		groupMutationAlgorithm.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(GroupMutationAlgorithm.class,
				"maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(
				maxMutationsPerChromosomeField, groupMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		groupMutationAlgorithm.setMaxMutationsPerChromosome(null);

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

		groupMutationAlgorithm.mutateChromosome(mockKeylessChromosome);
	}

	@Test
	public void testMutateChromosome() {
		groupMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<MockGene> originalGenes = new ArrayList<>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);
		originalGenes.add(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("r"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("l"));
		mockGene4.addSequence(new MockSequence("l"));
		mockGene4.addSequence(new MockSequence("y"));
		mockKeylessChromosome.addGene(mockGene4);
		originalGenes.add(mockGene4);

		MockGene mockGene5 = new MockGene();
		mockGene5.addSequence(new MockSequence("j"));
		mockGene5.addSequence(new MockSequence("u"));
		mockGene5.addSequence(new MockSequence("s"));
		mockGene5.addSequence(new MockSequence("t"));
		mockKeylessChromosome.addGene(mockGene5);
		originalGenes.add(mockGene5);

		MockGene mockGene6 = new MockGene();
		mockGene6.addSequence(new MockSequence("a"));
		mockGene6.addSequence(new MockSequence("w"));
		mockGene6.addSequence(new MockSequence("e"));
		mockGene6.addSequence(new MockSequence("s"));
		mockGene6.addSequence(new MockSequence("o"));
		mockGene6.addSequence(new MockSequence("m"));
		mockGene6.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene6);
		originalGenes.add(mockGene6);

		final MockGene mockGeneOfSize3 = new MockGene();
		mockGeneOfSize3.addSequence(new MockSequence("x"));
		mockGeneOfSize3.addSequence(new MockSequence("y"));
		mockGeneOfSize3.addSequence(new MockSequence("z"));

		MockGene mockGeneOfSize2 = new MockGene();
		mockGeneOfSize2.addSequence(new MockSequence("v"));
		mockGeneOfSize2.addSequence(new MockSequence("w"));

		MockGene mockGeneOfSize1 = new MockGene();
		mockGeneOfSize1.addSequence(new MockSequence("u"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenAnswer(new Answer<MockGene>() {
			public MockGene answer(InvocationOnMock invocation) throws Throwable {
				return mockGeneOfSize3.clone();
			}
		});
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), eq(2)))
				.thenReturn(mockGeneOfSize2.clone());
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), eq(1)))
				.thenReturn(mockGeneOfSize1.clone());

		groupMutationAlgorithm.mutateChromosome(mockKeylessChromosome);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertEquals(31, mockKeylessChromosome.actualSize().intValue());

		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(2)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneGroup() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<MockGene> originalGenes = new ArrayList<>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);
		originalGenes.add(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);
		originalGenes.add(mockGene4);

		final MockGene mockGeneOfSize3 = new MockGene();
		mockGeneOfSize3.addSequence(new MockSequence("x"));
		mockGeneOfSize3.addSequence(new MockSequence("y"));
		mockGeneOfSize3.addSequence(new MockSequence("z"));

		MockGene mockGeneOfSize2 = new MockGene();
		mockGeneOfSize2.addSequence(new MockSequence("v"));
		mockGeneOfSize2.addSequence(new MockSequence("w"));

		MockGene mockGeneOfSize1 = new MockGene();
		mockGeneOfSize1.addSequence(new MockSequence("u"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenAnswer(new Answer<MockGene>() {
			public MockGene answer(InvocationOnMock invocation) throws Throwable {
				return mockGeneOfSize3.clone();
			}
		});
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), eq(2))).thenReturn(mockGeneOfSize2);
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), eq(1))).thenReturn(mockGeneOfSize1);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		availableIndices.add(2);
		availableIndices.add(3);
		int originalAvailableIndicesSize = availableIndices.size();
		groupMutationAlgorithm.mutateRandomGeneGroup(mockKeylessChromosome, availableIndices, 2);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertTrue(originalAvailableIndicesSize > availableIndices.size());
		assertEquals(21, mockKeylessChromosome.actualSize().intValue());

		int indicesRemoved = 0;
		for (Integer availableIndex : availableIndices) {
			assertTrue(originalGenes.contains(mockKeylessChromosome.getGenes().get(availableIndex - indicesRemoved)));
			mockKeylessChromosome.removeGene(availableIndex - indicesRemoved);
			indicesRemoved++;
		}

		assertTrue(mockKeylessChromosome.getGenes().size() > 0);
		int mockChromosomeSizeMinusOne = mockKeylessChromosome.getGenes().size() - 1;
		for (int i = 0; i < mockChromosomeSizeMinusOne; i++) {
			assertEquals(mockGeneOfSize3, mockKeylessChromosome.getGenes().get(i));
		}

		Gene lastGene = mockKeylessChromosome.getGenes().get(mockKeylessChromosome.getGenes().size() - 1);
		switch (((VariableLengthGene) lastGene).size()) {
		case 3:
			assertEquals(mockGeneOfSize3, lastGene);
			break;
		case 2:
			assertEquals(mockGeneOfSize2, lastGene);
			break;
		case 1:
			assertEquals(mockGeneOfSize1, lastGene);
			break;
		}

		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(4)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(1)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneGroupWithUsedIndex() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<MockGene> originalGenes = new ArrayList<>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);
		originalGenes.add(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);
		originalGenes.add(mockGene4);

		MockGene mockGeneToReturn = new MockGene();
		mockGeneToReturn.addSequence(new MockSequence("x"));
		mockGeneToReturn.addSequence(new MockSequence("y"));
		mockGeneToReturn.addSequence(new MockSequence("z"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneToReturn);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		int originalAvailableIndicesSize = availableIndices.size();
		groupMutationAlgorithm.mutateRandomGeneGroup(mockKeylessChromosome, availableIndices, 2);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertTrue(originalAvailableIndicesSize > availableIndices.size());
		assertEquals(21, mockKeylessChromosome.actualSize().intValue());

		int indicesRemoved = 0;
		for (Integer availableIndex : availableIndices) {
			assertTrue(originalGenes.contains(mockKeylessChromosome.getGenes().get(availableIndex - indicesRemoved)));
			mockKeylessChromosome.removeGene(availableIndex - indicesRemoved);
			indicesRemoved++;
		}

		assertTrue(mockKeylessChromosome.getGenes().size() > 0);
		int mockChromosomeSizeMinusTwo = mockKeylessChromosome.getGenes().size() - 2;
		for (int i = 0; i < mockChromosomeSizeMinusTwo; i++) {
			assertEquals(mockGeneToReturn, mockKeylessChromosome.getGenes().get(i));
		}

		assertEquals(originalGenes.get(2), mockKeylessChromosome.getGenes().get(
				mockKeylessChromosome.getGenes().size() - 2));
		assertEquals(originalGenes.get(3), mockKeylessChromosome.getGenes().get(
				mockKeylessChromosome.getGenes().size() - 1));

		verify(geneDaoMock, atLeastOnce()).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, atMost(4)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, never()).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneGroupWithAllIndicesUsed() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<MockGene> originalGenes = new ArrayList<>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);
		originalGenes.add(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);
		originalGenes.add(mockGene4);

		List<Integer> availableIndices = new ArrayList<Integer>();
		groupMutationAlgorithm.mutateRandomGeneGroup(mockKeylessChromosome, availableIndices, 2);

		assertTrue(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertEquals(0, availableIndices.size());
		assertEquals(21, mockKeylessChromosome.actualSize().intValue());

		verifyZeroInteractions(geneDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneGroupBoundaryConditions() {
		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();
		List<MockGene> originalGenes = new ArrayList<>();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);
		originalGenes.add(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);
		originalGenes.add(mockGene2);

		MockGene mockGeneOfSize3 = new MockGene();
		mockGeneOfSize3.addSequence(new MockSequence("x"));
		mockGeneOfSize3.addSequence(new MockSequence("y"));
		mockGeneOfSize3.addSequence(new MockSequence("z"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(mockGeneOfSize3);

		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		groupMutationAlgorithm.mutateRandomGeneGroup(mockKeylessChromosome, availableIndices, 3);

		assertFalse(originalGenes.equals(mockKeylessChromosome.getGenes()));
		assertTrue(availableIndices.isEmpty());
		assertEquals(12, mockKeylessChromosome.actualSize().intValue());
		System.out.println(mockKeylessChromosome);
		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGeneOfSize3, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGeneOfSize3, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGeneOfSize3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGeneOfSize3, mockKeylessChromosome.getGenes().get(3));

		verify(geneDaoMock, times(4)).findRandomGene(same(mockKeylessChromosome));
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testAddRightIndices() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 3, 4, 5);
		int randomAvailableIndex = 0;
		int maxGenesToMutate = 3;

		int indicesAdded = GroupMutationAlgorithm.addRightIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(3, indicesAdded);
	}

	@Test
	public void testAddRightIndicesGap() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 4, 5, 6);
		int randomAvailableIndex = 1;
		int maxGenesToMutate = 100;

		int indicesAdded = GroupMutationAlgorithm.addRightIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(1, indicesAdded);
	}

	@Test
	public void testAddRightIndicesEnd() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 3, 4, 5);
		int randomAvailableIndex = 4;
		int maxGenesToMutate = 100;

		int indicesAdded = GroupMutationAlgorithm.addRightIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(1, indicesAdded);
	}

	@Test
	public void testAddLeftIndices() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 3, 4, 5);
		int randomAvailableIndex = 4;
		int maxGenesToMutate = 3;

		int indicesAdded = GroupMutationAlgorithm.addLeftIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(3, indicesAdded);
	}

	@Test
	public void testAddLeftIndicesGap() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 4, 5, 6);
		int randomAvailableIndex = 4;
		int maxGenesToMutate = 100;

		int indicesAdded = GroupMutationAlgorithm.addLeftIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(1, indicesAdded);
	}

	@Test
	public void testAddLeftIndicesEnd() {
		List<Integer> availableIndices = Arrays.asList(0, 1, 2, 3, 4, 5);
		int randomAvailableIndex = 1;
		int maxGenesToMutate = 100;

		int indicesAdded = GroupMutationAlgorithm.addLeftIndices(availableIndices, randomAvailableIndex,
				maxGenesToMutate);

		assertEquals(1, indicesAdded);
	}

	@Test
	public void testUpdateAvailableIndicesEqual() {
		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		availableIndices.add(2);
		availableIndices.add(3);
		int beginIndex = 1;
		int numGenesRemoved = 2;
		int numGenesInserted = 2;

		GroupMutationAlgorithm.updateAvailableIndices(availableIndices, beginIndex, numGenesRemoved, numGenesInserted);

		assertEquals(2, availableIndices.size());
		assertEquals(0, availableIndices.get(0).intValue());
		assertEquals(3, availableIndices.get(1).intValue());
	}

	@Test
	public void testUpdateAvailableIndicesNegativeDifference() {
		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		availableIndices.add(2);
		availableIndices.add(3);
		int beginIndex = 1;
		int numGenesRemoved = 2;
		int numGenesInserted = 3;

		GroupMutationAlgorithm.updateAvailableIndices(availableIndices, beginIndex, numGenesRemoved, numGenesInserted);

		assertEquals(2, availableIndices.size());
		assertEquals(0, availableIndices.get(0).intValue());
		assertEquals(4, availableIndices.get(1).intValue());
	}

	@Test
	public void testUpdateAvailableIndicesPositiveDifference() {
		List<Integer> availableIndices = new ArrayList<Integer>();
		availableIndices.add(0);
		availableIndices.add(1);
		availableIndices.add(2);
		availableIndices.add(3);
		availableIndices.add(4);
		int beginIndex = 1;
		int numGenesRemoved = 3;
		int numGenesInserted = 2;

		GroupMutationAlgorithm.updateAvailableIndices(availableIndices, beginIndex, numGenesRemoved, numGenesInserted);

		assertEquals(2, availableIndices.size());
		assertEquals(0, availableIndices.get(0).intValue());
		assertEquals(3, availableIndices.get(1).intValue());
	}

	@Test
	public void testMutateGeneGroup() {
		int beginIndex = 1;
		int numGenes = 2;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);

		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));

		MockGene randomGeneToReturn = new MockGene();
		randomGeneToReturn.addSequence(new MockSequence("c"));
		randomGeneToReturn.addSequence(new MockSequence("o"));
		randomGeneToReturn.addSequence(new MockSequence("o"));
		randomGeneToReturn.addSequence(new MockSequence("l"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenReturn(randomGeneToReturn);

		int genesInserted = groupMutationAlgorithm.mutateGeneGroup(mockKeylessChromosome, beginIndex, numGenes);

		assertEquals(2, genesInserted);
		verify(geneDaoMock, times(2)).findRandomGene(same(mockKeylessChromosome));
		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(randomGeneToReturn, mockKeylessChromosome.getGenes().get(1));
		assertEquals(randomGeneToReturn, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));
		assertEquals(21, mockKeylessChromosome.actualSize().intValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMutateGeneGroupBeginIndexOutOfBounds() {
		int beginIndex = 2;
		int numGenes = 1;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		groupMutationAlgorithm.mutateGeneGroup(mockKeylessChromosome, beginIndex, numGenes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMutateGeneGroupInsufficientRemaining() {
		int beginIndex = 1;
		int numGenes = 2;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		groupMutationAlgorithm.mutateGeneGroup(mockKeylessChromosome, beginIndex, numGenes);
	}

	@Test
	public void testRemoveGenes() {
		int beginIndex = 3;
		int numGenes = 2;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("r"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("l"));
		mockGene4.addSequence(new MockSequence("l"));
		mockGene4.addSequence(new MockSequence("y"));
		mockKeylessChromosome.addGene(mockGene4);

		MockGene mockGene5 = new MockGene();
		mockGene5.addSequence(new MockSequence("j"));
		mockGene5.addSequence(new MockSequence("u"));
		mockGene5.addSequence(new MockSequence("s"));
		mockGene5.addSequence(new MockSequence("t"));
		mockKeylessChromosome.addGene(mockGene5);

		MockGene mockGene6 = new MockGene();
		mockGene6.addSequence(new MockSequence("a"));
		mockGene6.addSequence(new MockSequence("w"));
		mockGene6.addSequence(new MockSequence("e"));
		mockGene6.addSequence(new MockSequence("s"));
		mockGene6.addSequence(new MockSequence("o"));
		mockGene6.addSequence(new MockSequence("m"));
		mockGene6.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene6);

		assertEquals(6, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));
		assertEquals(mockGene5, mockKeylessChromosome.getGenes().get(4));
		assertEquals(mockGene6, mockKeylessChromosome.getGenes().get(5));

		List<Gene> genesRemoved = GroupMutationAlgorithm.removeGenes(mockKeylessChromosome, beginIndex, numGenes);

		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, genesRemoved.get(0));
		assertEquals(mockGene5, genesRemoved.get(1));
		assertEquals(mockGene6, mockKeylessChromosome.getGenes().get(3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveGenesBeginIndexOutOfBounds() {
		int beginIndex = 2;
		int numGenes = 1;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		GroupMutationAlgorithm.removeGenes(mockKeylessChromosome, beginIndex, numGenes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveGenesInsufficientRemaining() {
		int beginIndex = 1;
		int numGenes = 2;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		GroupMutationAlgorithm.removeGenes(mockKeylessChromosome, beginIndex, numGenes);
	}

	@Test
	public void testInsertRandomGenes() {
		int beginGeneIndex = 3;
		int sequencesRemoved = 7;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);

		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));

		final MockGene randomGeneToReturn = new MockGene();
		randomGeneToReturn.addSequence(new MockSequence("c"));
		randomGeneToReturn.addSequence(new MockSequence("o"));
		randomGeneToReturn.addSequence(new MockSequence("o"));
		randomGeneToReturn.addSequence(new MockSequence("l"));

		when(geneDaoMock.findRandomGene(same(mockKeylessChromosome))).thenAnswer(new Answer<MockGene>() {
			public MockGene answer(InvocationOnMock invocation) throws Throwable {
				return randomGeneToReturn.clone();
			}
		});
		when(geneDaoMock.findRandomGeneOfLength(same(mockKeylessChromosome), anyInt())).thenReturn(
				randomGeneToReturn.clone());

		int genesInserted = groupMutationAlgorithm.insertRandomGenes(mockKeylessChromosome, beginGeneIndex,
				sequencesRemoved);

		assertEquals(2, genesInserted);
		assertEquals(6, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(randomGeneToReturn, mockKeylessChromosome.getGenes().get(3));
		assertEquals(randomGeneToReturn, mockKeylessChromosome.getGenes().get(4));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(5));
		verify(geneDaoMock, times(2)).findRandomGene(same(mockKeylessChromosome));
		verify(geneDaoMock, times(1)).findRandomGeneOfLength(same(mockKeylessChromosome), anyInt());
	}

	@Test
	public void testInsertRandomGenesWithZeroRemoved() {
		int beginGeneIndex = 3;
		int sequencesRemoved = 0;

		MockKeylessChromosome mockKeylessChromosome = new MockKeylessChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockGene1.addSequence(new MockSequence("o"));
		mockGene1.addSequence(new MockSequence("r"));
		mockGene1.addSequence(new MockSequence("g"));
		mockGene1.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("b"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("d"));
		mockGene2.addSequence(new MockSequence("e"));
		mockGene2.addSequence(new MockSequence("n"));
		mockKeylessChromosome.addGene(mockGene2);

		MockGene mockGene3 = new MockGene();
		mockGene3.addSequence(new MockSequence("i"));
		mockGene3.addSequence(new MockSequence("s"));
		mockKeylessChromosome.addGene(mockGene3);

		MockGene mockGene4 = new MockGene();
		mockGene4.addSequence(new MockSequence("a"));
		mockGene4.addSequence(new MockSequence("w"));
		mockGene4.addSequence(new MockSequence("e"));
		mockGene4.addSequence(new MockSequence("s"));
		mockGene4.addSequence(new MockSequence("o"));
		mockGene4.addSequence(new MockSequence("m"));
		mockGene4.addSequence(new MockSequence("e"));
		mockKeylessChromosome.addGene(mockGene4);

		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));

		int genesInserted = groupMutationAlgorithm.insertRandomGenes(mockKeylessChromosome, beginGeneIndex,
				sequencesRemoved);

		assertEquals(0, genesInserted);
		assertEquals(4, mockKeylessChromosome.getGenes().size());
		assertEquals(mockGene1, mockKeylessChromosome.getGenes().get(0));
		assertEquals(mockGene2, mockKeylessChromosome.getGenes().get(1));
		assertEquals(mockGene3, mockKeylessChromosome.getGenes().get(2));
		assertEquals(mockGene4, mockKeylessChromosome.getGenes().get(3));
		verifyZeroInteractions(geneDaoMock);
	}
}
