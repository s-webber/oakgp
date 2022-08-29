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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.tournament.RoundRobinIterator.Pair;

/** Ranks and sorts the fitness of {@code Node} instances using a {@code TwoPlayerGame} in a round-robin tournament. */
public final class RoundRobinTournament implements GenerationRanker {
   private static final int SPLITERATOR_CHARACTERISTICS = Spliterator.SIZED + Spliterator.NONNULL + Spliterator.IMMUTABLE;

   private final TwoPlayerGame game;
   private final boolean parallel;

   /**
    * Creates a {@code RoundRobinTournament} for the given {@code TwoPlayerGame}.
    *
    * @param game logic for determining the winner of a two-player game
    * @param parallel {@code true} if a parallel stream should be used for ranking candidates
    */
   public RoundRobinTournament(TwoPlayerGame game, boolean parallel) {
      this.game = game;
      this.parallel = parallel;
   }

   @Override
   public RankedCandidates rank(Collection<Node> input) {
      MutableRankedCandidate[] candidates = toMutableRankedCandidate(input);
      stream(candidates).forEach(this::playGame);
      return toRankedCandidates(candidates);
   }

   private MutableRankedCandidate[] toMutableRankedCandidate(Collection<Node> input) {
      MutableRankedCandidate[] output = new MutableRankedCandidate[input.size()];
      int ctr = 0;
      for (Node node : input) {
         output[ctr++] = new MutableRankedCandidate(node);
      }
      return output;
   }

   private Stream<Pair<MutableRankedCandidate>> stream(MutableRankedCandidate[] candidates) {
      return StreamSupport.stream(Spliterators.spliterator(new RoundRobinIterator<>(candidates), candidates.length, SPLITERATOR_CHARACTERISTICS), parallel);
   }

   private void playGame(Pair<MutableRankedCandidate> pair) {
      MutableRankedCandidate player1 = pair.getLeft();
      MutableRankedCandidate player2 = pair.getRight();
      TwoPlayerGameResult result = game.evaluate(player1.getNode(), player2.getNode());
      player1.addFitness(result.getFitness1());
      player2.addFitness(result.getFitness2());
   }

   private RankedCandidates toRankedCandidates(MutableRankedCandidate[] input) {
      RankedCandidate[] output = new RankedCandidate[input.length];
      for (int i = 0; i < input.length; i++) {
         output[i] = new RankedCandidate(input[i].getNode(), input[i].getFitness());
      }
      return new RankedCandidates(output, Collections.reverseOrder());
   }
}
