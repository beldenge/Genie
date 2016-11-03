package com.ciphertool.genetics.algorithms.selection;

public class BinaryRouletteTree {
	private BinaryRouletteNode root;

	public BinaryRouletteTree() {
	}

	public void insert(BinaryRouletteNode toInsert) {
		if (this.root == null) {
			this.root = toInsert;

			return;
		}

		insertNode(root, toInsert);
	}

	protected void insertNode(BinaryRouletteNode parent, BinaryRouletteNode toInsert) {
		if (toInsert.getValue() < parent.getValue()) {
			if (parent.getLessThan() == null) {
				parent.setLessThan(toInsert);

				return;
			}

			insertNode(parent.getLessThan(), toInsert);

			return;
		}

		if (parent.getGreaterThan() == null) {
			parent.setGreaterThan(toInsert);

			return;
		}

		insertNode(parent.getGreaterThan(), toInsert);
	}

	public BinaryRouletteNode find(double value) {
		return findNode(this.root, value, null);
	}

	protected BinaryRouletteNode findNode(BinaryRouletteNode current, double value, BinaryRouletteNode closestSoFar) {
		if (value <= current.getValue()) {
			if (current.getLessThan() == null) {
				return current;
			}

			if (value > current.getLessThan().getValue()) {
				closestSoFar = current;
			}

			return findNode(current.getLessThan(), value, closestSoFar);
		}

		if (current.getGreaterThan() == null) {
			return closestSoFar;
		}

		return findNode(current.getGreaterThan(), value, closestSoFar);
	}
}
