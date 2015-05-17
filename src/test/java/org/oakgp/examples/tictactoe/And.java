package org.oakgp.examples.tictactoe;

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.booleanType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

class And implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      return isTrue(arguments.firstArg(), assignments) && isTrue(arguments.secondArg(), assignments);
   }

   private boolean isTrue(Node node, Assignments assignments) {
      return TRUE.equals(node.evaluate(assignments));
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
