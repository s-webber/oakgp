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
package org.oakgp.rank.tournament;

public final class TwoPlayerGameResult {
   private final double fitness1;
   private final double fitness2;

   public TwoPlayerGameResult(double fitness1, double fitness2) {
      this.fitness1 = fitness1;
      this.fitness2 = fitness2;
   }

   public double getFitness1() {
      return fitness1;
   }

   public double getFitness2() {
      return fitness2;
   }

   public TwoPlayerGameResult flip() {
      return new TwoPlayerGameResult(fitness2, fitness1);
   }

   @Override
   public int hashCode() { // TODO
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(fitness1);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(fitness2);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) { // TODO
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      TwoPlayerGameResult other = (TwoPlayerGameResult) obj;
      if (Double.doubleToLongBits(fitness1) != Double.doubleToLongBits(other.fitness1)) {
         return false;
      }
      if (Double.doubleToLongBits(fitness2) != Double.doubleToLongBits(other.fitness2)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "TwoPlayerGameResult [fitness1=" + fitness1 + ", fitness2=" + fitness2 + "]";
   }
}
