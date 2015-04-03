package org.oakgp.examples.gridwar;

import static org.oakgp.examples.gridwar.GridWar.GRID_WIDTH;

import org.oakgp.node.Node;

/** Represents the current state of a player in a GridWar game. */
class Player {
   private final Node logic;
   private int x;
   private int y;
   private int previousMove;

   Player(int x, int y, int previousMove, Node logic) {
      this.x = x;
      this.y = y;
      this.previousMove = previousMove;
      this.logic = logic;
   }

   void updateState(int nextMove) {
      previousMove = nextMove;
      switch (nextMove) {
      case 0:
         up();
         break;
      case 1:
         right();
         break;
      case 2:
         down();
         break;
      case 3:
         left();
         break;
      default:
         throw new IllegalArgumentException("Invalid move: " + nextMove);
      }
   }

   private void up() {
      if (y > 0) {
         y--;
      }
   }

   private void down() {
      if (y < GRID_WIDTH - 1) {
         y++;
      }
   }

   private void left() {
      if (x > 0) {
         x--;
      }
   }

   private void right() {
      if (x < GRID_WIDTH - 1) {
         x++;
      }
   }

   int getPreviousMove() {
      return previousMove;
   }

   void setPreviousMove(int previousMove) {
      this.previousMove = previousMove;
   }

   Node getLogic() {
      return logic;
   }

   int getX() {
      return x;
   }

   int getY() {
      return y;
   }
}