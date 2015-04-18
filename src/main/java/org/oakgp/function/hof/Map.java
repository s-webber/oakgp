package org.oakgp.function.hof;

import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.functionType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/**
 * Returns the result of applying a function to each element of a collection.
 * <p>
 * Returns a new collection that exists of the result of applying the function (specified by the first argument) to each element of the collection (specified by
 * the second argument).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Wikipedia</a>
 */
public final class Map implements Function {
   private final Signature signature;

   public Map(Type from, Type to) {
      signature = Signature.createSignature(arrayType(to), functionType(to, from), arrayType(from));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.firstArg().evaluate(assignments);
      Type returnType = f.getSignature().getReturnType();
      Arguments candidates = arguments.secondArg().evaluate(assignments);
      List<Node> result = new ArrayList<>();
      for (int i = 0; i < candidates.getArgCount(); i++) {
         Node inputNode = candidates.getArg(i);
         Object evaluateResult = f.evaluate(Arguments.createArguments(inputNode), assignments);
         ConstantNode outputNode = new ConstantNode(evaluateResult, returnType);
         result.add(outputNode);
      }
      return Arguments.createArguments(result.toArray(new Node[result.size()]));
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
