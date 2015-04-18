package org.oakgp.function.compare;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Determines if the number represented by the first argument is greater than the number represented by the second. */
public final class GreaterThan extends ComparisonOperator {
   public GreaterThan() {
      super(false);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 > arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null) {
         return new FunctionNode(new LessThan(), arguments.secondArg(), arguments.firstArg());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public String getDisplayName() {
      return ">";
   }
}
