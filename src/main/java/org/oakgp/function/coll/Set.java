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
package org.oakgp.function.coll;

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.comparableType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Returns the distinct values contained in the given collection. */
public final class Set implements Function {
   private static final Set SINGLETON = new Set();

   public static Set getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private Set() {
      Type type = Types.generic("ElementType", comparableType());
      signature = Signature.createSignature(listType(type), listType(type));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Collection<Integer> input = arguments.first();
      return new LinkedHashSet<>(input);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) { // TODO copied from Sort
      Node n = functionNode.getChildren().first();
      if (isFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         Function f = fn.getFunction();
         if (f == this || f == SortedSet.getSingleton()) {
            // (set (set v0)) -> (set v0)
            // (set (sorted-set v0)) -> (sorted-set v0)
            return n;
         } else if (f == Sort.getSingleton()) {
            // (set (sort v0)) -> (sorted-set v0)
            return new FunctionNode(SortedSet.getSingleton(), functionNode.getType(), fn.getChildren());
         }
      }
      return null;
   }
}
