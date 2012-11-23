package com.ciphertool.genetics.algorithms;

public enum CrossoverAlgorithmType {
	LIBERAL("Liberal", LiberalCrossoverAlgorithm.class,
			"This will crossover words by index regardless of starting position and end position"),
	CONSERVATIVE("Conservative", ConservativeCrossoverAlgorithm.class,
			"This will only crossover words that match on starting position and end position"),
	LOWEST_COMMON_GROUP("Lowest Common Group", LowestCommonGroupCrossoverAlgorithm.class,
			"This will crossover groups of words that match on starting position and end position");

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
