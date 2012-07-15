package com.ciphertool.genetics;

public interface GeneListDao {
	public Gene findRandomGene(Chromosome chromosome, int beginIndex);
}
