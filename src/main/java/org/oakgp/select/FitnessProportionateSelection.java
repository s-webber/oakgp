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

import org.oakgp.node.Node;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.util.Random;

/**
 * The fitness of candidates is used to determine the probability that they will be selected.
 * <p>
 * Also known as roulette wheel selection.
 */
public final class FitnessProportionateSelection implements NodeSelector {
   private final Random random;
   private final RankedCandidates candidates;
   private final int size;
   private final double sumFitness;

   public FitnessProportionateSelection(Random random, RankedCandidates candidates) {
      this.random = random;
      this.candidates = candidates;
      this.size = candidates.size();
      this.sumFitness = sumFitness(candidates);
   }

   private static double sumFitness(RankedCandidates candidates) {
      // return candidates.stream().mapToDouble(c -> c.getFitness()).sum();
      double s = 0;
      for (RankedCandidate c : candidates) {
         s += c.getFitness();
      }
      return s;
   }

   @Override
   public Node next() {
      final double r = random.nextDouble();
      double p = 0;
      for (int i = 0; i < size; i++) {
         RankedCandidate c = candidates.get(i);
         p += c.getFitness() / sumFitness;
         if (r < p) {
            return c.getNode();
         }
      }
      // should only get here if rounding error - default to selecting the best candidate
      return candidates.best().getNode();
   }
}
