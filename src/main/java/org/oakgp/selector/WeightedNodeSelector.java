package org.oakgp.selector;

import java.util.List;

import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;
import org.oakgp.util.Random;

/** Randomly selects nodes - with a bias to selecting nodes that have the best fitness. */
public final class WeightedNodeSelector implements NodeSelector {
   private final Random random;
   private final List<RankedCandidate> candidates;
   private final int size;

   public WeightedNodeSelector(Random random, List<RankedCandidate> candidates) {
      this.random = random;
      this.candidates = candidates;
      this.size = candidates.size();
   }

   @Override
   public Node next() {
      for (int i = 0; i < size - 1; i++) {
         if (random.nextBoolean()) {
            return candidates.get(i).getNode();
         }
      }
      return candidates.get(size - 1).getNode();
   }
}
