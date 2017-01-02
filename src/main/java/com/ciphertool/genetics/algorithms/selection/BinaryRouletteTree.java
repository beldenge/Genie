package com.ciphertool.genetics.algorithms.selection;

import java.math.BigDecimal;

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
		if (toInsert.getValue().compareTo(parent.getValue()) < 0) {
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

	public BinaryRouletteNode find(BigDecimal value) {
		return findNode(this.root, value, null);
	}

	protected BinaryRouletteNode findNode(BinaryRouletteNode current, BigDecimal value,
			BinaryRouletteNode closestSoFar) {
		if (value.compareTo(current.getValue()) <= 0) {
			if (current.getLessThan() == null) {
				return current;
			}

			if (value.compareTo(current.getLessThan().getValue()) > 0) {
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
