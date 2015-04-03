package org.oakgp.function.hof;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Type.integerArrayType;
import static org.oakgp.Type.integerToBooleanFunctionType;
import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public class Reduce implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), integerToBooleanFunctionType(), integerType(), integerArrayType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.get(0).evaluate(assignments);
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
