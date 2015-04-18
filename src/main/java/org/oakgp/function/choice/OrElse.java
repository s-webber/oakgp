package org.oakgp.function.choice;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;

public class OrElse implements Function {
   private final Signature signature;

   public OrElse(Type type) {
      signature = Signature.createSignature(type, Type.optionalType(type), type);
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Object result = arguments.firstArg().evaluate(assignments);
      if (result == null) {
         return arguments.secondArg().evaluate(assignments);
      } else {
         return result;
      }
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
