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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/** Contains details of a two-dimensional grid where some cells contain food and other cells are empty. */
class GridReader {
   private static final File SOURCE_FILE = new File("src/test/java/org/oakgp/examples/ant/grid.txt");
   private static final int GRID_LENGTH = 32;
   private static final boolean[][] GRID;
   private static final int NUM_FOOD_CELLS;

   static {
      try {
         GRID = read();
         NUM_FOOD_CELLS = countFoodCells(GRID);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   static int getGridLength() {
      return GRID_LENGTH;
   }

   static int getNumberOfFoodCells() {
      return NUM_FOOD_CELLS;
   }

   static boolean[][] copyGrid() {
      boolean[][] copy = new boolean[GRID_LENGTH][GRID_LENGTH];
      for (int i = 0; i < GRID_LENGTH; i++) {
         copy[i] = Arrays.copyOf(GRID[i], GRID_LENGTH);
      }
      return copy;
   }

   private static boolean[][] read() throws IOException {
      List<String> lines = Files.readAllLines(SOURCE_FILE.toPath());
      boolean[][] grid = new boolean[GRID_LENGTH][GRID_LENGTH];
      for (int i = 0; i < GRID_LENGTH; i++) {
         grid[i] = toArray(lines.get(i));
      }
      return grid;
   }

   private static boolean[] toArray(String line) {
      char[] chars = line.toCharArray();
      boolean[] result = new boolean[GRID_LENGTH];
      for (int i = 0; i < chars.length; i++) {
         result[i] = chars[i] == 'X';
      }
      return result;
   }

   private static int countFoodCells(boolean[][] grid) {
      int ctr = 0;
      for (int x = 0; x < GRID_LENGTH; x++) {
         for (int y = 0; y < GRID_LENGTH; y++) {
            if (grid[x][y]) {
               ctr++;
            }
         }
      }
      return ctr;
   }
}
