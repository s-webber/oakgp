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
package org.oakgp.rank.fitness;

import java.util.Collection;

import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;

/** Ranks and sorts the fitness of {@code Node} instances using a {@code FitnessFunction}. */
public final class FitnessFunctionGenerationRanker implements GenerationRanker {
   private final FitnessFunction fitnessFunction;

   /**
    * Constructs a {@code GenerationRanker} with the specified {@code FitnessFunction}.
    *
    * @param fitnessFunction
    *           the {@code FitnessFunction} to use when determining the fitness of candidates
    */
   public FitnessFunctionGenerationRanker(FitnessFunction fitnessFunction) {
      this.fitnessFunction = fitnessFunction;
   }

   /**
    * Returns the sorted result of applying this object's {@code FitnessFunction} against each of the specified nodes.
    *
    * @param input
    *           the {@code Node} instances to apply this object's {@code FitnessFunction} against
    * @return a {@code List} of {@code RankedCandidate} - one for each {@code Node} specified in {@code input} - sorted by fitness
    */
   @Override
   public RankedCandidates rank(Collection<Node> input) {
      RankedCandidate[] output = new RankedCandidate[input.size()];
      int ctr = 0;
      for (Node n : input) {
         RankedCandidate rankedCandidate = rankCandidate(n);
         output[ctr++] = rankedCandidate;
      }
      return new RankedCandidates(output);
   }

   private RankedCandidate rankCandidate(Node n) {
      return new RankedCandidate(n, fitnessFunction.evaluate(n));
   }
}
