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

/** Represents the possible moves that can be used to attempt to solve the puzzle. */
public enum Move {
   LEFT_MIDDLE(Pole.LEFT, Pole.MIDDLE), LEFT_RIGHT(Pole.LEFT, Pole.RIGHT), MIDDLE_LEFT(Pole.MIDDLE, Pole.LEFT), MIDDLE_RIGHT(Pole.MIDDLE,
         Pole.RIGHT), RIGHT_LEFT(Pole.RIGHT, Pole.LEFT), RIGHT_MIDDLE(Pole.RIGHT, Pole.MIDDLE);

   /** The pole to remove a disc from. */
   final Pole from;
   /** The pole to add a disc to. */
   final Pole to;

   private Move(Pole from, Pole to) {
      this.from = from;
      this.to = to;
   }
}
