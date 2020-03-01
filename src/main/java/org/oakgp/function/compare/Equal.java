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

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/**
 * Determines if two objects are equal.
 * <p>
 * <b>Note:</b> Equality is checked using {@code Comparable#compareTo(Object)} rather than {@code Object#equals(Object)}.
 */
public final class Equal extends ComparisonOperator {
   private static final Equal SINGLETON = new Equal();

   public static Equal getSingleton() {
      return SINGLETON;
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private Equal() {
      super(true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff == 0;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      Node simplifiedVersion = simplifyToTrue(children);
      if (simplifiedVersion != null) {
         return simplifiedVersion;
      }

      Node first = children.first();
      Node second = children.second();

      if (NODE_COMPARATOR.compare(first, second) > 0) {
         return new FunctionNode(this, returnType, second, first);
      }

      // TODO have boolean specific version of Equal, other versions won't need following logic
      if (Utils.TRUE_NODE.equals(first)) {
         return second;
      }
      if (Utils.FALSE_NODE.equals(first)) {
         return IsFalse.negate(second);
      }

      return null;
   }

   @Override
   public String getDisplayName() {
      return "=";
   }

   @Override
   public Node getOpposite(FunctionNode fn) {
      return new FunctionNode(NotEqual.getSingleton(), fn.getType(), fn.getChildren());
   }

   @Override
   public Set<Node> getIncompatibles(FunctionNode fn) {
      Set<Node> result = new HashSet<>();

      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      result.add(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()));
      result.add(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs));

      return result;
   }

   @Override
   public Set<Node> getConsequences(FunctionNode fn) {
      Set<Node> result = new HashSet<>();

      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      result.add(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), fn.getChildren()));
      result.add(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), swappedArgs));

      return result;
   }
}
