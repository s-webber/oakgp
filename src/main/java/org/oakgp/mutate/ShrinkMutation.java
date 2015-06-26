package org.oakgp.mutate;

import org.oakgp.GeneticOperator;
import org.oakgp.PrimitiveSet;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Replaces a randomly selected function node of the parent with a terminal node.
 * <p>
 * The resulting offspring will be smaller than the parent.
 */
public final class ShrinkMutation implements GeneticOperator {
   private final Random random;
   private final PrimitiveSet primitiveSet;

   public ShrinkMutation(Random random, PrimitiveSet primitiveSet) {
      this.random = random;
      this.primitiveSet = primitiveSet;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int nodeCount = root.getNodeCount(NodeType::isFunction);
      if (nodeCount == 0) {
         // if nodeCount == 0 then that indicates that 'root' is a terminal node
         // (so can't be shrunk any further)
         return root;
      } else if (nodeCount == 1) {
         // if node count == 1 then that indicates that 'root' is a function node
         // that only has terminal nodes as arguments
         return primitiveSet.nextAlternativeTerminal(root);
      } else {
         int index = Utils.selectSubNodeIndex(random, nodeCount);
         return root.replaceAt(index, (n) -> primitiveSet.nextAlternativeTerminal(n), NodeType::isFunction);
      }
   }
}
