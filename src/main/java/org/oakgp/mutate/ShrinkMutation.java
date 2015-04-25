package org.oakgp.mutate;

import org.oakgp.NodeEvolver;
import org.oakgp.PrimitiveSet;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

public class ShrinkMutation implements NodeEvolver {
   private final Random random;
   private final PrimitiveSet primitiveSet;

   public ShrinkMutation(Random random, PrimitiveSet primitiveSet) {
      this.random = random;
      this.primitiveSet = primitiveSet;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int nodeCount = root.getNodeCount(Utils::isFunction);
      if (nodeCount == 0) {
         // if nodeCount == 0 then that indicates that 'root' is a terminal node
         // (so can't be shrunk any further)
         return root;
      } else if (nodeCount == 1) {
         // if node count == 1 then that indicates that 'root' is a function node
         // that only has terminal nodes as arguments
         return primitiveSet.nextAlternativeTerminal(root);
      } else {
         // Note: -1 to avoid selecting root node TODO move selectMutationPoint method
         int index = random.nextInt(nodeCount - 1);
         return root.replaceAt(index, (n) -> primitiveSet.nextAlternativeTerminal(n), Utils::isFunction);
      }
   }
}
