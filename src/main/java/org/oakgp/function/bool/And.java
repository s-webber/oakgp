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

import java.util.Collection;
import java.util.Iterator;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunctionUtils;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if two boolean expressions both evaluate to {@code true}. */
public final class And implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());
   private static final And SINGLETON = new And();

   public static And getSingleton() {
      return SINGLETON;
   }

   private And() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      return (boolean) arguments.first() && (boolean) arguments.second();
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      // (and true false) / (and false true) / (and false false) -> false
      if (Utils.FALSE_NODE.equals(arg1) || Utils.FALSE_NODE.equals(arg2)) {
         return Utils.FALSE_NODE;
      }

      // (and true v0) -> v0
      if (Utils.TRUE_NODE.equals(arg1)) {
         return arg2;
      }
      // (and v0 true) -> v0
      if (Utils.TRUE_NODE.equals(arg2)) {
         return arg1;
      }

      // (and v0 v0) -> v0
      if (arg1.equals(arg2)) {
         return arg1;
      }

      Collection<Node> orderedList = Utils.toOrderedList(functionNode);
      Node result = Utils.toNode(functionNode.getFunction(), functionNode.getType(), orderedList);
      if (!functionNode.equals(result)) {
         return result;
      }

      // (and (or (false? v0) v1) v0) -> (and v1 v0)
      Node simplifiedRightBranch = BooleanFunctionUtils.replace(BooleanFunctionUtils.getFactsWhenTrue(arg1), arg2);
      if (arg2 != simplifiedRightBranch) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(arg1, simplifiedRightBranch));
      }
      Node simplifiedLeftBranch = BooleanFunctionUtils.replace(BooleanFunctionUtils.getFactsWhenTrue(arg2), arg1);
      if (arg1 != simplifiedLeftBranch) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(simplifiedLeftBranch, arg2));
      }

      boolean updated = false;
      Iterator<Node> itr = orderedList.iterator();
      for (int i = 0; i < orderedList.size() - 1; i++) {
         Node other = itr.next();
         Node common = BooleanFunctionUtils.getIntersection(arg1, other);
         if (common != null) {
            updated = true;
            orderedList.remove(arg1);
            orderedList.remove(other);
            orderedList.add(common);
         }
      }
      if (updated) {
         return Utils.toNode(functionNode.getFunction(), functionNode.getType(), orderedList);
      }

      return null;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
