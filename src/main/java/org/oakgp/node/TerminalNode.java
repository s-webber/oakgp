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
package org.oakgp.node;

import java.util.function.Function;
import java.util.function.Predicate;

/** Represents a node that has no children. */
abstract class TerminalNode implements Node {
   @Override
   public final int getHeight() {
      return 1;
   }

   @Override
   public final int getNodeCount() {
      return 1;
   }

   @Override
   public final int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      return treeWalkerStrategy.test(this) ? 1 : 0;
   }

   @Override
   public final Node replaceAt(int index, Function<Node, Node> replacement) {
      return replacement.apply(this);
   }

   @Override
   public final Node replaceAt(int index, Function<Node, Node> replacement, Predicate<Node> treeWalkerStrategy) {
      // TODO review what correct behaviour should be
      return replaceAt(index, replacement);
   }

   @Override
   public Node replaceAll(Predicate<Node> criteria, Function<Node, Node> replacement) {
      if (criteria.test(this)) {
         return replacement.apply(this);
      } else {
         return this;
      }
   }

   @Override
   public final Node getAt(int index) {
      return this;
   }

   @Override
   public final Node getAt(int index, Predicate<Node> treeWalkerStrategy) {
      // TODO review what correct behaviour should be
      return getAt(index);
   }
}
