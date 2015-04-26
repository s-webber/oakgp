package org.oakgp.function.math;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;

abstract class ArithmeticOperator<T extends Number> implements Function {
   private final Signature signature;

   protected ArithmeticOperator(Type type) {
      signature = Signature.createSignature(type, type, type);
   }

   @Override
   public final T evaluate(Arguments arguments, Assignments assignments) {
      T n1 = arguments.firstArg().evaluate(assignments);
      T n2 = arguments.secondArg().evaluate(assignments);
      return evaluate(n1, n2);
   }

   protected abstract T evaluate(T arg1, T arg2);

   @Override
   public final Signature getSignature() {
      return signature;
   }
}
