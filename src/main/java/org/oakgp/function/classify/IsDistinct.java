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
package org.oakgp.function.classify;

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.comparableType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;
import java.util.HashSet;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.coll.Set;
import org.oakgp.function.coll.SortedSet;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/** Determines if all the elements in a collection are distinct. */
public final class IsDistinct implements Function { // TODO what is the correct package for this?
   private final Signature signature;

   public IsDistinct() {
      Type type = Types.generic("ElementType", comparableType());
      signature = Signature.createSignature(booleanType(), listType(type));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Collection<Object> input = arguments.first();
      HashSet<Object> previous = new HashSet<>();
      for (Object e : input) {
         if (!previous.add(e)) {
            return false;
         }
      }
      return true;
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
         if (f == Set.getSingleton() || f == SortedSet.getSingleton()) {
            return Utils.TRUE_NODE;
         }
      }
      return null;
   }
}
