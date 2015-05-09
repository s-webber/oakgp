package org.oakgp;

import java.util.List;
import java.util.Map;

import org.oakgp.node.ConstantNode;
import org.oakgp.util.Utils;

/** Represents the range of possible constants to use during a genetic programming run. */
public final class ConstantSet {
   private final Map<Type, List<ConstantNode>> constantsByType;

   public ConstantSet(ConstantNode... constants) {
      constantsByType = Utils.groupByType(constants);
   }

   public List<ConstantNode> getByType(Type type) {
      return constantsByType.get(type);
   }
}
