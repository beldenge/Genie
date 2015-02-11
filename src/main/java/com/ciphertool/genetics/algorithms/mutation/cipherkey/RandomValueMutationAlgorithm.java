package com.ciphertool.genetics.algorithms.mutation.cipherkey;

import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.dao.GeneDao;
import com.ciphertool.genetics.entities.KeyedChromosome;

public class RandomValueMutationAlgorithm implements MutationAlgorithm<KeyedChromosome<String>> {
	private static Logger log = Logger.getLogger(RandomValueMutationAlgorithm.class);
	
	private GeneDao geneDao;
	private Integer maxMutationsPerChromosome;
	
	@Override
	public void mutateChromosome(KeyedChromosome<String> chromosome) {
		if (maxMutationsPerChromosome == null) {
			throw new IllegalStateException("The maxMutationsPerChromosome cannot be null.");
		}

		/*
		 * Choose a random number of mutations constrained by the configurable
		 * max and the total number of genes
		 */
		int numMutations = (int) (Math.random() * Math.min(maxMutationsPerChromosome, chromosome
				.getGenes().size())) + 1;
		
		Set<String> availableKeys = chromosome.getGenes().keySet();
		
		for (int i = 0; i < numMutations; i++) {
			// Keep track of the mutated keys
			mutateRandomGene(chromosome, availableKeys);
		}
	}
	
	/**
	 * Performs a genetic mutation of a random Gene of the supplied Chromosome
	 * 
	 * @param chromosome
	 *            the Chromosome to mutate
	 * @param availableIndices
	 *            the Set of available indices to mutate
	 */
	protected void mutateRandomGene(KeyedChromosome<String> chromosome, Set<String> availableIndices) {
		if (availableIndices == null || availableIndices.isEmpty()) {
			log.warn("List of available indices is null or empty.  Unable to find a Gene to mutate.  Returning null.");

			return;
		}
		
		Random generator = new Random();
		Object[] keys = availableIndices.toArray();
		
		// Get a random map key
		Object randomKey = keys[generator.nextInt(keys.length)];
		
		// Replace that map value with a randomly generated Gene
		chromosome.getGenes().put((String) randomKey, geneDao.findRandomGene(chromosome));
		
		// Remove the key so that it is not used for mutation again
		availableIndices.remove(randomKey);
	}
	
	/**
	 * @param geneDao
	 *            the geneDao to set
	 */
	@Required
	public void setGeneDao(GeneDao geneDao) {
		this.geneDao = geneDao;
	}

	@Override
	public void setMaxMutationsPerChromosome(Integer maxMutationsPerChromosome) {
		this.maxMutationsPerChromosome = maxMutationsPerChromosome;
	}
}
