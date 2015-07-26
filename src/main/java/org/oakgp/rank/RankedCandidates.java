/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.rank;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** A sorted immutable collection of {@code RankedCandidate} objects. */
public final class RankedCandidates implements Iterable<RankedCandidate> {
   private static final Comparator<RankedCandidate> COMPARE_TO = (o1, o2) -> o1.compareTo(o2);

   private final RankedCandidate[] sortedCandidates;
   private final int size;

   /**
    * Constructs a new collection of candidates sorted by their natural ordering.
    *
    * @see RankedCandidate#compareTo(RankedCandidate)
    */
   public RankedCandidates(RankedCandidate[] candidates) {
      this(candidates, COMPARE_TO);
   }

   /** Constructs a new collection of candidates sorted according to the given comparator. */
   public RankedCandidates(RankedCandidate[] candidates, Comparator<RankedCandidate> comparator) {
      this.size = candidates.length;
      this.sortedCandidates = Arrays.copyOf(candidates, size);
      Arrays.sort(sortedCandidates, comparator);
   }

   /**
    * Returns the candidate at the specified position in this collection.
    *
    * @param index
    *           index of the candidate to return
    * @return the candidate at the specified position in this collection
    * @throws ArrayIndexOutOfBoundsException
    *            if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   public RankedCandidate get(int index) {
      return sortedCandidates[index];
   }

   /** Returns the best candidate in this collection. */
   public RankedCandidate best() {
      return sortedCandidates[0];
   }

   /** Returns the number of candidates in this collection. */
   public int size() {
      return size;
   }

   /** Returns a sequential {@code Stream} with this collection as its source. */
   public Stream<RankedCandidate> stream() {
      return Arrays.stream(sortedCandidates);
   }

   @Override
   public Iterator<RankedCandidate> iterator() {
      return new RankedCandidatesIterator();
   }

   private class RankedCandidatesIterator implements Iterator<RankedCandidate> {
      private final AtomicInteger ctr = new AtomicInteger();

      @Override
      public boolean hasNext() {
         return ctr.get() < size;
      }

      @Override
      public RankedCandidate next() {
         int next = ctr.getAndIncrement();
         if (next < size) {
            return sortedCandidates[next];
         } else {
            throw new NoSuchElementException();
         }
      }
   }
}
