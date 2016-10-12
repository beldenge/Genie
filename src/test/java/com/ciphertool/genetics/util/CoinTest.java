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

package com.ciphertool.genetics.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CoinTest {
	private static final int MAX_FLIPS = 100;

	@Test
	public void testFlip() {
		Coin coin = new Coin();
		boolean headsOccurred = false;
		boolean tailsOccurred = false;

		for (int i = 0; i < MAX_FLIPS; i++) {
			Boolean result = coin.flip();

			if (Coin.HEADS.equals(result)) {
				headsOccurred = true;
			}

			if (Coin.TAILS.equals(result)) {
				tailsOccurred = true;
			}

			if (headsOccurred && tailsOccurred) {
				break;
			}
		}

		assertTrue(headsOccurred && tailsOccurred);
	}
}
