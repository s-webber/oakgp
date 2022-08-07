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
package org.oakgp.function.bool;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Assignments;
import org.oakgp.function.BooleanFunctionUtils;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if exactly one of two boolean expressions evaluate to {@code true}. */
public final class Xor implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());
   private static final Xor SINGLETON = new Xor();

   public static Xor getSingleton() {
      return SINGLETON;
   }

   private Xor() {
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      boolean b1 = arguments.first().evaluate(assignments);
      boolean b2 = arguments.second().evaluate(assignments);
      return (b1 || b2) && b1 != b2;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      // (xor v0 v0) -> false
      if (arg1.equals(arg2)) {
         return Utils.FALSE_NODE;
      }

      // (xor false v0) -> v0
      if (Utils.FALSE_NODE.equals(arg1)) {
         return arg2;
      }
      // (xor v0 false) -> v0
      if (Utils.FALSE_NODE.equals(arg2)) {
         return arg1;
      }

      // (xor true v0) -> (false? v0)
      if (Utils.TRUE_NODE.equals(arg1)) {
         return IsFalse.negate(arg2);
      }
      // (xor v0 true) -> (false? v0)
      if (Utils.TRUE_NODE.equals(arg2)) {
         return IsFalse.negate(arg1);
      }

      if (NODE_COMPARATOR.compare(arg1, arg2) < 0) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(arg2, arg1));
      }

      if (BooleanFunctionUtils.isOpposite(arg1, arg2)) {
         return Utils.TRUE_NODE;
      }

      return null;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
