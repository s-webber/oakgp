package org.oakgp.function.hof;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

public class Filter implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(Type.ARRAY, Type.FUNCTION, Type.ARRAY);

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
      return SIGNATURE;
   }
}
