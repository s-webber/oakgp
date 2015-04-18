package org.oakgp.crossover;

import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/** Performs subtree crossover. */
public final class SubtreeCrossover implements NodeEvolver {
   private final Random random;

   public SubtreeCrossover(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node parent1 = selector.next();
      Node parent2 = selector.next();
      int to = Utils.selectSubNodeIndex(parent1, random);
      int from = random.nextInt(parent2.getNodeCount());
      Node replacementSubTree = parent2.getAt(from);
      return parent1.replaceAt(to, t -> {
         if (t.getType() == replacementSubTree.getType()) {
            return replacementSubTree;
         } else {
            return t;
         }
      });
   }
}
