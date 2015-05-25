package org.oakgp.mutate;

import java.util.function.Predicate;

import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Selects a subtree of the parent as a new offspring.
 * <p>
 * The resulting offspring will be smaller than the parent.
 */
public final class HoistMutation implements NodeEvolver {
   private final Random random;

   public HoistMutation(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      Predicate<Node> treeWalkerStrategy = n -> n.getType() == root.getType();
      int nodeCount = root.getNodeCount(treeWalkerStrategy);
      if (nodeCount == 1) {
         // if node count == 1 then that indicates that the only node with the same return type
         // as the root node is the root node itself
         return root;
      } else {
         int index = Utils.selectSubNodeIndex(random, nodeCount);
         return root.getAt(index, treeWalkerStrategy);
      }
   }
}
