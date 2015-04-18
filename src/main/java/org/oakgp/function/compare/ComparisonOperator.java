package org.oakgp.function.compare;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

abstract class ComparisonOperator implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), integerType(), integerType());

   private final boolean equalsIsTrue;

   protected ComparisonOperator(boolean equalsIsTrue) {
      this.equalsIsTrue = equalsIsTrue;
   }

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      int i1 = (int) arguments.firstArg().evaluate(assignments);
      int i2 = (int) arguments.secondArg().evaluate(assignments);
      if (evaluate(i1, i2)) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }

   protected abstract boolean evaluate(int arg1, int arg2);

   @Override
   public final Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node simplify(Arguments arguments) {
      if (arguments.firstArg().equals(arguments.secondArg())) {
         return equalsIsTrue ? Utils.TRUE_NODE : Utils.FALSE_NODE;
      } else {
         return null;
      }
   }
}
