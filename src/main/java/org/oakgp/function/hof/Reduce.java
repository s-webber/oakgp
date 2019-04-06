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

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Type.listType;
import static org.oakgp.Type.functionType;

import java.util.Collection;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
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

   /**
    * Creates a higher order functions that recursively applies a function to the elements of a collection.
    *
    * @param type
    *           the type of the elements contained in the collection - this will also be the type associated with the value produced by evaluating this function
    */
   public Reduce(Type type) {
      signature = Signature.createSignature(type, functionType(type, type, type), type, listType(type));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.firstArg().evaluate(assignments);
      Node result = arguments.secondArg();
      Collection<Node> candidates = arguments.thirdArg().evaluate(assignments);
      for (Node n : candidates) {
         result = new ConstantNode(f.evaluate(createArguments(result, n), assignments), f.getSignature().getReturnType());
      }
      return result.evaluate(assignments);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
