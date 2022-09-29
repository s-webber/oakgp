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
import java.util.TreeSet;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/** Returns the distinct values contained in the given collection sorted into ascending order. */
public final class SortedSet implements Function {
   private static final SortedSet SINGLETON = new SortedSet();

   public static SortedSet getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private SortedSet() {
      Type type = Types.generic("ElementType", comparableType());
      signature = Signature.createSignature(listType(type), listType(type));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Collection<Integer> input = arguments.first().evaluate(assignments, adfs);
      return new TreeSet<>(input);
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
         if (f == this) {
            // (sorted-set (sorted-set v0)) -> (sorted-set v0)
            return n;
         } else if (f == Set.getSingleton() || f == Sort.getSingleton()) {
            // (sorted-set (set v0)) -> (sorted-set v0)
            // (sorted-set (sort v0)) -> (sorted-set v0)
            return new FunctionNode(functionNode, fn.getChildren());
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
