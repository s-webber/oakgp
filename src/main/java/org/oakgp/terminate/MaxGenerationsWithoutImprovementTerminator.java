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

/** A predicate that returns {@code true} when a specified number of generations has since the last change in the fitness of candidates. */
public final class MaxGenerationsWithoutImprovementTerminator implements Predicate<List<RankedCandidate>> {
   private final int maxGenerationsWithoutImprovement;
   private int currentGenerationsWithoutImprovement;
   private double currentBest;

   public MaxGenerationsWithoutImprovementTerminator(int maxGenerationsWithoutImprovement) {
      this.maxGenerationsWithoutImprovement = maxGenerationsWithoutImprovement;
   }

   @Override
   public boolean test(List<RankedCandidate> t) {
      double best = t.get(0).getFitness();
      if (best != currentBest) {
         currentGenerationsWithoutImprovement = 0;
         currentBest = best;
         return false;
      } else {
         return ++currentGenerationsWithoutImprovement >= maxGenerationsWithoutImprovement;
      }
   }
}
