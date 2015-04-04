package org.oakgp.function.hof;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.functionType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/**
 * Combines the elements of a collection by recursively applying a function.
 * <p>
 * Expects three arguments:
 * <ol>
 * <li>A function.</li>
 * <li>An initial value.</li>
 * <li>A collection.</li>
 * </ol>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)">Wikipedia</a>
 */
public final class Reduce implements Function {
   private final Signature signature;

   public Reduce(Type type) {
      signature = Signature.createSignature(type, functionType(type, type, type), type, arrayType(type));
   }

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
      return signature;
   }
}
