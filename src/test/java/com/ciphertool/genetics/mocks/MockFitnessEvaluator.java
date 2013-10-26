package com.ciphertool.genetics.mocks;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.util.fitness.FitnessEvaluator;

public class MockFitnessEvaluator implements FitnessEvaluator {
	private Object geneticStructure;

	@Override
	public Double evaluate(Chromosome chromosome) {
		throw new UnsupportedOperationException("Method stub not yet implemented");
	}

	@Override
	public void setGeneticStructure(Object geneticStructure) {
		this.geneticStructure = geneticStructure;
	}

}
