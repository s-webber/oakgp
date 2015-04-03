package org.oakgp.function.coll;

import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;

public class Count implements Function {
   private final Signature signature;

   public Count(Type t) {
      signature = Signature.createSignature(integerType(), arrayType(t));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Arguments a = arguments.get(0).evaluate(assignments);
      return a.length();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
