package org.oakgp.function.hof;

import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.functionType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

public final class Filter implements Function {
   private final Signature signature;

   public Filter(Type type) {
      signature = Signature.createSignature(arrayType(type), functionType(booleanType(), type), arrayType(type));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.get(0).evaluate(assignments);
      Arguments candidates = arguments.get(1).evaluate(assignments);
      List<Node> result = new ArrayList<>();
      for (int i = 0; i < candidates.length(); i++) {
         Node candidate = candidates.get(i);
         if (((Boolean) f.evaluate(Arguments.createArguments(candidate), assignments)).booleanValue()) {
            result.add(candidate);
         }
      }
      return Arguments.createArguments(result.toArray(new Node[result.size()]));
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
