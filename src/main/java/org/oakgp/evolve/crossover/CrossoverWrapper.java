package org.oakgp.evolve.crossover;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class CrossoverWrapper implements GeneticOperator {
   private final CrossoverOperator crossoverOperator;
   private final Random random;

   public CrossoverWrapper(CrossoverOperator crossoverOperator, Random random) {
      this.crossoverOperator = crossoverOperator;
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      return crossoverOperator.crossover(selector.next(), selector.next(), random);
   }
}
