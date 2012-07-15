package com.ciphertool.genetics;

public interface Sequence extends Cloneable {
	public Gene getGene();

	public void setGene(Gene gene);

	public Sequence clone();
}
