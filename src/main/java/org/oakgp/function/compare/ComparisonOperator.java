package org.oakgp.function.compare;

import static org.oakgp.Type.booleanType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

abstract class ComparisonOperator implements Function {
   private final Signature signature;
   private final boolean equalsIsTrue;

   protected ComparisonOperator(Type type, boolean equalsIsTrue) {
      this.signature = Signature.createSignature(booleanType(), type, type);
      this.equalsIsTrue = equalsIsTrue;
   }

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      Comparable o1 = arguments.firstArg().evaluate(assignments);
      Comparable o2 = arguments.secondArg().evaluate(assignments);
      int diff = o1.compareTo(o2);
      if (evaluate(diff)) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }

   protected abstract boolean evaluate(int diff);

   @Override
   public final Signature getSignature() {
      return signature;
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
