package com.ciphertool.genetics.algorithms.selection;

public class BinaryRouletteNode {
	private double				value;
	private int					index;
	private BinaryRouletteNode	lessThan;
	private BinaryRouletteNode	greaterThan;

	/**
	 * @param index
	 *            the index to set
	 * 
	 * @param value
	 *            the value to set
	 */
	public BinaryRouletteNode(int index, double value) {
		this.index = index;
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the lessThan
	 */
	public BinaryRouletteNode getLessThan() {
		return lessThan;
	}

	/**
	 * @param lessThan
	 *            the lessThan to set
	 */
	public void setLessThan(BinaryRouletteNode lessThan) {
		this.lessThan = lessThan;
	}

	/**
	 * @return the greaterThan
	 */
	public BinaryRouletteNode getGreaterThan() {
		return greaterThan;
	}

	/**
	 * @param greaterThan
	 *            the greaterThan to set
	 */
	public void setGreaterThan(BinaryRouletteNode greaterThan) {
		this.greaterThan = greaterThan;
	}
}
