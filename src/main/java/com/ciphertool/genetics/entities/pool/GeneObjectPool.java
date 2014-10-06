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

package com.ciphertool.genetics.entities.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.factory.GeneObjectFactory;

/*
 * This is an Object pool which eliminates the concurrency burden by maintaining pools of objects for each thread.  This comes with the cost of additional memory usage due to objects not being intrinsically balanced among threads.
 */
public class GeneObjectPool {
	private static AtomicLong numberOfObjectsInUse = new AtomicLong(0);
	private static GeneObjectFactory geneFactory;

	private static Map<Long, List<Gene>> objectsAvailableByThreadMap = new HashMap<Long, List<Gene>>();

	public static long getNumberOfObjectsInUse() {
		return numberOfObjectsInUse.get();
	}

	public static long getNumberOfObjectsAvailable() {
		long totalObjectsAvailable = 0;
		for (List<Gene> geneList : objectsAvailableByThreadMap.values()) {
			totalObjectsAvailable += geneList.size();
		}

		return totalObjectsAvailable;
	}

	public static Gene getNextObjectFromPool() {
		addThreadIdIfNotExists();

		numberOfObjectsInUse.incrementAndGet();

		List<Gene> genePoolForThread = objectsAvailableByThreadMap.get(Thread.currentThread()
				.getId());

		if (genePoolForThread.isEmpty()) {
			return geneFactory.createObject();
		}

		/*
		 * It's faster to remove from the end of the List because no elements
		 * need to shift
		 */
		return genePoolForThread.remove(genePoolForThread.size() - 1);
	}

	public static void returnObjectToPool(Gene wordGene) {
		addThreadIdIfNotExists();

		numberOfObjectsInUse.decrementAndGet();

		wordGene.reset();

		objectsAvailableByThreadMap.get(Thread.currentThread().getId()).add(wordGene);
	}

	protected static void addThreadIdIfNotExists() {
		long currentThreadId = Thread.currentThread().getId();

		if (!objectsAvailableByThreadMap.containsKey(currentThreadId)) {
			objectsAvailableByThreadMap.put(currentThreadId, new ArrayList<Gene>());
		}
	}

	public static void setObjectFactory(GeneObjectFactory geneFactoryToSet) {
		geneFactory = geneFactoryToSet;
	}

	public static void balancePool() {
		List<Gene> entirePool = new ArrayList<Gene>();

		for (List<Gene> geneList : objectsAvailableByThreadMap.values()) {
			int genePoolForThreadSize = geneList.size();

			for (int i = 0; i < genePoolForThreadSize; i++) {
				/*
				 * It's faster to remove from the end of the List because no
				 * elements need to shift
				 */
				entirePool.add(geneList.remove(geneList.size() - 1));
			}
		}

		if (entirePool.isEmpty()) {
			// Nothing to do
			return;
		}

		int eachThreadNewSize = entirePool.size() / objectsAvailableByThreadMap.size();

		int currentThread = 0;
		for (List<Gene> geneList : objectsAvailableByThreadMap.values()) {
			currentThread++;

			if (currentThread == objectsAvailableByThreadMap.size()) {
				// This is the last thread, so just add whatever is left over
				geneList.addAll(entirePool);

			} else {
				for (int i = 0; i < eachThreadNewSize; i++) {
					/*
					 * It's faster to remove from the end of the List because no
					 * elements need to shift
					 */
					geneList.add(entirePool.remove(entirePool.size() - 1));
				}
			}
		}
	}

	public static String toStringStatic() {
		String toString = "WordGeneObjectPool [numberOfObjectsInUse=" + numberOfObjectsInUse
				+ ", numberOfObjectsAvailable=" + getNumberOfObjectsAvailable() + "]  Breakdown:\n";

		for (Long key : objectsAvailableByThreadMap.keySet()) {
			toString += "Thread ID: " + key + ", Size: "
					+ objectsAvailableByThreadMap.get(key).size() + "\n";
		}

		return toString;
	}
}
