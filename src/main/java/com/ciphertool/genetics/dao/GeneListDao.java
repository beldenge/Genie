package com.ciphertool.genetics.dao;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public interface GeneListDao {
	public Gene findRandomGene(Chromosome chromosome, int beginIndex);
}
