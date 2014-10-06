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
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.ciphertool.genetics.entities.Sequence;
import com.ciphertool.genetics.entities.factory.SequenceObjectFactory;

/*
 * This is an Object pool which lessens the concurrency burden by reading and writing to separate Lists, and then synchronizing the changes to the Lists at a single-threaded point in the application.
 */
public class TwoListSequenceObjectPool {
	private static AtomicLong numberOfObjectsInUse = new AtomicLong(0);
	private static SequenceObjectFactory sequenceFactory;

	// Objects which have been freed, but are not yet available
	private static List<Sequence> freedObjects = new ArrayList<Sequence>();

	// Objects which are available for use
	private static List<Sequence> availableObjects = new ArrayList<Sequence>();

	public static long getNumberOfObjectsInUse() {
		return numberOfObjectsInUse.get();
	}

	public static Sequence getNextObjectFromPool() {
		numberOfObjectsInUse.incrementAndGet();

		synchronized (TwoListSequenceObjectPool.class) {
			if (availableObjects.isEmpty()) {
				return sequenceFactory.createObject();
			}

			/*
			 * It's faster to remove from the end of the List because no
			 * elements need to shift
			 */
			return availableObjects.remove(availableObjects.size() - 1);
		}
	}

	public static void returnObjectToPool(Sequence plaintextSequence) {
		numberOfObjectsInUse.decrementAndGet();

		plaintextSequence.reset();

		synchronized (TwoListSequenceObjectPool.class) {
			freedObjects.add(plaintextSequence);
		}
	}

	public static void setObjectFactory(SequenceObjectFactory sequenceFactoryToSet) {
		sequenceFactory = sequenceFactoryToSet;
	}

	/*
	 * This method should only ever be called by one thread, but it is
	 * synchronized just in case
	 */
	public synchronized static void balancePool() {
		availableObjects.addAll(freedObjects);

		freedObjects.clear();
	}

	public static String toStringStatic() {
		String toString = "TwoListSequenceObjectPool [numberOfObjectsInUse=" + numberOfObjectsInUse
				+ ", numberOfObjectsAvailable=" + availableObjects.size()
				+ ", numberOfObjectsFreed=" + freedObjects.size() + "]";

		return toString;
	}
}
