package org.oakgp.examples.snake;

final class Cell {
   final int x;
   final int y;
   Cell north;
   Cell east;
   Cell south;
   Cell west;

   Cell(int x, int y) {
      this.x = x;
      this.y = y;
   }

   Cell move(Direction d) {
      switch (d) {
         case NORTH:
            return north;
         case EAST:
            return east;
         case SOUTH:
            return south;
         case WEST:
            return west;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public String toString() {
      return "[" + x + "," + y + "]";
   }
}
