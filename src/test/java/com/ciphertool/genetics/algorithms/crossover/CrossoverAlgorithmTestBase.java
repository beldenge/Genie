package com.ciphertool.genetics.algorithms.crossover;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class CrossoverAlgorithmTestBase {

	protected static Chromosome getMom() {
		MockChromosome momChromosome = new MockChromosome();

		MockGene momGene1 = new MockGene();
		momGene1.addSequence(new MockSequence("o"));
		momGene1.addSequence(new MockSequence("n"));
		momGene1.addSequence(new MockSequence("e"));
		momChromosome.addGene(momGene1);

		MockGene momGene2 = new MockGene();
		momGene2.addSequence(new MockSequence("t"));
		momGene2.addSequence(new MockSequence("w"));
		momGene2.addSequence(new MockSequence("o"));
		momChromosome.addGene(momGene2);

		MockGene momGene3 = new MockGene();
		momGene3.addSequence(new MockSequence("t"));
		momGene3.addSequence(new MockSequence("h"));
		momGene3.addSequence(new MockSequence("r"));
		momGene3.addSequence(new MockSequence("e"));
		momGene3.addSequence(new MockSequence("e"));
		momChromosome.addGene(momGene3);

		MockGene momGene4 = new MockGene();
		momGene4.addSequence(new MockSequence("f"));
		momGene4.addSequence(new MockSequence("o"));
		momGene4.addSequence(new MockSequence("u"));
		momGene4.addSequence(new MockSequence("r"));
		momChromosome.addGene(momGene4);

		MockGene momGene5 = new MockGene();
		momGene5.addSequence(new MockSequence("f"));
		momGene5.addSequence(new MockSequence("i"));
		momGene5.addSequence(new MockSequence("v"));
		momGene5.addSequence(new MockSequence("e"));
		momChromosome.addGene(momGene5);

		MockGene momGene6 = new MockGene();
		momGene6.addSequence(new MockSequence("t"));
		momGene6.addSequence(new MockSequence("w"));
		momGene6.addSequence(new MockSequence("e"));
		momGene6.addSequence(new MockSequence("l"));
		momGene6.addSequence(new MockSequence("v"));
		momGene6.addSequence(new MockSequence("e"));
		momChromosome.addGene(momGene6);

		return momChromosome;
	}

	protected static Chromosome getDad() {
		MockChromosome dadChromosome = new MockChromosome();

		MockGene dadGene1 = new MockGene();
		dadGene1.addSequence(new MockSequence("s"));
		dadGene1.addSequence(new MockSequence("i"));
		dadGene1.addSequence(new MockSequence("x"));
		dadChromosome.addGene(dadGene1);

		MockGene dadGene2 = new MockGene();
		dadGene2.addSequence(new MockSequence("f"));
		dadGene2.addSequence(new MockSequence("i"));
		dadGene2.addSequence(new MockSequence("f"));
		dadGene2.addSequence(new MockSequence("t"));
		dadGene2.addSequence(new MockSequence("y"));
		dadChromosome.addGene(dadGene2);

		MockGene dadGene3 = new MockGene();
		dadGene3.addSequence(new MockSequence("t"));
		dadGene3.addSequence(new MockSequence("e"));
		dadGene3.addSequence(new MockSequence("n"));
		dadChromosome.addGene(dadGene3);

		MockGene dadGene4 = new MockGene();
		dadGene4.addSequence(new MockSequence("n"));
		dadGene4.addSequence(new MockSequence("i"));
		dadGene4.addSequence(new MockSequence("n"));
		dadGene4.addSequence(new MockSequence("e"));
		dadChromosome.addGene(dadGene4);

		MockGene dadGene5 = new MockGene();
		dadGene5.addSequence(new MockSequence("z"));
		dadGene5.addSequence(new MockSequence("e"));
		dadGene5.addSequence(new MockSequence("r"));
		dadGene5.addSequence(new MockSequence("o"));
		dadChromosome.addGene(dadGene5);

		MockGene dadGene6 = new MockGene();
		dadGene6.addSequence(new MockSequence("e"));
		dadGene6.addSequence(new MockSequence("i"));
		dadGene6.addSequence(new MockSequence("g"));
		dadGene6.addSequence(new MockSequence("h"));
		dadGene6.addSequence(new MockSequence("t"));
		dadGene6.addSequence(new MockSequence("y"));
		dadChromosome.addGene(dadGene6);

		return dadChromosome;
	}
}
