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
package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;

import org.oakgp.evaluate.RankedCandidate;

/**
 * A predicate that will return {@code true} when any of it component predicates return {@code true}.
 * <p>
 * A composite of multiple termination criteria.
 */
public final class CompositeTerminator implements Predicate<List<RankedCandidate>> {
   private final Predicate<List<RankedCandidate>>[] terminators;

   @SafeVarargs
   public CompositeTerminator(Predicate<List<RankedCandidate>>... terminators) {
      this.terminators = terminators;
   }

   @Override
   public boolean test(List<RankedCandidate> candidates) {
      for (Predicate<List<RankedCandidate>> t : terminators) {
         if (t.test(candidates)) {
            return true;
         }
      }
      return false;
   }
}
