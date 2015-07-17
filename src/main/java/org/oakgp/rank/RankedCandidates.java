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

/** A sorted immutable collection of {@code RankedCandidate} objects. */
public final class RankedCandidates implements Iterable<RankedCandidate> {
   private static final Comparator<RankedCandidate> COMPARE_TO = (o1, o2) -> o1.compareTo(o2);

   private final RankedCandidate[] sortedCandidates;
   private final int size;

   public RankedCandidates(RankedCandidate[] candidates) {
      this(candidates, COMPARE_TO);
   }

   public RankedCandidates(RankedCandidate[] candidates, Comparator<RankedCandidate> comparator) {
      this.size = candidates.length;
      this.sortedCandidates = Arrays.copyOf(candidates, size);
      Arrays.sort(sortedCandidates, comparator);
   }

   public RankedCandidate get(int i) {
      return sortedCandidates[i];
   }

   public RankedCandidate best() {
      return sortedCandidates[0];
   }

   public int size() {
      return size;
   }

   @Override
   public Iterator<RankedCandidate> iterator() {
      return new RankedCandidatesIterator();
   }

   private class RankedCandidatesIterator implements Iterator<RankedCandidate> {
      private int ctr;

      @Override
      public boolean hasNext() {
         return ctr < size;
      }

      @Override
      public synchronized RankedCandidate next() {
         return sortedCandidates[ctr++];
      }
   }
}
