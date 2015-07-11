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
package org.oakgp.rank;

import org.oakgp.node.Node;

/** Associates a {@code Node} with its fitness value. */
public final class RankedCandidate implements Comparable<RankedCandidate> {
   private final double fitness;
   private final Node node;

   public RankedCandidate(Node node, double fitness) {
      this.node = node;
      this.fitness = fitness;
   }

   public double getFitness() {
      return fitness;
   }

   public Node getNode() {
      return node;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof RankedCandidate) {
         RankedCandidate r = (RankedCandidate) o;
         return this.node.equals(r.node) && this.fitness == r.fitness;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return node.hashCode();
   }

   @Override
   public int compareTo(RankedCandidate o) {
      return Double.compare(fitness, o.fitness);
   }

   @Override
   public String toString() {
      return "[" + node + " " + fitness + "]";
   }
}
