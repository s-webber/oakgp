/*
 * Copyright 2022 S. Webber
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
package org.oakgp.rank.fitness;

public final class IntegerArrayBuilder {
   private int start;
   private int end;
   private int step = 1;

   public static IntegerArrayBuilder integers() {
      return new IntegerArrayBuilder();
   }

   public IntegerArrayBuilder from(int start) {
      this.start = start;
      return this;
   }

   public IntegerArrayBuilder to(int end) {
      this.end = end;
      return this;
   }

   public IntegerArrayBuilder increment(int step) {
      if (step < 1) { // TODO add requireTrue static method
         throw new IllegalArgumentException(step + " < 1");
      }

      this.step = step;
      return this;
   }

   public Integer[] build() {
      if (start > end) { // TODO add requireTrue static method
         throw new IllegalArgumentException(start + " > " + end);
      }

      Integer[] result = new Integer[end - start + 1];
      for (int i = 0, v = start; v <= end; i++, v += step) {
         result[i] = v;
      }
      return result;
   }
}
