package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

abstract class ArithmeticOperator implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), integerType(), integerType());

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      int i1 = (int) arguments.get(0).evaluate(assignments);
      int i2 = (int) arguments.get(1).evaluate(assignments);
      return evaluate(i1, i2);
   }

   protected abstract int evaluate(int arg1, int arg2);

   @Override
   public final Signature getSignature() {
      return SIGNATURE;
   }
}
