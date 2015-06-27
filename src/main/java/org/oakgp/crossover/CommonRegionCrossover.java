package org.oakgp.crossover;

import org.oakgp.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

public final class CommonRegionCrossover implements GeneticOperator {
   private final Random random;

   public CommonRegionCrossover(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node parent1 = selector.next();
      Node parent2 = selector.next();
      int commonRegionSize = CommonRegion.getNodeCount(parent1, parent2);
      int crossOverPoint = Utils.selectSubNodeIndex(random, commonRegionSize);
      return CommonRegion.crossoverAt(parent1, parent2, crossOverPoint);
   }
}
