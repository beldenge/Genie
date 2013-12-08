/**
 * Copyright 2013 George Belden
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

package com.ciphertool.genetics.algorithms.crossover;

public enum CrossoverAlgorithmType {
	LIBERAL("Liberal", LiberalCrossoverAlgorithm.class,
			"This will crossover words by index regardless of starting position and end position"),
	CONSERVATIVE("Conservative", ConservativeCrossoverAlgorithm.class,
			"This will only crossover words that match on starting position and end position"),
	LOWEST_COMMON_GROUP(
			"Lowest Common Group",
			LowestCommonGroupCrossoverAlgorithm.class,
			"This will crossover groups of words that match on starting position and end position only if it results in a better fit Chromosome."),
	LIBERAL_UNEVALUATED(
			"Liberal Unevaluated",
			LiberalUnevaluatedCrossoverAlgorithm.class,
			"This will crossover words by index regardless of starting position and end position.  It produces child Chromosomes regardless of whether they are better fit."),
	CONSERVATIVE_UNEVALUATED(
			"Conservative Unevaluated",
			ConservativeUnevaluatedCrossoverAlgorithm.class,
			"This will only crossover words that match on starting position and end position.  It produces child Chromosomes regardless of whether they are better fit."),
	LOWEST_COMMON_GROUP_UNEVALUATED(
			"Lowest Common Group Unevaluated",
			LowestCommonGroupUnevaluatedCrossoverAlgorithm.class,
			"This will crossover groups of words that match on starting position and end position.  It produces child Chromosomes regardless of whether they are better fit."),
	CONSERVATIVE_CENTROMERE("Conservative Centromere",
			ConservativeCentromereCrossoverAlgorithm.class,
			"This will crossover from a common centromere position determined somewhat randomly");

	private String displayName;
	private Class<? extends CrossoverAlgorithm> type;
	private String description;

	CrossoverAlgorithmType(String displayName, Class<? extends CrossoverAlgorithm> type,
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
	public Class<? extends CrossoverAlgorithm> getType() {
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
