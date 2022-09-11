package org.oakgp.examples.snake;

enum Direction {
   UP,
   RIGHT,
   DOWN,
   LEFT;

   Direction turnRight() {
      switch (this) {
         case UP:
            return RIGHT;
         case RIGHT:
            return DOWN;
         case DOWN:
            return LEFT;
         case LEFT:
            return UP;
         default:
            throw new IllegalStateException();
      }
   }

   Direction turnLeft() {
      switch (this) {
         case UP:
            return LEFT;
         case RIGHT:
            return UP;
         case DOWN:
            return RIGHT;
         case LEFT:
            return DOWN;
         default:
            throw new IllegalStateException();
      }
   }
}
