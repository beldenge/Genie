package com.ciphertool.genetics.util;

import com.ciphertool.genetics.entities.Chromosome;

public class MaximizationFitnessComparator implements FitnessComparator {

	@Override
	public int compare(Chromosome c1, Chromosome c2) {
		if (c1.getFitness() > c2.getFitness()) {
			return 1;
		} else if (c1.getFitness() < c2.getFitness()) {
			return -1;
		}

		return 0;
	}

}
