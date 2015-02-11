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

package com.ciphertool.genetics.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockKeylessChromosome;
import com.ciphertool.genetics.mocks.MockSequence;

public class KeylessChromosomeHelperTest {
	private static KeylessChromosomeHelper keylessChromosomeHelper;
	private static GeneDao geneDaoMock;

	@BeforeClass
	public static void setUp() {
		keylessChromosomeHelper = new KeylessChromosomeHelper();
		geneDaoMock = mock(GeneDao.class);

		keylessChromosomeHelper.setGeneDao(geneDaoMock);
	}

	@Before
	public void resetMocks() {
		reset(geneDaoMock);

		when(geneDaoMock.findRandomGene(any(Chromosome.class))).thenReturn(createRandomGene(5),
				createRandomGene(5), createRandomGene(5), createRandomGene(5), createRandomGene(5),
				createRandomGene(5), createRandomGene(5));
	}

	@Test
	public void testSetGeneDao() {
		KeylessChromosomeHelper keylessChromosomeHelper = new KeylessChromosomeHelper();
		keylessChromosomeHelper.setGeneDao(geneDaoMock);

		Field geneDaoField = ReflectionUtils.findField(KeylessChromosomeHelper.class, "geneDao");
		ReflectionUtils.makeAccessible(geneDaoField);
		GeneDao geneDaoFromObject = (GeneDao) ReflectionUtils.getField(
				geneDaoField, keylessChromosomeHelper);

		assertSame(geneDaoMock, geneDaoFromObject);
	}

	@Test
	public void testResizeChromosomeLessThanTargetSize() {
		// Test where the last gene ends exactly at the target size
		MockKeylessChromosome chromosomeToResize = new MockKeylessChromosome();
		chromosomeToResize.setTargetSize(25);
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));

		assertEquals(2, chromosomeToResize.getGenes().size());
		assertEquals(new Integer(10), chromosomeToResize.actualSize());

		keylessChromosomeHelper.resizeChromosome(chromosomeToResize);

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(chromosomeToResize.targetSize(), chromosomeToResize.actualSize());

		// Test where the last gene does not end at the target size
		chromosomeToResize = new MockKeylessChromosome();
		chromosomeToResize.setTargetSize(25);
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(1));

		assertEquals(2, chromosomeToResize.getGenes().size());
		assertEquals(new Integer(6), chromosomeToResize.actualSize());

		keylessChromosomeHelper.resizeChromosome(chromosomeToResize);

		assertEquals(6, chromosomeToResize.getGenes().size());
		assertEquals(chromosomeToResize.targetSize(), chromosomeToResize.actualSize());
	}

	@Test
	public void testResizeChromosomeGreaterThanTargetSize() {
		// Test where no gene overlaps the the target size
		MockKeylessChromosome chromosomeToResize = new MockKeylessChromosome();
		chromosomeToResize.setTargetSize(25);
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));

		assertEquals(6, chromosomeToResize.getGenes().size());
		assertEquals(new Integer(30), chromosomeToResize.actualSize());

		keylessChromosomeHelper.resizeChromosome(chromosomeToResize);

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(chromosomeToResize.targetSize(), chromosomeToResize.actualSize());

		// Test where a gene overlaps the the target size
		chromosomeToResize = new MockKeylessChromosome();
		chromosomeToResize.setTargetSize(25);
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(10));

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(new Integer(30), chromosomeToResize.actualSize());

		keylessChromosomeHelper.resizeChromosome(chromosomeToResize);

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(chromosomeToResize.targetSize(), chromosomeToResize.actualSize());
	}

	@Test
	public void testResizeChromosomeAlreadyAtTargetSize() {
		MockKeylessChromosome chromosomeToResize = new MockKeylessChromosome();
		chromosomeToResize.setTargetSize(25);
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));
		chromosomeToResize.addGene(createRandomGene(5));

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(new Integer(25), chromosomeToResize.actualSize());

		keylessChromosomeHelper.resizeChromosome(chromosomeToResize);

		assertEquals(5, chromosomeToResize.getGenes().size());
		assertEquals(chromosomeToResize.targetSize(), chromosomeToResize.actualSize());
	}

	private static Gene createRandomGene(int size) {
		MockGene randomGene = new MockGene();

		for (int i = 0; i < size; i++) {
			MockSequence sequenceToAdd = new MockSequence();
			sequenceToAdd.setValue(Integer.toString(i));
			randomGene.addSequence(sequenceToAdd);
		}

		return randomGene;
	}
}
