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

import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

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
      // TODO second argument of signature should be for a function that accepts two args, not one
      signature = Signature.createSignature(type, functionType(type, type), type, listType(type));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Node first = arguments.first();
      Node second = arguments.second();
      Type resultType = second.getType();
      Node third = arguments.third();
      Type elementType = third.getType().getParameter(0);
      Function f = first.evaluate(assignments, adfs);
      Object result = second.evaluate(assignments, adfs);
      Collection<Object> elements = third.evaluate(assignments, adfs);
      for (Object element : elements) {
         ChildNodes childNodes = ChildNodes.createChildNodes(new ConstantNode(result, resultType), new ConstantNode(element, elementType));
         result = f.evaluate(childNodes, assignments, adfs);
      }
      return result;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
