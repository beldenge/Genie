/**
 * Copyright 2015 George Belden
 * 
 * This file is part of ZodiacGenetics.
 * 
 * ZodiacGenetics is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ZodiacGenetics is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ZodiacGenetics. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.mutation;

public enum MutationAlgorithmType {
	LIBERAL("Liberal", LiberalMutationAlgorithm.class,
			"This will mutate genes by index regardless of starting position and end position"),
	CONSERVATIVE(
			"Conservative",
			ConservativeMutationAlgorithm.class,
			"This will mutate genes, only replacing them with genes that match on starting position and end position"),
	GROUP("Group", GroupMutationAlgorithm.class,
			"This mutates groups of genes instead of individual genes"),
	SINGLE_SEQUENCE("Single Sequence", SingleSequenceMutationAlgorithm.class,
			"This will mutate a single sequence of a specified gene");

	private String displayName;
	private Class<? extends MutationAlgorithm> type;
	private String description;

	MutationAlgorithmType(String displayName, Class<? extends MutationAlgorithm> type,
			String description) {
		this.displayName = displayName;
		this.type = type;
		this.description = description;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the types
	 */
	public Class<? extends MutationAlgorithm> getType() {
		return type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the value returned by name()
	 */
	public String getName() {
		return name();
	}
}
