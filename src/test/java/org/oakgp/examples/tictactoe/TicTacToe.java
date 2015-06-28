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
package org.oakgp.examples.tictactoe;

import static org.oakgp.examples.tictactoe.Symbol.X;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.tournament.TwoPlayerGame;

class TicTacToe implements TwoPlayerGame {
   @Override
   public double evaluate(Node player1, Node player2) {
      Board board = new Board();
      Symbol next = X;
      while (true) {
         Assignments assignments = Assignments.createAssignments(board, next, next.getOpponent());
         Move move = (next == X ? player1 : player2).evaluate(assignments);
         board = board.update(next, move);

         if (board.isDraw()) {
            return 0;
         } else if (board.isWinner(next)) {
            return next == X ? 1 : -1;
         }

         next = next.getOpponent();
      }
   }
}
