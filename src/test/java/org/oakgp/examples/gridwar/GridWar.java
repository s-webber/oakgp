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
package org.oakgp.examples.gridwar;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.util.Random;

/** Game engine for Grid War. */
class GridWar implements TwoPlayerGame {
   static final int GRID_WIDTH = 4;
   /** Number of possible directions a player can move in - up, down, left or right. */
   private static final int NUMBER_OF_POSSIBLE_DIRECTIONS = 4;
   /** The maximum number of moves without a winner before the game is considered a draw. */
   private static final int MAX_MOVES = 24;
   /** The reward assigned to a winning player. */
   private static final int WIN = 1;
   /** The penalty assigned to a losing player. */
   private static final int LOSE = -WIN;
   /** The reward assigned to both players of a drawn game. */
   private static final int NO_WINNER = 0;
   private final Random random;

   GridWar(Random random) {
      this.random = random;
   }

   @Override
   public double evaluate(Node playerLogic1, Node playerLogic2) {
      Player[] players = createPlayers(playerLogic1, playerLogic2);
      int moveCtr = 0;
      int currentPlayerIdx = 0;
      while (moveCtr++ < MAX_MOVES) {
         Player player = players[currentPlayerIdx];
         Player opponent = players[1 - currentPlayerIdx];
         int moveOutcome = processNextMove(player, opponent);
         if (moveOutcome != NO_WINNER) {
            return currentPlayerIdx == 0 ? moveOutcome : -moveOutcome;
         }
         // each player takes it in turn to move
         currentPlayerIdx = 1 - currentPlayerIdx;
      }
      // no winner within maximum number of moves - draw
      return NO_WINNER;
   }

   private Player[] createPlayers(Node playerLogic1, Node playerLogic2) {
      // randomly position player 1
      int x = random.nextInt(GRID_WIDTH);
      int y = random.nextInt(GRID_WIDTH);
      Player player1 = new Player(x, y, -1, playerLogic1);

      // randomly position player 2, ensuring they do not occupy the same or an adjacent square to player 1
      Player player2 = new Player((x + 2) % GRID_WIDTH, (y + 2) % GRID_WIDTH, -1, playerLogic2);

      return new Player[] { player1, player2 };
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
      // normalise the result to ensure it is in the valid range of possible moves
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
