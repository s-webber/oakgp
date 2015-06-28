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

import java.util.Scanner;

import org.oakgp.Assignments;
import org.oakgp.util.DummyNode;

/** Represents a human player in a GridWar game. */
class Human extends DummyNode {
   @Override
   public Integer evaluate(Assignments assignments) {
      int playerX = (int) assignments.get(0);
      int playerY = (int) assignments.get(1);
      int opponentX = (int) assignments.get(3);
      int opponentY = (int) assignments.get(4);
      for (int y = 0; y < GRID_WIDTH; y++) {
         for (int x = 0; x < GRID_WIDTH; x++) {
            char c;
            if (x == playerX && y == playerY) {
               c = 'X';
            } else if (x == opponentX && y == opponentY) {
               c = 'O';
            } else {
               c = '-';
            }
            System.out.print(c);
         }
         System.out.println();
      }
      System.out.println(" 0");
      System.out.println("3 1");
      System.out.println(" 2");
      System.out.println("your move");
      return readInt();
   }

   @SuppressWarnings("resource")
   private int readInt() {
      return new Scanner(System.in).nextInt();
   }
}
