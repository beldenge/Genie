/**
 * Copyright 2015 George Belden
 * 
 * This file is part of Genie.
 * 
 * Genie is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Genie is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Genie. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.entities;

public class Ancestry {
	private String dad;
	private String mom;
	private Ancestry maternal;
	private Ancestry paternal;

	public Ancestry(String dad, String mom, Ancestry maternal, Ancestry paternal, int generationsToKeep) {
		this.dad = dad;
		this.mom = mom;
		this.maternal = maternal;
		this.paternal = paternal;
		truncateAncestry(generationsToKeep);
	}

	/**
	 * @param dad
	 *            the dad to set
	 */
	public void setDad(String dad) {
		this.dad = dad;
	}

	/**
	 * @return the mom
	 */
	public String getMom() {
		return mom;
	}

	private void truncateAncestry(int generationsToKeep) {
		if (generationsToKeep <= 0) {
			this.maternal = null;
			this.paternal = null;
		}

		generationsToKeep--;
		if (this.maternal != null) {
			this.maternal.truncateAncestry(generationsToKeep);
		}

		if (this.paternal != null) {
			this.paternal.truncateAncestry(generationsToKeep);
		}
	}

	public boolean sharesLineageWith(Ancestry other, int generationsToSkip) {
		if (generationsToSkip > 0
				&& (other.mom.equals(this.mom) || other.mom.equals(this.dad) || other.dad.equals(this.dad) || other.dad
						.equals(this.mom))) {
			// These two lineages don't branch far enough
			return false;
		}
		if (generationsToSkip <= 0
				&& (other.mom.equals(this.mom) || other.mom.equals(this.dad) || other.dad.equals(this.dad) || other.dad
						.equals(this.mom))) {
			return true;
		} else {
			generationsToSkip--;

			return this.maternal != null
					&& this.paternal != null
					&& other.maternal != null
					&& other.paternal != null
					&& (other.maternal.sharesLineageWith(this.maternal, generationsToSkip)
							|| other.maternal.sharesLineageWith(this.paternal, generationsToSkip)
							|| other.paternal.sharesLineageWith(this.paternal, generationsToSkip) || other.paternal
								.sharesLineageWith(this.maternal, generationsToSkip));
		}
	}
}
