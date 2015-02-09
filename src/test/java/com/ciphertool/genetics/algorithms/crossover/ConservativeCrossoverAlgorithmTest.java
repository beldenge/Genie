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

package com.ciphertool.genetics.algorithms.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;

public class ConservativeCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {

	@Test
	public void testSetFitnessEvaluator() {
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorToSet);

		Field fitnessEvaluatorField = ReflectionUtils.findField(
				ConservativeCrossoverAlgorithm.class, "fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		FitnessEvaluator fitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(
				fitnessEvaluatorField, conservativeCrossoverAlgorithm);

		assertSame(fitnessEvaluatorToSet, fitnessEvaluatorFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();
		conservativeCrossoverAlgorithm.setMutationAlgorithm(mutationAlgorithmToSet);

		Field mutationAlgorithmField = ReflectionUtils.findField(
				ConservativeCrossoverAlgorithm.class, "mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils
				.getField(mutationAlgorithmField, conservativeCrossoverAlgorithm);

		assertSame(mutationAlgorithmToSet, mutationAlgorithmFromObject);
	}

	@Test
	public void testSetMutateDuringCrossover() {
		boolean mutateDuringCrossoverToSet = true;

		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		Field mutateDuringCrossoverField = ReflectionUtils.findField(
				ConservativeCrossoverAlgorithm.class, "mutateDuringCrossover");
		ReflectionUtils.makeAccessible(mutateDuringCrossoverField);
		boolean mutateDuringCrossoverFromObject = (boolean) ReflectionUtils.getField(
				mutateDuringCrossoverField, conservativeCrossoverAlgorithm);

		assertEquals(mutateDuringCrossoverToSet, mutateDuringCrossoverFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testCrossoverWithIllegalState() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		conservativeCrossoverAlgorithm.crossover(mom, dad);
	}

	@Test
	public void testCrossover() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness
		 * value defaults to zero, and this algorithm allows replacements where
		 * the fitness is equal
		 */
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();

		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = conservativeCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(1, children.size());

		assertFalse(children.get(0).equals(mom));
		assertFalse(children.get(0).equals(dad));

		assertEquals(1, mom.getNumberOfChildren());
		assertEquals(1, dad.getNumberOfChildren());

		assertEquals(6, children.get(0).getGenes().size());
		assertEquals(dad.getGenes().get(0), children.get(0).getGenes().get(0));
		assertEquals(mom.getGenes().get(1), children.get(0).getGenes().get(1));
		assertEquals(mom.getGenes().get(2), children.get(0).getGenes().get(2));
		assertEquals(dad.getGenes().get(3), children.get(0).getGenes().get(3));
		assertEquals(dad.getGenes().get(4), children.get(0).getGenes().get(4));
		assertEquals(dad.getGenes().get(5), children.get(0).getGenes().get(5));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(4)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testCrossoverNullChild() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(false);

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

		List<KeylessChromosome> children = conservativeCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(0, children.size());

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_NoMutation() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness
		 * value defaults to zero, and this algorithm allows replacements where
		 * the fitness is equal
		 */
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = conservativeCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(dad.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(dad.getGenes().get(3), child.getGenes().get(3));
		assertEquals(dad.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));

		verify(fitnessEvaluatorMock, times(4)).evaluate(any(Chromosome.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPerformCrossover_WithMutation() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness
		 * value defaults to zero, and this algorithm allows replacements where
		 * the fitness is equal
		 */
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		MutationAlgorithm mockMutationAlgorithm = mock(MutationAlgorithm.class);
		conservativeCrossoverAlgorithm.setMutationAlgorithm(mockMutationAlgorithm);

		conservativeCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = conservativeCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(dad.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(dad.getGenes().get(3), child.getGenes().get(3));
		assertEquals(dad.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));

		verify(mockMutationAlgorithm, times(1)).mutateChromosome(eq(child));
		verify(fitnessEvaluatorMock, times(4)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ChildEqualsFirstParent() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(false);

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

		KeylessChromosome child = conservativeCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ChildEqualsSecondParent() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(999.0);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		conservativeCrossoverAlgorithm.setMutationAlgorithm(null);
		conservativeCrossoverAlgorithm.setMutateDuringCrossover(false);

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

		KeylessChromosome child = conservativeCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testAttemptToReplaceGeneInChildEqualFitness() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness
		 * value defaults to zero, and this algorithm allows replacements where
		 * the fitness is equal
		 */
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		conservativeCrossoverAlgorithm.attemptToReplaceGeneInChild(crossoverProgressDto, mom, dad);

		// The "mom" Chromosome should only have the first Gene replaced
		assertEquals(6, mom.getGenes().size());
		assertEquals(dad.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(momClone.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));

		// The "dad" Chromosome should remain unmodified
		assertEquals(6, dad.getGenes().size());
		assertEquals(dadClone.getGenes().get(0), dad.getGenes().get(0));
		assertEquals(dadClone.getGenes().get(1), dad.getGenes().get(1));
		assertEquals(dadClone.getGenes().get(2), dad.getGenes().get(2));
		assertEquals(dadClone.getGenes().get(3), dad.getGenes().get(3));
		assertEquals(dadClone.getGenes().get(4), dad.getGenes().get(4));
		assertEquals(dadClone.getGenes().get(5), dad.getGenes().get(5));

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}

	@Test
	public void testAttemptToReplaceGeneInChildHigherFitness() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(999.0);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		conservativeCrossoverAlgorithm.attemptToReplaceGeneInChild(crossoverProgressDto, mom, dad);

		// The "mom" Chromosome should only have the first Gene replaced
		assertEquals(6, mom.getGenes().size());
		assertEquals(dad.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(momClone.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));

		// The "dad" Chromosome should remain unmodified
		assertEquals(6, dad.getGenes().size());
		assertEquals(dadClone.getGenes().get(0), dad.getGenes().get(0));
		assertEquals(dadClone.getGenes().get(1), dad.getGenes().get(1));
		assertEquals(dadClone.getGenes().get(2), dad.getGenes().get(2));
		assertEquals(dadClone.getGenes().get(3), dad.getGenes().get(3));
		assertEquals(dadClone.getGenes().get(4), dad.getGenes().get(4));
		assertEquals(dadClone.getGenes().get(5), dad.getGenes().get(5));

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}

	@Test
	public void testAttemptToReplaceGeneInChildLowerFitness() {
		ConservativeCrossoverAlgorithm conservativeCrossoverAlgorithm = new ConservativeCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		conservativeCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		CrossoverProgressDto crossoverProgressDto = new CrossoverProgressDto();

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		conservativeCrossoverAlgorithm.attemptToReplaceGeneInChild(crossoverProgressDto, mom, dad);

		// The "mom" Chromosome should remain unmodified
		assertEquals(6, mom.getGenes().size());
		assertEquals(momClone.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(momClone.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));

		// The "dad" Chromosome should remain unmodified
		assertEquals(6, dad.getGenes().size());
		assertEquals(dadClone.getGenes().get(0), dad.getGenes().get(0));
		assertEquals(dadClone.getGenes().get(1), dad.getGenes().get(1));
		assertEquals(dadClone.getGenes().get(2), dad.getGenes().get(2));
		assertEquals(dadClone.getGenes().get(3), dad.getGenes().get(3));
		assertEquals(dadClone.getGenes().get(4), dad.getGenes().get(4));
		assertEquals(dadClone.getGenes().get(5), dad.getGenes().get(5));

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}
}