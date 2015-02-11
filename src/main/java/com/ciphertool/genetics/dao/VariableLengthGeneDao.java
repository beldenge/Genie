package com.ciphertool.genetics.dao;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;

public interface VariableLengthGeneDao extends GeneDao {
	public Gene findRandomGeneOfLength(Chromosome chromosome, int length);
}
