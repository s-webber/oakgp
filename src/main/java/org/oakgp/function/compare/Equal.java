package org.oakgp.function.compare;

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * Determines if two object are equal.
 * <p>
 * <b>Note:</b> Equality is checked using {@code Comparable#compareTo(Object)} rather than {@code Object#equals(Object)}.
 */
public final class Equal extends ComparisonOperator {
   public Equal(Type type) {
      super(type, true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff == 0;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null && NODE_COMPARATOR.compare(arguments.firstArg(), arguments.secondArg()) > 0) {
         return new FunctionNode(this, arguments.secondArg(), arguments.firstArg());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public String getDisplayName() {
      return "=";
   }
}
