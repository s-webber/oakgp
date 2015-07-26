/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.function.hof;

import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.functionType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
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

   /**
    * Creates a higher order functions that applies a function to each element of a collection.
    *
    * @param from
    *           the type of the elements contained in the collection provided as an argument to the function
    * @param to
    *           the type of the elements contained in the collection returned by the function
    */
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
