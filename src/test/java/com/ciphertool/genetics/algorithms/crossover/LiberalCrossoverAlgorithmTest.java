/**
 * Copyright 2015 George Belden
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
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.KeylessChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.KeylessChromosomeHelper;

public class LiberalCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {

	@Test
	public void testSetGeneDao() {
		GeneDao geneDaoToSet = mock(GeneDao.class);
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();
		liberalCrossoverAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(LiberalCrossoverAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(geneDaoField, liberalCrossoverAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetChromosomeHelper() {
		KeylessChromosomeHelper chromosomeHelperToSet = mock(KeylessChromosomeHelper.class);
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();
		liberalCrossoverAlgorithm.setChromosomeHelper(chromosomeHelperToSet);

		Field chromosomeHelperField = ReflectionUtils.findField(LiberalCrossoverAlgorithm.class,
				"keylessChromosomeHelper");
		ReflectionUtils.makeAccessible(chromosomeHelperField);
		KeylessChromosomeHelper chromosomeHelperFromObject = (KeylessChromosomeHelper) ReflectionUtils.getField(
				chromosomeHelperField, liberalCrossoverAlgorithm);

		assertSame(chromosomeHelperToSet, chromosomeHelperFromObject);
	}

	@Test
	public void testSetFitnessEvaluator() {
		FitnessEvaluator fitnessEvaluatorToSet = mock(FitnessEvaluator.class);
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorToSet);

		Field fitnessEvaluatorField = ReflectionUtils.findField(LiberalCrossoverAlgorithm.class, "fitnessEvaluator");
		ReflectionUtils.makeAccessible(fitnessEvaluatorField);
		FitnessEvaluator fitnessEvaluatorFromObject = (FitnessEvaluator) ReflectionUtils.getField(
				fitnessEvaluatorField, liberalCrossoverAlgorithm);

		assertSame(fitnessEvaluatorToSet, fitnessEvaluatorFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();
		liberalCrossoverAlgorithm.setMutationAlgorithm(mutationAlgorithmToSet);

		Field mutationAlgorithmField = ReflectionUtils.findField(LiberalCrossoverAlgorithm.class, "mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils.getField(
				mutationAlgorithmField, liberalCrossoverAlgorithm);

		assertSame(mutationAlgorithmToSet, mutationAlgorithmFromObject);
	}

	@Test
	public void testSetMutateDuringCrossover() {
		boolean mutateDuringCrossoverToSet = true;

		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();
		liberalCrossoverAlgorithm.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		Field mutateDuringCrossoverField = ReflectionUtils.findField(LiberalCrossoverAlgorithm.class,
				"mutateDuringCrossover");
		ReflectionUtils.makeAccessible(mutateDuringCrossoverField);
		boolean mutateDuringCrossoverFromObject = (boolean) ReflectionUtils.getField(mutateDuringCrossoverField,
				liberalCrossoverAlgorithm);

		assertEquals(mutateDuringCrossoverToSet, mutateDuringCrossoverFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testCrossoverWithIllegalState() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		liberalCrossoverAlgorithm.crossover(mom, dad);
	}

	@Test
	public void testCrossover() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(1.0, -1.0, -1.0, -1.0, -1.0, 1.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();

		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = liberalCrossoverAlgorithm.crossover(mom, dad);

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
		assertEquals(mom.getGenes().get(3), children.get(0).getGenes().get(3));
		assertEquals(mom.getGenes().get(4), children.get(0).getGenes().get(4));
		assertEquals(dad.getGenes().get(5), children.get(0).getGenes().get(5));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(6)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testCrossoverNullChild() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		List<KeylessChromosome> children = liberalCrossoverAlgorithm.crossover(mom, dad);

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
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(1.0, -1.0, -1.0, -1.0, -1.0, 1.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(dad.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(mom.getGenes().get(3), child.getGenes().get(3));
		assertEquals(mom.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));

		verify(fitnessEvaluatorMock, times(6)).evaluate(any(Chromosome.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPerformCrossover_WithMutation() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(1.0, -1.0, -1.0, -1.0, -1.0, 1.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		MutationAlgorithm mockMutationAlgorithm = mock(MutationAlgorithm.class);
		liberalCrossoverAlgorithm.setMutationAlgorithm(mockMutationAlgorithm);

		liberalCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(dad.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(mom.getGenes().get(3), child.getGenes().get(3));
		assertEquals(mom.getGenes().get(4), child.getGenes().get(4));
		assertEquals(dad.getGenes().get(5), child.getGenes().get(5));

		verify(mockMutationAlgorithm, times(1)).mutateChromosome(eq(child));
		verify(fitnessEvaluatorMock, times(6)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ChildEqualsFirstParent() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ChildEqualsSecondParent() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(999.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ResizeChromosome_TooSmall() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-1.0, -1.0, 1.0, -1.0, -1.0, -1.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(7, child.getGenes().size());
		assertEquals(mom.getGenes().get(0), child.getGenes().get(0));
		assertEquals(mom.getGenes().get(1), child.getGenes().get(1));
		assertEquals(dad.getGenes().get(2), child.getGenes().get(2));
		assertEquals(mom.getGenes().get(3), child.getGenes().get(3));
		assertEquals(mom.getGenes().get(4), child.getGenes().get(4));
		assertEquals(mom.getGenes().get(5), child.getGenes().get(5));
		assertEquals(geneToReturn, child.getGenes().get(6));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(6)).evaluate(same(child));
		verify(geneDaoMock, times(1)).findRandomGene(any(Chromosome.class));
	}

	@Test
	public void testPerformCrossover_ResizeChromosome_TooLarge() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-1.0, 1.0, -1.0, -1.0, -1.0, -1.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();
		// The GeneDao should never be used
		keylessChromosomeHelper.setGeneDao(null);

		liberalCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		KeylessChromosome child = liberalCrossoverAlgorithm.performCrossover(mom, dad);

		assertNotNull(child);
		assertFalse(child.equals(mom));
		assertFalse(child.equals(dad));

		assertEquals(6, child.getGenes().size());
		assertEquals(mom.getGenes().get(0), child.getGenes().get(0));
		assertEquals(dad.getGenes().get(1), child.getGenes().get(1));
		assertEquals(mom.getGenes().get(2), child.getGenes().get(2));
		assertEquals(mom.getGenes().get(3), child.getGenes().get(3));
		assertEquals(mom.getGenes().get(4), child.getGenes().get(4));

		MockGene trimmedGene = new MockGene();
		trimmedGene.addSequence(new MockSequence("t", 19));
		trimmedGene.addSequence(new MockSequence("w", 20));
		trimmedGene.addSequence(new MockSequence("e", 21));
		trimmedGene.addSequence(new MockSequence("l", 22));
		assertEquals(trimmedGene, child.getGenes().get(5));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(6)).evaluate(same(child));
	}

	@Test
	public void testAttemptToReplaceGeneInChild_ResizeChromosome() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness value defaults to zero, and this
		 * algorithm allows replacements where the fitness is equal
		 */
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		liberalCrossoverAlgorithm.attemptToReplaceGeneInChild(2, mom, dad);

		// The "mom" Chromosome should only have one Gene replaced
		assertEquals(7, mom.getGenes().size());
		assertEquals(momClone.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(dad.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));
		assertEquals(geneToReturn, mom.getGenes().get(6));

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
		verify(geneDaoMock, times(1)).findRandomGene(any(Chromosome.class));
	}

	@Test
	public void testAttemptToReplaceGeneInChildEqualFitness() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		/*
		 * We do not need to stub out the evaluate() method because the fitness value defaults to zero, and this
		 * algorithm allows replacements where the fitness is equal
		 */
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setGeneDao(null);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		liberalCrossoverAlgorithm.attemptToReplaceGeneInChild(0, mom, dad);

		// The "mom" Chromosome should only have the first Gene replaced
		assertEquals(6, mom.getGenes().size());
		assertEquals(dad.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(momClone.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}

	@Test
	public void testAttemptToReplaceGeneInChildHigherFitness() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(999.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setGeneDao(null);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		liberalCrossoverAlgorithm.attemptToReplaceGeneInChild(0, mom, dad);

		// The "mom" Chromosome should only have the first Gene replaced
		assertEquals(6, mom.getGenes().size());
		assertEquals(dad.getGenes().get(0), mom.getGenes().get(0));
		assertEquals(momClone.getGenes().get(1), mom.getGenes().get(1));
		assertEquals(momClone.getGenes().get(2), mom.getGenes().get(2));
		assertEquals(momClone.getGenes().get(3), mom.getGenes().get(3));
		assertEquals(momClone.getGenes().get(4), mom.getGenes().get(4));
		assertEquals(momClone.getGenes().get(5), mom.getGenes().get(5));

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}

	@Test
	public void testAttemptToReplaceGeneInChildLowerFitness() {
		LiberalCrossoverAlgorithm liberalCrossoverAlgorithm = new LiberalCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		when(fitnessEvaluatorMock.evaluate(any(Chromosome.class))).thenReturn(-999.0);
		liberalCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalCrossoverAlgorithm.setGeneDao(null);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		liberalCrossoverAlgorithm.attemptToReplaceGeneInChild(0, mom, dad);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(fitnessEvaluatorMock, times(1)).evaluate(same(mom));
	}
}