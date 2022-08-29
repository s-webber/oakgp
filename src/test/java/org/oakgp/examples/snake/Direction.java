package org.oakgp.examples.snake;

enum Direction {
   NORTH,
   EAST,
   SOUTH,
   WEST;

   Direction opposite() {
      switch (this) {
         case NORTH:
            return SOUTH;
         case EAST:
            return WEST;
         case SOUTH:
            return NORTH;
         case WEST:
            return EAST;
         default:
            throw new IllegalStateException();
      }
   }
}
