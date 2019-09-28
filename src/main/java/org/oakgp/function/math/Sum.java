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
package org.oakgp.function.math;

import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Returns the sum of the numbers contained in the given collection. */
final class Sum<T extends Comparable<T>> implements Function {
   private final Signature signature;
   private final NumberUtils<T> numberUtils;

   /** @see NumberUtils#getSum() */
   Sum(NumberUtils<T> numberUtils) {
      this.signature = Signature.createSignature(numberUtils.getType(), listType(numberUtils.getType()));
      this.numberUtils = numberUtils;
   }

   @Override
   public T evaluate(Arguments arguments) {
      Collection<T> input = arguments.first();
      T result = numberUtils.rawZero();
      for (T i : input) {
         result = numberUtils.add(result, i);
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
      Node x = Utils.recursivelyRemoveSort(n);
      if (x != n) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(x));
      }

      return null;
   }
}
