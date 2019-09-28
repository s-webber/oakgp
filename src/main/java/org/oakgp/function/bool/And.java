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

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if two boolean expressions both evaluate to {@code true}. */
public final class And implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());

   @Override
   public Object evaluate(Arguments arguments) {
      return (boolean) arguments.first() && (boolean) arguments.second();
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      if (Utils.FALSE_NODE.equals(arg1) || Utils.FALSE_NODE.equals(arg2)) {
         return Utils.FALSE_NODE;
      } else if (Utils.TRUE_NODE.equals(arg1)) {
         return arg2;
      } else if (Utils.TRUE_NODE.equals(arg2)) {
         return arg1;
      } else {
         // (
         return Utils.toOrderedNode(functionNode);
      }
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
