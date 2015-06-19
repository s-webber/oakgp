package org.oakgp.examples.gridwar;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.util.Random;

/** Game engine for Grid War. */
class GridWar implements TwoPlayerGame {
   static final int GRID_WIDTH = 4;
   private static final int NUMBER_OF_POSSIBLE_DIRECTIONS = 4;
   private static final int MAX_MOVES = 24;
   private static final int WIN = 1;
   private static final int LOSE = -1;
   private static final int NO_WINNER = 0;
   private final Random random;

   GridWar(Random random) {
      this.random = random;
   }

   @Override
   public double evaluate(Node player1, Node player2) {
      int x = random.nextInt(GRID_WIDTH);
      int y = random.nextInt(GRID_WIDTH);
      Player[] players = { new Player(x, y, -1, player1), new Player((x + 2) % 4, (y + 2) % 4, -1, player2) };
      int moveCtr = 0;
      int currentPlayerIdx = 0;
      while (moveCtr++ < MAX_MOVES) {
         Player player = players[currentPlayerIdx];
         Player opponent = players[1 - currentPlayerIdx];
         int moveOutcome = processNextMove(player, opponent);
         if (moveOutcome != NO_WINNER) {
            return currentPlayerIdx == 0 ? moveOutcome : -moveOutcome;
         }
         currentPlayerIdx = 1 - currentPlayerIdx;
      }
      return NO_WINNER;
   }

   private static int processNextMove(Player player, Player opponent) {
      Assignments assignments = createAssignments(player, opponent);
      int nextMove = getNextMove(player, assignments);
      if (isRepeatedMove(player, nextMove)) {
         // duplicate move - lose
         return LOSE;
      }
      player.updateState(nextMove);
      if (isWon(player, opponent)) {
         // entered square already occupied by opponent - win
         return WIN;
      }
      return NO_WINNER;
   }

   private static int getNextMove(Player player, Assignments assignments) {
      int result = (int) player.getLogic().evaluate(assignments);
      // ensure result is in the range 0 to 3
      return Math.abs(result % NUMBER_OF_POSSIBLE_DIRECTIONS);
   }

   private static Assignments createAssignments(Player playerToMoveNext, Player opponent) {
      return Assignments.createAssignments(playerToMoveNext.getX(), playerToMoveNext.getY(), playerToMoveNext.getPreviousMove(), opponent.getX(),
            opponent.getY(), opponent.getPreviousMove());
   }

   private static boolean isRepeatedMove(Player currentPlayer, int nextMove) {
      return currentPlayer.getPreviousMove() == nextMove;
   }

   private static boolean isWon(Player player, Player opponent) {
      return player.getX() == opponent.getX() && player.getY() == opponent.getY();
   }
}
