package com.ciphertool.genetics.algorithms.crossover;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class CrossoverAlgorithmTestBase {

	protected static Chromosome getMom() {
		MockChromosome momChromosome = new MockChromosome();

		MockGene momGene1 = new MockGene();
		momGene1.addSequence(new MockSequence("o", 0));
		momGene1.addSequence(new MockSequence("n", 1));
		momGene1.addSequence(new MockSequence("e", 2));
		momChromosome.addGene(momGene1);

		MockGene momGene2 = new MockGene();
		momGene2.addSequence(new MockSequence("t", 3));
		momGene2.addSequence(new MockSequence("w", 4));
		momGene2.addSequence(new MockSequence("o", 5));
		momChromosome.addGene(momGene2);

		MockGene momGene3 = new MockGene();
		momGene3.addSequence(new MockSequence("t", 6));
		momGene3.addSequence(new MockSequence("h", 7));
		momGene3.addSequence(new MockSequence("r", 8));
		momGene3.addSequence(new MockSequence("e", 9));
		momGene3.addSequence(new MockSequence("e", 10));
		momChromosome.addGene(momGene3);

		MockGene momGene4 = new MockGene();
		momGene4.addSequence(new MockSequence("f", 11));
		momGene4.addSequence(new MockSequence("o", 12));
		momGene4.addSequence(new MockSequence("u", 13));
		momGene4.addSequence(new MockSequence("r", 14));
		momChromosome.addGene(momGene4);

		MockGene momGene5 = new MockGene();
		momGene5.addSequence(new MockSequence("f", 15));
		momGene5.addSequence(new MockSequence("i", 16));
		momGene5.addSequence(new MockSequence("v", 17));
		momGene5.addSequence(new MockSequence("e", 18));
		momChromosome.addGene(momGene5);

		MockGene momGene6 = new MockGene();
		momGene6.addSequence(new MockSequence("t", 19));
		momGene6.addSequence(new MockSequence("w", 20));
		momGene6.addSequence(new MockSequence("e", 21));
		momGene6.addSequence(new MockSequence("l", 22));
		momGene6.addSequence(new MockSequence("v", 23));
		momGene6.addSequence(new MockSequence("e", 24));
		momChromosome.addGene(momGene6);

		momChromosome.setTargetSize(25);

		return momChromosome;
	}

	protected static Chromosome getDad() {
		MockChromosome dadChromosome = new MockChromosome();

		MockGene dadGene1 = new MockGene();
		dadGene1.addSequence(new MockSequence("s", 0));
		dadGene1.addSequence(new MockSequence("i", 1));
		dadGene1.addSequence(new MockSequence("x", 2));
		dadChromosome.addGene(dadGene1);

		MockGene dadGene2 = new MockGene();
		dadGene2.addSequence(new MockSequence("f", 3));
		dadGene2.addSequence(new MockSequence("i", 4));
		dadGene2.addSequence(new MockSequence("f", 5));
		dadGene2.addSequence(new MockSequence("t", 6));
		dadGene2.addSequence(new MockSequence("y", 7));
		dadChromosome.addGene(dadGene2);

		MockGene dadGene3 = new MockGene();
		dadGene3.addSequence(new MockSequence("t", 8));
		dadGene3.addSequence(new MockSequence("e", 9));
		dadGene3.addSequence(new MockSequence("n", 10));
		dadChromosome.addGene(dadGene3);

		MockGene dadGene4 = new MockGene();
		dadGene4.addSequence(new MockSequence("n", 11));
		dadGene4.addSequence(new MockSequence("i", 12));
		dadGene4.addSequence(new MockSequence("n", 13));
		dadGene4.addSequence(new MockSequence("e", 14));
		dadChromosome.addGene(dadGene4);

		MockGene dadGene5 = new MockGene();
		dadGene5.addSequence(new MockSequence("z", 15));
		dadGene5.addSequence(new MockSequence("e", 16));
		dadGene5.addSequence(new MockSequence("r", 17));
		dadGene5.addSequence(new MockSequence("o", 18));
		dadChromosome.addGene(dadGene5);

		MockGene dadGene6 = new MockGene();
		dadGene6.addSequence(new MockSequence("e", 19));
		dadGene6.addSequence(new MockSequence("i", 20));
		dadGene6.addSequence(new MockSequence("g", 21));
		dadGene6.addSequence(new MockSequence("h", 22));
		dadGene6.addSequence(new MockSequence("t", 23));
		dadGene6.addSequence(new MockSequence("y", 24));
		dadChromosome.addGene(dadGene6);

		dadChromosome.setTargetSize(25);

		return dadChromosome;
	}
}
