package org.oakgp.function.coll;

import static org.oakgp.Type.booleanArrayType;
import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public class Count implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), booleanArrayType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Arguments a = arguments.get(0).evaluate(assignments);
      return a.length();
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
