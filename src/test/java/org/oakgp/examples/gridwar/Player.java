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

import static org.oakgp.examples.gridwar.GridWar.GRID_WIDTH;

import org.oakgp.node.Node;

/** Represents the current state of a player in a GridWar game. */
class Player {
   private final Node logic;
   private int x;
   private int y;
   private int previousMove = -1;

   Player(int x, int y, Node logic) {
      this.x = x;
      this.y = y;
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
