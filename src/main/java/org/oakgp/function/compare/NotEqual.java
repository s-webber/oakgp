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

import org.oakgp.function.BooleanFunctionUtils;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/**
 * Determines if two objects are not equal.
 * <p>
 * <b>Note:</b> Equality is checked using {@code Comparable#compareTo(Object)} rather than {@code Object#equals(Object)}.
 */
public final class NotEqual extends ComparisonOperator {
   private static final NotEqual SINGLETON = new NotEqual();

   public static NotEqual getSingleton() {
      return SINGLETON;
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private NotEqual() {
      super(false);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff != 0;
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

      // TODO have boolean specific version of NotEqual, other versions won't need following logic
      if (Utils.FALSE_NODE.equals(first)) {
         return second;
      }
      if (Utils.TRUE_NODE.equals(first)) {
         return IsFalse.negate(second);
      }

      if (BooleanFunctionUtils.isOpposite(first, second)) {
         return Utils.TRUE_NODE;
      }

      Node union = BooleanFunctionUtils.getUnion(first, second);
      if (union != null) {
         return BooleanFunctionUtils.getOpposite(union);
      }

      return null;
   }

   @Override
   public String getDisplayName() {
      return "!=";
   }

   @Override
   public Node getOpposite(FunctionNode fn) {
      return new FunctionNode(Equal.getSingleton(), fn.getType(), fn.getChildren());
   }

   @Override
   public Set<Node> getCauses(FunctionNode fn) {
      Set<Node> result = new HashSet<>();

      ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
      result.add(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()));
      result.add(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs));

      return result;
   }
}
