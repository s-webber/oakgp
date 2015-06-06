package org.oakgp.examples.hanoi;

enum Move {
   LEFT_MIDDLE(Pole.LEFT, Pole.MIDDLE), //
   LEFT_RIGHT(Pole.LEFT, Pole.RIGHT), //
   MIDDLE_LEFT(Pole.MIDDLE, Pole.LEFT), //
   MIDDLE_RIGHT(Pole.MIDDLE, Pole.RIGHT), //
   RIGHT_LEFT(Pole.RIGHT, Pole.LEFT), //
   RIGHT_MIDDLE(Pole.RIGHT, Pole.MIDDLE);

   final Pole from;
   final Pole to;

   private Move(Pole from, Pole to) {
      this.from = from;
      this.to = to;
   }
}
