package org.oakgp;

import org.oakgp.node.ConstantNode;

/** Represents the range of possible constants to use during a genetic programming run. */
public final class ConstantSet extends AbstractTerminalSet<ConstantNode> {
   public ConstantSet(ConstantNode... constants) {
      super(constants);
   }
}
