package org.oakgp.function.hof;

import static org.oakgp.Arguments.createArguments;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.function.Operator;

public class Reduce implements Operator {
   private static final Signature SIGNATURE = Signature.createSignature(Type.INTEGER, Type.OPERATOR, Type.INTEGER, Type.ARRAY);

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Operator f = arguments.get(0).evaluate(assignments);
      Node result = arguments.get(1);
      Arguments candidates = arguments.get(2).evaluate(assignments);
      for (int i = 0; i < candidates.length(); i++) {
         result = new ConstantNode(f.evaluate(createArguments(result, candidates.get(i)), assignments), f.getSignature().getReturnType());
      }
      return result.evaluate(assignments);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
