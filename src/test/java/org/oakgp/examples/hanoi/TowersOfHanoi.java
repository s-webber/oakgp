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
package org.oakgp.examples.hanoi;

import java.util.Arrays;

/** Represents an individual state of a Towers of Hanoi puzzle. */
class TowersOfHanoi {
   private static final int NUM_POLES = Pole.values().length;
   /**
    * IDs of the discs, suitable for bitwise operations.
    * <p>
    * 1st element represents the smallest disc, 2nd element represents the medium size disc and the 3rd element represents the large size disc.
    */
   private static final int[] DISC_IDS = { 1, 2, 4 };
   private static final int SUM_DISC_IDS = Arrays.stream(DISC_IDS).sum();

   private final int[] poles;

   TowersOfHanoi() {
      poles = new int[NUM_POLES];
      poles[0] = SUM_DISC_IDS;
   }

   private TowersOfHanoi(int[] poles) {
      this.poles = poles;
   }

   /** @return the ID of the upper (i.e. top) disc of the specified pole, or {code 0} if there are no discs on the pole */
   int upperDisc(Pole pole) {
      return Integer.lowestOneBit(poles[pole.ordinal()]);
   }

   /**
    * Returns fitness value for the state represented by this object.
    * <p>
    * The more discs that are in their required position (i.e. correctly ordered on the middle pole) the lower the fitness value - so the lower the fitness
    * value the better. A fitness value of {@code 0} means all discs are on the middle pole (i.e. the puzzle is complete).
    *
    * @return {@code 0} if the middle pole contains all the discs, or a positive number if the puzzle is not yet complete
    */
   int getFitness() {
      int poleContents = poles[Pole.MIDDLE.ordinal()];
      for (int i = DISC_IDS.length - 1; i > -1; i--) {
         if ((poleContents & DISC_IDS[i]) == 0) {
            return DISC_IDS[i];
         }
      }
      return 0;
   }

   /** @return the result of applying {@code move} to the current state, or {code null} if the move is not valid */
   TowersOfHanoi move(Move move) {
      return move(move.from, move.to);
   }

   private TowersOfHanoi move(Pole from, Pole to) {
      if (from == to) {
         // pointless move
         return null;
      }

      int fromUpperDisc = upperDisc(from);
      int toUpperDisc = upperDisc(to);

      if (fromUpperDisc == 0) {
         // invalid move - cannot move a disc from an empty pole
         return null;
      }
      if (toUpperDisc != 0 && fromUpperDisc > toUpperDisc) {
         // invalid move - no disc may be placed on top of a smaller one
         return null;
      }

      // copy current state
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
