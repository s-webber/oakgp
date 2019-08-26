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

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public class Max implements Function {
   private final Signature signature;

   public Max() {
      Type type = Types.generic("ElementType", comparableType());
      this.signature = Signature.createSignature(type, type, type);
   }

   @Override
   public Comparable evaluate(Arguments arguments) {
      Comparable first = arguments.first();
      Comparable second = arguments.second();
      return first.compareTo(second) > 0 ? first : second;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      ChildNodes children = functionNode.getChildren();
      Node first = children.first();
      Node second = children.second();
      if (first.equals(second)) {
         return first;
      } else {
         return null;
      }
   }
}
