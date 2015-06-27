package org.oakgp.examples.hanoi;

/** Represents the possible moves that can be used to attempt to solve the puzzle. */
enum Move {
   LEFT_MIDDLE(Pole.LEFT, Pole.MIDDLE),
   LEFT_RIGHT(Pole.LEFT, Pole.RIGHT),
   MIDDLE_LEFT(Pole.MIDDLE, Pole.LEFT),
   MIDDLE_RIGHT(Pole.MIDDLE, Pole.RIGHT),
   RIGHT_LEFT(Pole.RIGHT, Pole.LEFT),
   RIGHT_MIDDLE(Pole.RIGHT, Pole.MIDDLE);

   /** The pole to remove a disc from. */
   final Pole from;
   /** The pole to add a disc to. */
   final Pole to;

   private Move(Pole from, Pole to) {
      this.from = from;
      this.to = to;
   }
}
