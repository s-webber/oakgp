/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function.compare;

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.comparableType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.nullableType;

import java.util.Collection;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.coll.Sort;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Compares the values of a given collection and returns the maximum. */
public final class MaxList implements Function {
   private final Signature signature;

   public MaxList() {
      Type type = Types.generic("ElementType", comparableType());
      this.signature = Signature.createSignature(nullableType(type), listType(type));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Comparable evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
      Collection<Comparable> input = arguments.first().evaluate(assignments, adfs);
      Comparable result = null;
      for (Comparable i : input) {
         if (result == null || i.compareTo(result) > 0) {
            result = i;
         }
      }
      return result;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node n = functionNode.getChildren().first();
      if (isFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         Function f = fn.getFunction();
         if (f == Sort.getSingleton()) {
            return new FunctionNode(functionNode, fn.getChildren());
         }
      }
      return null;
   }

   @Override
   public String getDisplayName() {
      return "max";
   }
}
