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
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;
import com.ciphertool.genetics.util.Coin;
import com.ciphertool.genetics.util.KeylessChromosomeHelper;

public class LiberalUnevaluatedCrossoverAlgorithmTest extends CrossoverAlgorithmTestBase {
	@Test
	public void testSetGeneDao() {
		GeneDao geneDaoToSet = mock(GeneDao.class);
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoToSet);

		Field geneDaoField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(
				geneDaoField, liberalUnevaluatedCrossoverAlgorithm);

		assertSame(geneDaoToSet, geneDaoFromObject);
	}

	@Test
	public void testSetChromosomeHelper() {
		KeylessChromosomeHelper chromosomeHelperToSet = mock(KeylessChromosomeHelper.class);
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();
		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(chromosomeHelperToSet);

		Field chromosomeHelperField = ReflectionUtils.findField(
				LiberalUnevaluatedCrossoverAlgorithm.class, "keylessChromosomeHelper");
		ReflectionUtils.makeAccessible(chromosomeHelperField);
		KeylessChromosomeHelper chromosomeHelperFromObject = (KeylessChromosomeHelper) ReflectionUtils.getField(
				chromosomeHelperField, liberalUnevaluatedCrossoverAlgorithm);

		assertSame(chromosomeHelperToSet, chromosomeHelperFromObject);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);
	}

	@Test
	public void testCrossover() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();

		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();

		assertEquals(0, mom.getNumberOfChildren());
		assertEquals(0, dad.getNumberOfChildren());

		List<KeylessChromosome> children = liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);

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

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		List<KeylessChromosome> children = liberalUnevaluatedCrossoverAlgorithm.crossover(mom, dad);

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

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPerformCrossover_WithMutation() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		MutationAlgorithm mockMutationAlgorithm = mock(MutationAlgorithm.class);
		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(mockMutationAlgorithm);

		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(true);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true, false, false, false, false, true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome dad = getDad();
		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

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

		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false, false, true, false, false, false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		verify(geneDaoMock, times(1)).findRandomGene(any(Chromosome.class));
		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testPerformCrossover_ResizeChromosome_TooLarge() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		liberalUnevaluatedCrossoverAlgorithm.setMutationAlgorithm(null);
		liberalUnevaluatedCrossoverAlgorithm.setMutateDuringCrossover(false);

		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();
		// The GeneDao should never be used
		keylessChromosomeHelper.setGeneDao(null);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(false, true, false, false, false, false);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		liberalUnevaluatedCrossoverAlgorithm.setChromosomeHelper(keylessChromosomeHelper);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
		KeylessChromosome child = liberalUnevaluatedCrossoverAlgorithm.performCrossover(mom, dad);

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

		verify(coinMock, times(6)).flip();
	}

	@Test
	public void testAttemptToReplaceGeneInChild_ResizeChromosome() {
		LiberalUnevaluatedCrossoverAlgorithm liberalUnevaluatedCrossoverAlgorithm = new LiberalUnevaluatedCrossoverAlgorithm();

		GeneDao geneDaoMock = mock(GeneDao.class);
		MockGene geneToReturn = new MockGene();
		geneToReturn.addSequence(new MockSequence("w"));
		geneToReturn.addSequence(new MockSequence("o"));
		geneToReturn.addSequence(new MockSequence("r"));
		geneToReturn.addSequence(new MockSequence("d"));
		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(geneToReturn);
		liberalUnevaluatedCrossoverAlgorithm.setGeneDao(geneDaoMock);

		Coin coinMock = mock(Coin.class);
		when(coinMock.flip()).thenReturn(true);
		liberalUnevaluatedCrossoverAlgorithm.setCoin(coinMock);

		KeylessChromosome mom = getMom();
		KeylessChromosome momClone = (KeylessChromosome) mom.clone();
		KeylessChromosome dad = getDad();
		KeylessChromosome dadClone = (KeylessChromosome) dad.clone();
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
		verify(geneDaoMock, times(1)).findRandomGene(any(Chromosome.class));
	}
}
