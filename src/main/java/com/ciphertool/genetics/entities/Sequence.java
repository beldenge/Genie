package com.ciphertool.genetics.entities;

public interface Sequence extends Cloneable {
	public Integer getSequenceId();

	public Gene getGene();

	public void setGene(Gene gene);

	public Sequence clone();
}
