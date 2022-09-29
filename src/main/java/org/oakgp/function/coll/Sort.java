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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.MapperFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/** Returns the elements in the given collection sorted into ascending order. */
public final class Sort implements MapperFunction {
   private static final Sort SINGLETON = new Sort();

   public static Sort getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private Sort() {
      Type type = Types.generic("ElementType", comparableType());
      signature = Signature.createSignature(listType(type), listType(type));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Collection<Comparable<?>> input = arguments.first().evaluate(assignments, adfs);
      List<Comparable<?>> output = new ArrayList<>(input);
      output.sort(null);
      return output;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) { // TODO copied from Set
      Node n = functionNode.getChildren().first();
      if (isFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         Function f = fn.getFunction();
         if (f == this || f == SortedSet.getSingleton()) {
            // (sort (sort v0)) -> (sort v0)
            // (sort (sorted-set v0)) -> (sorted-set v0)
            return n;
         } else if (f == Set.getSingleton()) {
            // (sort (set v0)) -> (sorted-set v0)
            return new FunctionNode(SortedSet.getSingleton(), functionNode.getType(), fn.getChildren());
         } else {
            Node x = Utils.recursivelyRemoveSort(n);
            if (x != n) {
               return new FunctionNode(functionNode, ChildNodes.createChildNodes(x));
            }
         }
      }
      return null;
   }
}
