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
package org.oakgp.select;

import java.util.List;

import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;
import org.oakgp.util.Random;

/** Randomly selects nodes - with a bias to selecting nodes that have the best fitness. */
public final class WeightedNodeSelector implements NodeSelector {
   private final Random random;
   private final List<RankedCandidate> candidates;
   private final int size;

   public WeightedNodeSelector(Random random, List<RankedCandidate> candidates) {
      this.random = random;
      this.candidates = candidates;
      this.size = candidates.size();
   }

   @Override
   public Node next() {
      for (int i = 0; i < size - 1; i++) {
         if (random.nextBoolean()) {
            return candidates.get(i).getNode();
         }
      }
      return candidates.get(size - 1).getNode();
   }
}
