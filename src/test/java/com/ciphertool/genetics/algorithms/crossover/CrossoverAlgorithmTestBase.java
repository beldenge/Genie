package com.ciphertool.genetics.algorithms.crossover;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class CrossoverAlgorithmTestBase {

	protected static Chromosome getMom() {
		MockChromosome momChromosome = new MockChromosome();

		MockGene momGene1 = new MockGene();
		momGene1.addSequence(new MockSequence("o", 1));
		momGene1.addSequence(new MockSequence("n", 2));
		momGene1.addSequence(new MockSequence("e", 3));
		momChromosome.addGene(momGene1);

		MockGene momGene2 = new MockGene();
		momGene2.addSequence(new MockSequence("t", 4));
		momGene2.addSequence(new MockSequence("w", 5));
		momGene2.addSequence(new MockSequence("o", 6));
		momChromosome.addGene(momGene2);

		MockGene momGene3 = new MockGene();
		momGene3.addSequence(new MockSequence("t", 7));
		momGene3.addSequence(new MockSequence("h", 8));
		momGene3.addSequence(new MockSequence("r", 9));
		momGene3.addSequence(new MockSequence("e", 10));
		momGene3.addSequence(new MockSequence("e", 11));
		momChromosome.addGene(momGene3);

		MockGene momGene4 = new MockGene();
		momGene4.addSequence(new MockSequence("f", 12));
		momGene4.addSequence(new MockSequence("o", 13));
		momGene4.addSequence(new MockSequence("u", 14));
		momGene4.addSequence(new MockSequence("r", 15));
		momChromosome.addGene(momGene4);

		MockGene momGene5 = new MockGene();
		momGene5.addSequence(new MockSequence("f", 16));
		momGene5.addSequence(new MockSequence("i", 17));
		momGene5.addSequence(new MockSequence("v", 18));
		momGene5.addSequence(new MockSequence("e", 19));
		momChromosome.addGene(momGene5);

		MockGene momGene6 = new MockGene();
		momGene6.addSequence(new MockSequence("t", 20));
		momGene6.addSequence(new MockSequence("w", 21));
		momGene6.addSequence(new MockSequence("e", 22));
		momGene6.addSequence(new MockSequence("l", 23));
		momGene6.addSequence(new MockSequence("v", 24));
		momGene6.addSequence(new MockSequence("e", 25));
		momChromosome.addGene(momGene6);

		momChromosome.setTargetSize(25);

		return momChromosome;
	}

	protected static Chromosome getDad() {
		MockChromosome dadChromosome = new MockChromosome();

		MockGene dadGene1 = new MockGene();
		dadGene1.addSequence(new MockSequence("s", 1));
		dadGene1.addSequence(new MockSequence("i", 2));
		dadGene1.addSequence(new MockSequence("x", 3));
		dadChromosome.addGene(dadGene1);

		MockGene dadGene2 = new MockGene();
		dadGene2.addSequence(new MockSequence("f", 4));
		dadGene2.addSequence(new MockSequence("i", 5));
		dadGene2.addSequence(new MockSequence("f", 6));
		dadGene2.addSequence(new MockSequence("t", 7));
		dadGene2.addSequence(new MockSequence("y", 8));
		dadChromosome.addGene(dadGene2);

		MockGene dadGene3 = new MockGene();
		dadGene3.addSequence(new MockSequence("t", 9));
		dadGene3.addSequence(new MockSequence("e", 10));
		dadGene3.addSequence(new MockSequence("n", 11));
		dadChromosome.addGene(dadGene3);

		MockGene dadGene4 = new MockGene();
		dadGene4.addSequence(new MockSequence("n", 12));
		dadGene4.addSequence(new MockSequence("i", 13));
		dadGene4.addSequence(new MockSequence("n", 14));
		dadGene4.addSequence(new MockSequence("e", 15));
		dadChromosome.addGene(dadGene4);

		MockGene dadGene5 = new MockGene();
		dadGene5.addSequence(new MockSequence("z", 16));
		dadGene5.addSequence(new MockSequence("e", 17));
		dadGene5.addSequence(new MockSequence("r", 18));
		dadGene5.addSequence(new MockSequence("o", 19));
		dadChromosome.addGene(dadGene5);

		MockGene dadGene6 = new MockGene();
		dadGene6.addSequence(new MockSequence("e", 20));
		dadGene6.addSequence(new MockSequence("i", 21));
		dadGene6.addSequence(new MockSequence("g", 22));
		dadGene6.addSequence(new MockSequence("h", 23));
		dadGene6.addSequence(new MockSequence("t", 24));
		dadGene6.addSequence(new MockSequence("y", 25));
		dadChromosome.addGene(dadGene6);

		dadChromosome.setTargetSize(25);

		return dadChromosome;
	}
}
