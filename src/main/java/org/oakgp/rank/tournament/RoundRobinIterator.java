/*
 * Copyright 2019 S. Webber
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
package org.oakgp.rank.tournament;

import java.util.Iterator;

import org.oakgp.rank.tournament.RoundRobinIterator.Pair;

/** Note: NOT thread-safe. */
class RoundRobinIterator<T> implements Iterator<Pair<T>> {
   private final T[] candidates;
   private final int length;
   private int i1;
   private int i2 = 1;

   public RoundRobinIterator(T[] candidates) {
      this.length = candidates.length;
      if (this.length < 2) {
         throw new IllegalArgumentException();
      }
      this.candidates = candidates;
   }

   @Override
   public boolean hasNext() {
      return i1 < length - 1;
   }

   @Override
   public Pair<T> next() {
      Pair<T> result = new Pair<>(candidates[i1], candidates[i2]);
      if (++i2 == length) {
         i1++;
         i2 = i1 + 1;
      }
      return result;
   }

   static class Pair<T> { // TODO replace with Java 9 version
      private final T left;
      private final T right;

      Pair(T left, T right) {
         this.left = left;
         this.right = right;
      }

      T getLeft() {
         return left;
      }

      T getRight() {
         return right;
      }
   }
}
