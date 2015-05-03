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
