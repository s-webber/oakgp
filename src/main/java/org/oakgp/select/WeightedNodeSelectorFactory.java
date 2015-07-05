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

import org.oakgp.evaluate.RankedCandidate;
import org.oakgp.util.Random;

/** Returns instances of {@code WeightedNodeSelector}. */
public final class WeightedNodeSelectorFactory implements NodeSelectorFactory {
   private final Random random;

   public WeightedNodeSelectorFactory(Random random) {
      this.random = random;
   }

   @Override
   public WeightedNodeSelector getSelector(List<RankedCandidate> candidates) {
      return new WeightedNodeSelector(random, candidates);
   }
}
