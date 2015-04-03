package org.oakgp.util;

import java.util.HashSet;

import org.oakgp.NodeSimplifier;
import org.oakgp.node.Node;

/**
 * A {@code java.util.Set} of the simplified versions of {@code Node} instances.
 *
 * @see NodeSimplifier
 */
public final class NodeSet extends HashSet<Node> {
   private static final NodeSimplifier SIMPLIFIER = new NodeSimplifier();

   /**
    * Adds the simplified version of the specified {@code Node} to this set if it is not already present.
    *
    * @param n
    *           element that a simplified version of will be added to this set
    * @return {@code true} if this set did not already contain a simplified version of the specified {@code Node}
    * @see NodeSimplifier
    */
   @Override
   public boolean add(Node n) {
      return super.add(SIMPLIFIER.simplify(n));
   }
}
