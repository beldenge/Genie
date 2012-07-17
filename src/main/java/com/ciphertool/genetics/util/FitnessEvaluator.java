package com.ciphertool.genetics.util;

import com.ciphertool.genetics.entities.Chromosome;

public interface FitnessEvaluator {
	public int evaluate(Chromosome chromosome);
}
