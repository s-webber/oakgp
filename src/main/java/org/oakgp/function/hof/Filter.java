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

/**
 * Uses a function to filter the elements of a collection.
 * <p>
 * Returns a new collection that exists of all the elements in the collection (specified by the second argument) for which the function (specified by the first
 * argument) returns {@code true}.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Wikipedia</a>
 */
public final class Filter implements Function {
   private final Signature signature;

   public Filter(Type type) {
      signature = Signature.createSignature(arrayType(type), functionType(booleanType(), type), arrayType(type));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.firstArg().evaluate(assignments);
      Arguments candidates = arguments.secondArg().evaluate(assignments);
      List<Node> result = new ArrayList<>();
      for (int i = 0; i < candidates.getArgCount(); i++) {
         Node candidate = candidates.getArg(i);
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
