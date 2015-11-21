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
package org.oakgp.examples.ant;

import org.oakgp.Type;

/** Represents the grid and the ant's position within it. */
class MutableState {
   static final Type STATE_TYPE = Type.type("state");

   private boolean[][] grid;
   private int incX = 0;
   private int incY = 1;
   private int[] currentPosition = new int[2];
   private int foodEaten;
   private int movesTaken;

   MutableState(boolean[][] grid) {
      this.grid = grid;
   }

   /** Returns the number of cells visited that have contained food. */
   int getFoodEaten() {
      return foodEaten;
   }

   /** Returns the total number of moves (left, right and forward) that have already been applied to this object. */
   int getMovesTaken() {
      return movesTaken;
   }

   /** Returns {@code true} if the cell directly ahead of the ant contains food. */
   boolean isFoodAhead() {
      return isFood(getCoordsAhead());
   }

   /** Turns the ant to the left. */
   void left() {
      movesTaken++;

      int oldIncX = incX;
      incX = incY;
      incY = 0 - oldIncX;
   }

   /** Turns the ant to the right. */
   void right() {
      movesTaken++;

      int oldIncX = incX;
      incX = 0 - incY;
      incY = oldIncX;
   }

   /** Moves the ant forward one cell. */
   void forward() {
      movesTaken++;

      currentPosition = getCoordsAhead();
      if (isFood(currentPosition)) {
         eatFood(currentPosition);
      }
   }

   private int[] getCoordsAhead() {
      int[] newPosition = new int[2];
      newPosition[0] = update(currentPosition[0], incX);
      newPosition[1] = update(currentPosition[1], incY);
      return newPosition;
   }

   private int update(int current, int increment) {
      int newValue = current + increment;
      if (newValue == -1) {
         return GridReader.getGridLength() - 1;
      } else if (newValue == GridReader.getGridLength()) {
         return 0;
      } else {
         return newValue;
      }
   }

   private boolean isFood(int[] coords) {
      return grid[coords[0]][coords[1]];
   }

   private void eatFood(int[] coords) {
      grid[coords[0]][coords[1]] = false;
      foodEaten++;
   }
}
