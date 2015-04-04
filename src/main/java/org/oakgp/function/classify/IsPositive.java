package org.oakgp.function.classify;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public final class IsPositive implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), integerType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      int i = arguments.get(0).evaluate(assignments);
      return i > 0;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
