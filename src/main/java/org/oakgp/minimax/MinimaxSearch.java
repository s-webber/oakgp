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

import org.oakgp.Assignments;
import org.oakgp.node.Node;

final class MinimaxSearch {
   private MinimaxSearch() {
   }

   /**
    * Returns the best possible next move from the specified game state.
    *
    * @param currentGameState
    *           the current state of the game
    * @param depth
    *           the maximum depth of the game tree to search. A negative value indicates that there should be no depth limit.
    * @return the best possible next move
    */
   public static MinimaxGameState minimax(final MinimaxGameState currentGameState, Node fitnessFunction, final int depth) {
      int bestOutcomeValue = Integer.MIN_VALUE;
      MinimaxGameState bestMove = null;

      for (final MinimaxGameState possibleMove : currentGameState.getChildren()) {
         final int moveOutcomeValue = alphabeta(possibleMove, fitnessFunction, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
         if (moveOutcomeValue >= bestOutcomeValue) {
            bestOutcomeValue = moveOutcomeValue;
            bestMove = possibleMove;
         }
      }

      return bestMove;
   }

   private static int alphabeta(MinimaxGameState state, Node fitnessFunction, int depth, int alpha, int beta, boolean maximizingPlayer) {
      if (depth == 0) {
         return getGoodness(state, fitnessFunction);
      }

      final MinimaxGameState[] children = state.getChildren();
      if (children.length == 0) {
         return getGoodness(state, fitnessFunction);
      }

      if (maximizingPlayer) {
         int value = Integer.MAX_VALUE;
         for (MinimaxGameState child : children) {
            value = alphabeta(child, fitnessFunction, depth - 1, alpha, beta, false);
            if (alpha < value) {
               alpha = value;
            }
            if (value >= beta) {
               return value;
            }
         }
         return value;
      } else {
         int value = Integer.MIN_VALUE;
         for (MinimaxGameState child : children) {
            value = alphabeta(child, fitnessFunction, depth - 1, alpha, beta, true);
            if (beta > value) {
               beta = value;
            }
            if (value <= alpha) {
               return value;
            }
         }
         return value;
      }
   }

   // TODO use comparable rather than int
   private static final int getGoodness(MinimaxGameState state, Node fitnessFunction) {
      return fitnessFunction.evaluate(Assignments.createAssignments(state), null);
   }
}
