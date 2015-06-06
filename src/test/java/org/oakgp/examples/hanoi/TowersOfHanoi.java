package org.oakgp.examples.hanoi;

import java.util.Arrays;

/**
 * Towers of Hanoi
 * <p>
 * The Towers of Hanoi is a game played with three poles and a number of discs of different sizes which can slide onto any pole. The game starts with all the
 * discs stacked in ascending order of size on one pole, the smallest at the top. The aim of the game is to move the entire stack to another pole, obeying the
 * following rules:
 * <ul>
 * <li>Only one disc may be moved at a time.</li>
 * <li>Each move consists of taking the upper disc from one of the poles and sliding it onto another pole.</li>
 * <li>No disc may be placed on top of a smaller one.</li>
 * </ul>
 * </p>
 */
class TowersOfHanoi {
   private static final int NUM_POLES = Pole.values().length;

   private final int[] poles;

   public TowersOfHanoi(int numberOfDiscs) {
      poles = new int[NUM_POLES];
      poles[0] = calculateTotalBits(numberOfDiscs);
   }

   private static int calculateTotalBits(int numberOfDiscs) {
      int totalBits = 0;
      for (int i = 0, b = 1; i < numberOfDiscs; i++, b *= 2) {
         totalBits += b;
      }
      return totalBits;
   }

   public TowersOfHanoi(int[] poles) {
      this.poles = poles;
   }

   public int upperDisc(Pole pole) {
      return Integer.lowestOneBit(poles[pole.ordinal()]);
   }

   public int getFitness() {
      int pole = poles[Pole.MIDDLE.ordinal()];
      boolean isEmpty = pole == 0;
      if ((pole & 4) == 0) {
         return isEmpty ? 4 : 8;
      }
      if ((pole & 2) == 0) {
         return 2;
      }
      if ((pole & 1) == 0) {
         return 1;
      }
      return 0;
   }

   public TowersOfHanoi move(Move move) {
      return move(move.from, move.to);
   }

   private TowersOfHanoi move(Pole from, Pole to) {
      if (from == to) {
         // pointless move
         return null;
      }

      int fromUpperDisc = upperDisc(from);
      int toUpperDisc = upperDisc(to);

      if (fromUpperDisc == 0 || (toUpperDisc != 0 && fromUpperDisc > toUpperDisc)) {
         // invalid move
         return null;
      }

      // return updated copy
      int[] updatedPoles = Arrays.copyOf(poles, NUM_POLES);
      // remove disc from pole
      updatedPoles[from.ordinal()] -= fromUpperDisc;
      // add disc to pole
      updatedPoles[to.ordinal()] += fromUpperDisc;
      return new TowersOfHanoi(updatedPoles);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(poles);
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof TowersOfHanoi && Arrays.equals(poles, ((TowersOfHanoi) o).poles);
   }

   @Override
   public String toString() {
      return Arrays.toString(poles);
   }
}
