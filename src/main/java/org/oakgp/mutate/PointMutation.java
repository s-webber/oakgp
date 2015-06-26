package org.oakgp.mutate;

import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.GeneticOperator;
import org.oakgp.PrimitiveSet;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Randomly changes a point (node) in the parent.
 * <p>
 * A node in the parent is selected at random and replaced with another primitive of the same type and arity.
 * </p>
 * <p>
 * Also known as node replacement mutation.
 * </p>
 */
public final class PointMutation implements GeneticOperator {
   private final Random random;
   private final PrimitiveSet primitiveSet;

   public PointMutation(Random random, PrimitiveSet primitiveSet) {
      this.random = random;
      this.primitiveSet = primitiveSet;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int mutationPoint = Utils.selectSubNodeIndex(random, root);
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
