package org.oakgp.function.compare;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Determines if the object represented by the first argument is greater than the object represented by the second. */
public final class GreaterThan extends ComparisonOperator {
   public GreaterThan(Type type) {
      super(type, false);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff > 0;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null) {
         // TODO don't create new LessThanOrEqual each time
         return new FunctionNode(new LessThan(getSignature().getReturnType()), arguments.secondArg(), arguments.firstArg());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public String getDisplayName() {
      return ">";
   }
}
