package org.oakgp.function.math;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

abstract class ArithmeticOperator implements Function {
   private final Signature signature;

   protected ArithmeticOperator(Type type) {
      signature = Signature.createSignature(type, type, type);
   }

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      return evaluate(arguments.firstArg(), arguments.secondArg(), assignments);
   }

   protected abstract Object evaluate(Node arg1, Node arg2, Assignments assignments);

   @Override
   public final Signature getSignature() {
      return signature;
   }
}
