/*
 * Copyright 2022 S. Webber
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
package org.oakgp.minimax;

import java.util.function.Supplier;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.rank.tournament.TwoPlayerGame;
import org.oakgp.rank.tournament.TwoPlayerGameResult;

public final class MinimaxGame implements TwoPlayerGame {
   private final Supplier<MinimaxGameState> supplier;

   public MinimaxGame(Supplier<MinimaxGameState> supplier) {
      this.supplier = supplier;
   }

   @Override
   public TwoPlayerGameResult evaluate(Node player1, Node player2) {
      MinimaxGameState state = supplier.get();
      Node nextPlayer = player1;
      MinimaxGameState nextState;
      while ((nextState = nextPlayer.evaluate(Assignments.createAssignments(state))) != null) {
         state = nextState;
         nextPlayer = nextPlayer == player1 ? player2 : player1;
      }
      return state.getResult();
   }
}
