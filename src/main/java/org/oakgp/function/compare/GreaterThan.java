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
package org.oakgp.function.compare;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

/** Determines if the object represented by the first argument is greater than the object represented by the second. */
public final class GreaterThan extends ComparisonOperator {
   private static final GreaterThan SINGLETON = new GreaterThan();

   public static GreaterThan getSingleton() {
      return SINGLETON;
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private GreaterThan() {
      super(false);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff > 0;
   }

   @Override
   public String getDisplayName() {
      return ">";
   }

   @Override
   public Node getOpposite(FunctionNode fn) {
      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      return new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), swappedArgs);
   }

   @Override
   public Set<Node> getIncompatibles(FunctionNode fn) {
      Set<Node> result = new HashSet<>();

      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      result.add(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs));

      ChildNodes orderedArgs = NodeComparator.NODE_COMPARATOR.compare(fn.getChildren().first(), swappedArgs.first()) < 0 ? fn.getChildren() : swappedArgs;
      result.add(new FunctionNode(Equal.getSingleton(), fn.getType(), orderedArgs));

      return result;
   }

   @Override
   public Set<Node> getConsequences(FunctionNode fn) {
      Set<Node> result = new HashSet<>();

      result.add(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), fn.getChildren()));

      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      ChildNodes orderedArgs = NodeComparator.NODE_COMPARATOR.compare(fn.getChildren().first(), swappedArgs.first()) < 0 ? fn.getChildren() : swappedArgs;
      result.add(new FunctionNode(NotEqual.getSingleton(), fn.getType(), orderedArgs));

      return result;
   }

   @Override
   public Node getUnion(FunctionNode fn, Node n) {
      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      if (n.equals(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs))) {
         ChildNodes orderedArgs = NodeComparator.NODE_COMPARATOR.compare(fn.getChildren().first(), swappedArgs.first()) < 0 ? fn.getChildren() : swappedArgs;
         return new FunctionNode(NotEqual.getSingleton(), fn.getType(), orderedArgs);
      } else {
         return null;
      }
   }
}
