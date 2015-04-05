package org.oakgp;

import org.oakgp.node.ConstantNode;

public final class ConstantSet extends AbstractTerminalSet<ConstantNode> {
   public ConstantSet(ConstantNode... constants) {
      super(constants);
   }
}
