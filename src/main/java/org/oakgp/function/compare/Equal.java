package org.oakgp.function.compare;

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Determines if two numbers are equal. */
public final class Equal extends ComparisonOperator {
   public Equal() {
      super(true);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 == arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null && NODE_COMPARATOR.compare(arguments.get(0), arguments.get(1)) > 0) {
         return new FunctionNode(this, arguments.get(1), arguments.get(0));
      } else {
         return simplifiedVersion;
      }
   }
}
