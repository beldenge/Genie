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

import com.ciphertool.genetics.entities.Sequence;
import com.ciphertool.genetics.entities.factory.SequenceObjectFactory;

public class SequenceObjectPool {
	private static AtomicLong numberOfObjectsInUse = new AtomicLong(0);
	private static SequenceObjectFactory sequenceFactory;

	private static Map<Long, List<Sequence>> objectsAvailableByThreadMap = new HashMap<Long, List<Sequence>>();

	public static long getNumberOfObjectsInUse() {
		return numberOfObjectsInUse.get();
	}

	public static long getNumberOfObjectsAvailable() {
		long totalObjectsAvailable = 0;
		for (List<Sequence> sequenceList : objectsAvailableByThreadMap.values()) {
			totalObjectsAvailable += sequenceList.size();
		}

		return totalObjectsAvailable;
	}

	public static Sequence getNextObjectFromPool() {
		addThreadIdIfNotExists();

		numberOfObjectsInUse.incrementAndGet();

		List<Sequence> sequencePoolForThread = objectsAvailableByThreadMap.get(Thread
				.currentThread().getId());

		if (sequencePoolForThread.isEmpty()) {
			return sequenceFactory.createObject();
		}

		/*
		 * It's faster to remove from the end of the List because no elements
		 * need to shift
		 */
		return sequencePoolForThread.remove(sequencePoolForThread.size() - 1);
	}

	public static void returnObjectToPool(Sequence plaintextSequence) {
		addThreadIdIfNotExists();

		numberOfObjectsInUse.decrementAndGet();

		plaintextSequence.reset();

		objectsAvailableByThreadMap.get(Thread.currentThread().getId()).add(plaintextSequence);
	}

	protected static void addThreadIdIfNotExists() {
		long currentThreadId = Thread.currentThread().getId();

		if (!objectsAvailableByThreadMap.containsKey(currentThreadId)) {
			objectsAvailableByThreadMap.put(currentThreadId, new ArrayList<Sequence>());
		}
	}

	public static void setObjectFactory(SequenceObjectFactory sequenceFactoryToSet) {
		sequenceFactory = sequenceFactoryToSet;
	}

	public static void balancePool() {
		List<Sequence> entirePool = new ArrayList<Sequence>();

		for (List<Sequence> sequenceList : objectsAvailableByThreadMap.values()) {
			int sequencePoolForThreadSize = sequenceList.size();

			for (int i = 0; i < sequencePoolForThreadSize; i++) {
				/*
				 * It's faster to remove from the end of the List because no
				 * elements need to shift
				 */
				entirePool.add(sequenceList.remove(sequenceList.size() - 1));
			}
		}

		if (entirePool.isEmpty()) {
			// Nothing to do
			return;
		}

		int eachThreadNewSize = entirePool.size() / objectsAvailableByThreadMap.size();

		int currentThread = 0;
		for (List<Sequence> sequenceList : objectsAvailableByThreadMap.values()) {
			currentThread++;

			if (currentThread == objectsAvailableByThreadMap.size()) {
				// This is the last thread, so just add whatever is left over
				sequenceList.addAll(entirePool);

			} else {
				for (int i = 0; i < eachThreadNewSize; i++) {
					/*
					 * It's faster to remove from the end of the List because no
					 * elements need to shift
					 */
					sequenceList.add(entirePool.remove(entirePool.size() - 1));
				}
			}

		}
	}

	public static String toStringStatic() {
		String toString = "PlaintextSequenceObjectPool [numberOfObjectsInUse="
				+ numberOfObjectsInUse + ", numberOfObjectsAvailable="
				+ getNumberOfObjectsAvailable() + "]  Breakdown:\n";

		for (Long key : objectsAvailableByThreadMap.keySet()) {
			toString += "Thread ID: " + key + ", Size: "
					+ objectsAvailableByThreadMap.get(key).size() + "\n";
		}

		return toString;
	}
}
