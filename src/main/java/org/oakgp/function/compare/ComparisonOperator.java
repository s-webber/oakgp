package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.function.Operator;

abstract class ComparisonOperator implements Operator {
   private static final ConstantNode TRUE_NODE = new ConstantNode(TRUE, BOOLEAN); // TODO share version used by NodeReader
   private static final ConstantNode FALSE_NODE = new ConstantNode(FALSE, BOOLEAN); // TODO share version used by NodeReader
   private static final Signature SIGNATURE = Signature.createSignature(BOOLEAN, INTEGER, INTEGER);

   private final boolean equalsIsTrue;

   protected ComparisonOperator(boolean equalsIsTrue) {
      this.equalsIsTrue = equalsIsTrue;
   }

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      int i1 = (int) arguments.get(0).evaluate(assignments);
      int i2 = (int) arguments.get(1).evaluate(assignments);
      if (evaluate(i1, i2)) {
         return TRUE;
      } else {
         return FALSE;
      }
   }

   protected abstract boolean evaluate(int arg1, int arg2);

   @Override
   public final Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node simplify(Arguments arguments) {
      if (arguments.get(0).equals(arguments.get(1))) {
         return equalsIsTrue ? TRUE_NODE : FALSE_NODE;
      } else {
         return null;
      }
   }
}
