package org.oakgp.mutate;

import static org.oakgp.util.Utils.isFunction;

import org.oakgp.NodeEvolver;
import org.oakgp.PrimitiveSet;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/** Performs mutation (also known as node replacement mutation). */
public final class PointMutation implements NodeEvolver {
   private final Random random;
   private final PrimitiveSet primitiveSet;

   public PointMutation(Random random, PrimitiveSet primitiveSet) {
      this.random = random;
      this.primitiveSet = primitiveSet;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int mutationPoint = Utils.selectSubNodeIndex(root, random);
      return root.replaceAt(mutationPoint, node -> {
         if (isFunction(node)) {
            FunctionNode functionNode = (FunctionNode) node;
            Function function = primitiveSet.nextAlternativeFunction(functionNode.getFunction());
            return new FunctionNode(function, functionNode.getArguments());
         } else {
            return primitiveSet.nextAlternativeTerminal(node);
         }
      });
   }
}
