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

package com.ciphertool.genetics.algorithms.selection;

public enum SelectionAlgorithmType {
	TRUNCATION(
			"Truncation",
			TruncationSelectionAlgorithm.class,
			"Performs selection by simply dropping all of the individuals that fall below the survival rate, based on fitness. So only the most fit individuals will survive."),
	PROBABILISTIC("Probabilistic", ProbabilisticSelectionAlgorithm.class,
			"Performs selection by giving each individual a probabilistic chance of survival, weighted by its fitness."),
	TOURNAMENT("Tournament", TournamentSelectionAlgorithm.class,
			"Performs selection by running probabilistic tournaments on small groups of individuals.");

	private String displayName;
	private Class<? extends SelectionAlgorithm> type;
	private String description;

	SelectionAlgorithmType(String displayName, Class<? extends SelectionAlgorithm> type, String description) {
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
	public Class<? extends SelectionAlgorithm> getType() {
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