package org.oakgp.mutate;

import org.oakgp.GeneticOperator;
import org.oakgp.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class ConstantToFunctionMutation implements GeneticOperator {
   private final Random random;
   private final TreeGenerator treeGenerator;

   public ConstantToFunctionMutation(Random random, TreeGenerator treeGenerator) {
      this.random = random;
      this.treeGenerator = treeGenerator;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int nodeCount = root.getNodeCount(NodeType::isTerminal);
      int index = random.nextInt(nodeCount);
      return root.replaceAt(index, n -> treeGenerator.generate(n.getType(), 2), NodeType::isTerminal);
   }
}
