package org.oakgp.mutate;

import org.oakgp.GeneticOperator;
import org.oakgp.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/** Randomly replaces a subtree of the parent with a newly generated subtree. */
public final class SubTreeMutation implements GeneticOperator {
   private final Random random;
   private final TreeGenerator treeGenerator;

   public SubTreeMutation(Random random, TreeGenerator treeGenerator) {
      this.random = random;
      this.treeGenerator = treeGenerator;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int mutationPoint = Utils.selectSubNodeIndex(random, root);
      return root.replaceAt(mutationPoint, node -> {
         return treeGenerator.generate(node.getType(), node.getHeight());
      });
   }
}
