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

package com.ciphertool.genetics.algorithms.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.RandomListElementSelector;

public class ConservativeSinglePointCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();
		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(mutationAlgorithmToSet);

		Field mutationAlgorithmField = ReflectionUtils.findField(ConservativeSinglePointCrossoverAlgorithm.class,
				"mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils.getField(
				mutationAlgorithmField, conservativeSinglePointCrossoverAlgorithm);

		assertSame(mutationAlgorithmToSet, mutationAlgorithmFromObject);
	}

	@Test
	public void testSetMutateDuringCrossover() {
		boolean mutateDuringCrossoverToSet = true;

		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		Field mutateDuringCrossoverField = ReflectionUtils.findField(ConservativeSinglePointCrossoverAlgorithm.class,
				"mutateDuringCrossover");
		ReflectionUtils.makeAccessible(mutateDuringCrossoverField);
		boolean mutateDuringCrossoverFromObject = (boolean) ReflectionUtils.getField(mutateDuringCrossoverField,
				conservativeSinglePointCrossoverAlgorithm);

		assertEquals(mutateDuringCrossoverToSet, mutateDuringCrossoverFromObject);
	}

	@Test
	public void testSetRandomListElementSelector() {
		RandomListElementSelector randomListElementSelectorToSet = mock(RandomListElementSelector.class);
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();
		conservativeSinglePointCrossoverAlgorithm.setRandomListElementSelector(randomListElementSelectorToSet);

		Field randomListElementSelectorField = ReflectionUtils.findField(
				ConservativeSinglePointCrossoverAlgorithm.class, "randomListElementSelector");
		ReflectionUtils.makeAccessible(randomListElementSelectorField);
		RandomListElementSelector randomListElementSelectorFromObject = (RandomListElementSelector) ReflectionUtils
				.getField(randomListElementSelectorField, conservativeSinglePointCrossoverAlgorithm);

		assertSame(randomListElementSelectorToSet, randomListElementSelectorFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testCrossoverWithIllegalState() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		conservativeSinglePointCrossoverAlgorithm.crossover(mom, dad);
	}

	@Test
	public void testCrossover() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		RandomListElementSelector randomListElementSelectorMock = mock(RandomListElementSelector.class);
		when(randomListElementSelectorMock.selectRandomListElement(anyListOf(Integer.class))).thenReturn(1);
		conservativeSinglePointCrossoverAlgorithm.setRandomListElementSelector(randomListElementSelectorMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();

		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = conservativeSinglePointCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(1, children.size());

		assertFalse(children.get(0).equals(mom));
		assertFalse(children.get(0).equals(dad));

		assertEquals(1, mom.getNumberOfChildren());
		assertEquals(1, dad.getNumberOfChildren());

		assertEquals(6, children.get(0).getGenes().size());
		assertEquals(mom.getGenes().get(0), children.get(0).getGenes().get(0));
		assertEquals(mom.getGenes().get(1), children.get(0).getGenes().get(1));
		assertEquals(mom.getGenes().get(2), children.get(0).getGenes().get(2));
		assertEquals(dad.getGenes().get(3), children.get(0).getGenes().get(3));
		assertEquals(dad.getGenes().get(4), children.get(0).getGenes().get(4));
		assertEquals(dad.getGenes().get(5), children.get(0).getGenes().get(5));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());
	}

	@Test
	public void testCrossoverNoCentromeres() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		MockKeylessChromosome mom = new MockKeylessChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockKeylessChromosome dad = new MockKeylessChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		KeylessChromosome momClone = mom.clone();
		KeylessChromosome dadClone = dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = conservativeSinglePointCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(0, children.size());

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());
	}

	@Test
	public void testCrossoverNullChild() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		RandomListElementSelector randomListElementSelectorMock = mock(RandomListElementSelector.class);
		when(randomListElementSelectorMock.selectRandomListElement(anyListOf(Integer.class))).thenReturn(0);
		conservativeSinglePointCrossoverAlgorithm.setRandomListElementSelector(randomListElementSelectorMock);

		KeylessChromosome mom = (KeylessChromosome) getMom().clone();
		// We want the two Chromosomes to be the same for this test
		KeylessChromosome dad = (KeylessChromosome) getMom().clone();

		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = conservativeSinglePointCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(0, children.size());

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());
	}

	@Test
	public void testPerformCrossover_NoMutation() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = conservativeSinglePointCrossoverAlgorithm.performCrossover(mom, dad, 11);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(mom.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(dad.getGenes().get(3), child.getGenes().get(3));
		assertEquals(dad.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPerformCrossover_WithMutation() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		MutationAlgorithm mockMutationAlgorithm = mock(MutationAlgorithm.class);
		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(mockMutationAlgorithm);

		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = conservativeSinglePointCrossoverAlgorithm.performCrossover(mom, dad, 11);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(mom.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(dad.getGenes().get(3), child.getGenes().get(3));
		assertEquals(dad.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));

		verify(mockMutationAlgorithm, times(1)).mutateChromosome(eq(child));
	}

	@Test
	public void testPerformCrossover_ChildEqualsFirstParent() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		MockKeylessChromosome mom = new MockKeylessChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockKeylessChromosome dad = new MockKeylessChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		KeylessChromosome momClone = mom.clone();
		KeylessChromosome dadClone = dad.clone();

		KeylessChromosome child = conservativeSinglePointCrossoverAlgorithm.performCrossover(mom, dad, 11);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());
	}

	@Test
	public void testPerformCrossover_ChildEqualsSecondParent() {
		ConservativeSinglePointCrossoverAlgorithm conservativeSinglePointCrossoverAlgorithm = new ConservativeSinglePointCrossoverAlgorithm();

		conservativeSinglePointCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeSinglePointCrossoverAlgorithm.setMutateDuringCrossover(false);

		MockKeylessChromosome mom = new MockKeylessChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockKeylessChromosome dad = new MockKeylessChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		KeylessChromosome momClone = mom.clone();
		KeylessChromosome dadClone = dad.clone();

		KeylessChromosome child = conservativeSinglePointCrossoverAlgorithm.performCrossover(mom, dad, 11);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());
	}

	@Test
	public void testFindPotentialCentromeres() {
		List<Integer> expectedCentromeres = Arrays.asList(3, 11, 15, 19);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		List<Integer> potentialCentromeres = ConservativeSinglePointCrossoverAlgorithm.findPotentialCentromeres(mom,
				dad);

		assertEquals(expectedCentromeres, potentialCentromeres);
	}

	@Test
	public void testFindGeneBeginningAtCentromere() {
		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();

		int geneAtCentromere = ConservativeSinglePointCrossoverAlgorithm.findGeneBeginningAtCentromere(mom, 11);

		assertEquals(3, geneAtCentromere);

		geneAtCentromere = ConservativeSinglePointCrossoverAlgorithm.findGeneBeginningAtCentromere(dad, 11);

		assertEquals(3, geneAtCentromere);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindGeneBeginningAtInvalidCentromere() {
		KeylessChromosome mom = getMom();

		ConservativeSinglePointCrossoverAlgorithm.findGeneBeginningAtCentromere(mom, 4);
	}
}
