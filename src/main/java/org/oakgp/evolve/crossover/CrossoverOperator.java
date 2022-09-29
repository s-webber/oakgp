package org.oakgp.evolve.crossover;

import org.oakgp.node.Node;
import org.oakgp.util.Random;

public interface CrossoverOperator {
   Node crossover(Node parent1, Node parent2, Random random);
}
