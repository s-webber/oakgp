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
package org.oakgp.function.classify;

import static org.oakgp.type.CommonTypes.booleanType;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** Determines if a boolean expression evaluates to {@code false}. */
public final class IsFalse implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType());

   @Override
   public Object evaluate(Arguments arguments) {
      boolean b = arguments.first();
      return !b;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      if (functionNode.getChildren().size() != 1) {
         return null;
      }

      Node childNode = functionNode.getChildren().first();
      if (childNode.getNodeType() != NodeType.FUNCTION) {
         return null;
      }

      FunctionNode childFunctionNode = (FunctionNode) childNode;
      if (childFunctionNode.getFunction() != this) {
         return null;
      }

      return childFunctionNode.getChildren().first();
   }
}
