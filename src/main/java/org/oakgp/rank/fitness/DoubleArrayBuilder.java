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

import java.util.ArrayList;
import java.util.List;

public final class DoubleArrayBuilder {
   private double start;
   private double end;
   private double step = 1; // TODO rename step to increment

   public static DoubleArrayBuilder doubles() {
      return new DoubleArrayBuilder();
   }

   public DoubleArrayBuilder from(double start) {
      this.start = start;
      return this;
   }

   public DoubleArrayBuilder to(double end) {
      this.end = end;
      return this;
   }

   public DoubleArrayBuilder increment(double step) {
      if (step <= 0) {
         throw new IllegalArgumentException(step + " <= 0");
      }

      this.step = step;
      return this;
   }

   // TODO use BigDecimal
   public List<Double> build() {
      if (start > end) {
         throw new IllegalArgumentException(start + " > " + end);
      }

      List<Double> result = new ArrayList<>();
      double v = start;
      while (v <= end) {
         result.add(v);
         v += step;
      }
      return result;
   }
}
