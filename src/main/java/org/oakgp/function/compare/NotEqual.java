package org.oakgp.function.compare;

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Determines if two numbers are not equal. */
public final class NotEqual extends ComparisonOperator {
   public NotEqual(Type type) {
      super(type, false);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff != 0;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null && NODE_COMPARATOR.compare(arguments.firstArg(), arguments.secondArg()) > 0) {
         // TODO instead of this have argument orderer method used here, Equal and arithmetic operators
         return new FunctionNode(this, arguments.secondArg(), arguments.firstArg());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public String getDisplayName() {
      return "!=";
   }
}
