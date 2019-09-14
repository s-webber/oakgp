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
package org.oakgp.function.compare;

import static org.oakgp.type.CommonTypes.comparableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

public class Min implements Function {
   private static final Min SINGLETON = new Min();

   public static Min getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private Min() {
      Type type = Types.generic("ElementType", comparableType());
      this.signature = Signature.createSignature(type, type, type);
   }

   @Override
   public Comparable evaluate(Arguments arguments) {
      Comparable first = arguments.first();
      Comparable second = arguments.second();
      return first.compareTo(second) < 0 ? first : second;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      return simplifyMinMax(functionNode, this, Max.getSingleton());
   }

   static Node simplifyMinMax(FunctionNode functionNode, Function a, Function b) { // TODO refactor this logic
      Collection<Node> nodes = Utils.toOrderedList(functionNode);

      List<Node> simplifiedArgs = new ArrayList<>();
      for (Node node : nodes) {
         if (NodeType.isFunction(node) && ((FunctionNode) node).getFunction() == b) {
            if (isRequired(nodes, ((FunctionNode) node))) {
               simplifiedArgs.add(node);
            }
         } else {
            simplifiedArgs.add(node);
         }
      }

      if (simplifiedArgs.size() > 1) {
         Node result = Utils.toNode(a, functionNode.getType(), simplifiedArgs);
         return result.equals(functionNode) ? null : result;
      } else {
         return simplifiedArgs.get(0);
      }
   }

   private static boolean isRequired(Collection<Node> nodes, FunctionNode functionNode) { // TODO rename and comment
      Collection<Node> children = Utils.toOrderedList(functionNode);
      // if not disjunct then node is redundant
      // used to simplify "(min (max v1 1) v1)" to "v1"
      return Collections.disjoint(nodes, children);
   }
}
