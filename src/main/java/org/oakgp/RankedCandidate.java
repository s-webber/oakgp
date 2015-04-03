package org.oakgp;

import org.oakgp.node.Node;

/** Associates a {@code Node} with its fitness value. */
public final class RankedCandidate implements Comparable<RankedCandidate> { // TODO rename?
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
