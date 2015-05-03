package org.oakgp.function.math;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.Node;

/** Performs addition. */
final class Divide extends ArithmeticOperator {
   private final NumberUtils numberUtils;

   Divide(NumberUtils numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
   }

   @Override
   protected Object evaluate(Node arg1, Node arg2, Assignments assignments) {
      if (numberUtils.isZero(arg2)) {
         return numberUtils.one().evaluate(null);
      } else {
         return numberUtils.divide(arg1, arg2, assignments).evaluate(null);
      }
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg2 = arguments.secondArg();
      if (numberUtils.isZero(arg2)) {
         return numberUtils.one();
      } else if (numberUtils.one().equals(arg2)) {
         return arguments.firstArg();
      } else {
         return null;
      }
      // TODO when second arg is -2 ?
   }

   @Override
   public String getDisplayName() {
      return "/";
   }
}
