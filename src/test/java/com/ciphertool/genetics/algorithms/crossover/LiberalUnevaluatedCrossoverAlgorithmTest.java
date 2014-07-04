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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.dao.GeneListDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.ChromosomeHelper;
import com.ciphertool.genetics.util.Coin;

public class LiberalUnevaluatedCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {
	@Test
	public void testSetGeneListDao() {
		GeneListDao geneListDaoToSet = mock(GeneListDao.class);
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoToSet);

		Field geneListDaoField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "geneListDao");
		ReflectionUtils.makeAccessible(geneListDaoField);
		GeneListDao geneListDaoFromObject = (GeneListDao) ReflectionUtils.getField(
				geneListDaoField, liberalUnevaluatedCrossoverAlgorithm);

		assertSame(geneListDaoToSet, geneListDaoFromObject);
	}

	@Test
	public void testSetChromosomeHelper() {
		ChromosomeHelper chromosomeHelperToSet = mock(ChromosomeHelper.class);
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelperToSet);

		Field chromosomeHelperField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "chromosomeHelper");
		ReflectionUtils.makeAccessible(chromosomeHelperField);
		ChromosomeHelper chromosomeHelperFromObject = (ChromosomeHelper) ReflectionUtils.getField(
				chromosomeHelperField, liberalUnevaluatedCrossoverAlgorithm);

		assertSame(chromosomeHelperToSet, chromosomeHelperFromObject);
	}

	@Test
	public void testSetMutationAlgorithm() {
		MutationAlgorithm mutationAlgorithmToSet = mock(MutationAlgorithm.class);

		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(mutationAlgorithmToSet);

		Field mutationAlgorithmField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "mutationAlgorithm");
		ReflectionUtils.makeAccessible(mutationAlgorithmField);
		MutationAlgorithm mutationAlgorithmFromObject = (MutationAlgorithm) ReflectionUtils
				.getField(mutationAlgorithmField, liberalUnevaluatedCrossoverAlgorithm);

		assertSame(mutationAlgorithmToSet, mutationAlgorithmFromObject);
	}

	@Test
	public void testSetMutateDuringCrossover() {
		boolean mutateDuringCrossoverToSet = true;

		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(mutateDuringCrossoverToSet);

		Field mutateDuringCrossoverField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "mutateDuringCrossover");
		ReflectionUtils.makeAccessible(mutateDuringCrossoverField);
		boolean mutateDuringCrossoverFromObject = (boolean) ReflectionUtils.getField(
				mutateDuringCrossoverField, liberalUnevaluatedCrossoverAlgorithm);

		assertEquals(mutateDuringCrossoverToSet, mutateDuringCrossoverFromObject);
	}

	@Test
	public void testSetCoin() {
		Coin coinToSet = mock(Coin.class);
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinToSet);

		Field coinField = ReflectionUtils.findField(LiberalUnevaluatedCrossoverAlgorithm.class,
				"coin");
		ReflectionUtils.makeAccessible(coinField);
		Coin coinFromObject = (Coin) ReflectionUtils.getField(coinField,
				liberalUnevaluatedCrossoverAlgorithm);

		assertSame(coinToSet, coinFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testCrossoverWithIllegalState() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		FitnessEvaluator fitnessEvaluatorMock = mock(FitnessEvaluator.class);
		liberalUnevaluatedCrossoverAlgorithm.setFitnessEvaluator(fitnessEvaluatorMock);

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(true);

		Chromosome mom = getMom();
		Chromosome dad = getDad();
		liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);
	}

	@Test
	public void testCrossover() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		Chromosome mom = getMom();
		Chromosome dad = getDad();

		Chromosome momClone = mom.clone();
		Chromosome dadClone = dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<Chromosome> children = liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);

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

		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testCrossoverNullChild() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		MockChromosome mom = new MockChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockChromosome dad = new MockChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		Chromosome momClone = mom.clone();
		Chromosome dadClone = dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<Chromosome> children = liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);

		assertNotNull(children);
		assertEquals(0, children.size());

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(coinMock, times(1)).flip();
	}

	@Test
	public void testPerformCrossover_NoMutation() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		Chromosome mom = getMom();
		Chromosome dad = getDad();
		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testPerformCrossover_WithMutation() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		MutationAlgorithm mockMutationAlgorithm = mock(MutationAlgorithm.class);
		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(mockMutationAlgorithm);

		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(true);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		Chromosome mom = getMom();
		Chromosome dad = getDad();
		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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
		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testPerformCrossover_ChildEqualsFirstParent() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		MockChromosome mom = new MockChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockChromosome dad = new MockChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		Chromosome momClone = mom.clone();
		Chromosome dadClone = dad.clone();

		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(coinMock, times(1)).flip();
	}

	@Test
	public void testPerformCrossover_ChildEqualsSecondParent() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		MockChromosome mom = new MockChromosome();
		mom.setTargetSize(1);
		MockGene momGene = new MockGene();
		MockSequence momSequence = new MockSequence("m");
		momGene.addSequence(momSequence);
		mom.addGene(momGene);

		MockChromosome dad = new MockChromosome();
		dad.setTargetSize(1);
		MockGene dadGene = new MockGene();
		MockSequence dadSequence = new MockSequence("d");
		dadGene.addSequence(dadSequence);
		dad.addGene(dadGene);

		Chromosome momClone = mom.clone();
		Chromosome dadClone = dad.clone();

		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

		assertNull(child);

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(coinMock, times(1)).flip();
	}

	@Test
	public void testPerformCrossover_ResizeChromosome_TooSmall() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		chromosomeHelper.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false, false, true, false, false, false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);

		Chromosome mom = getMom();
		Chromosome momClone = mom.clone();
		Chromosome dad = getDad();
		Chromosome dadClone = dad.clone();
		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		verify(geneListDaoMock, times(1)).findRandomGene(any(Chromosome.class));
		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testPerformCrossover_ResizeChromosome_TooLarge() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		ChromosomeHelper chromosomeHelper = new ChromosomeHelper();
		// The GeneListDao should never be used
		chromosomeHelper.setGeneListDao(null);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false, true, false, false, false, false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelper);

		Chromosome mom = getMom();
		Chromosome momClone = mom.clone();
		Chromosome dad = getDad();
		Chromosome dadClone = dad.clone();
		Chromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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
		trimmedGene.addSequence(new MockSequence("t", 20));
		trimmedGene.addSequence(new MockSequence("w", 21));
		trimmedGene.addSequence(new MockSequence("e", 22));
		trimmedGene.addSequence(new MockSequence("l", 23));
		assertEquals(trimmedGene, child.getGenes().get(5));

		// The "mom" Chromosome should remain unmodified
		assertEquals(momClone.getGenes(), mom.getGenes());

		// The "dad" Chromosome should remain unmodified
		assertEquals(dadClone.getGenes(), dad.getGenes());

		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testAttemptToReplaceGeneInChild_ResizeChromosome() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		GeneListDao geneListDaoMock = mock(GeneListDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneListDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalUnevaluatedCrossoverAlgorithm.setGeneListDao(geneListDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		Chromosome mom = getMom();
		Chromosome momClone = mom.clone();
		Chromosome dad = getDad();
		Chromosome dadClone = dad.clone();
		liberalUnevaluatedCrossoverAlgorithm.attemptToReplaceGeneInChild(2, mom, dad);

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

		verify(coinMock, times(1)).flip();
		verify(geneListDaoMock, times(1)).findRandomGene(any(Chromosome.class));
	}
}
