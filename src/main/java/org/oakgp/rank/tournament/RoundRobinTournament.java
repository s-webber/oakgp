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
package org.oakgp.rank.tournament;

import java.util.Collection;
import java.util.Collections;

import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;

/** Ranks and sorts the fitness of {@code Node} instances using a {@code TwoPlayerGame} in a round-robin tournament. */
public final class RoundRobinTournament implements GenerationRanker {
   private final TwoPlayerGame game;

   /** Creates a {@code RoundRobinTournament} for the given {@code TwoPlayerGame}. */
   public RoundRobinTournament(TwoPlayerGame game) {
      this.game = game;
   }

   @Override
   public RankedCandidates rank(Collection<Node> input) {
      Node[] inputAsArray = input.toArray(new Node[input.size()]);
      double[] fitness = evaluateFitness(inputAsArray);
      return toRankedCandidates(inputAsArray, fitness);
   }

   private double[] evaluateFitness(Node[] input) {
      int size = input.length;
      double[] fitness = new double[size];
      for (int i1 = 0; i1 < size - 1; i1++) {
         Node player1 = input[i1];
         for (int i2 = i1 + 1; i2 < size; i2++) {
            Node player2 = input[i2];
            double result = game.evaluate(player1, player2);
            fitness[i1] += result;
            fitness[i2] += -result;
         }
      }
      return fitness;
   }

   private RankedCandidates toRankedCandidates(Node[] input, double[] fitness) {
      int size = fitness.length;
      RankedCandidate[] output = new RankedCandidate[size];
      for (int i = 0; i < size; i++) {
         output[i] = new RankedCandidate(input[i], fitness[i]);
      }
      return new RankedCandidates(output, Collections.reverseOrder());
   }
}
