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
package org.oakgp.function.hof;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Returns {@code true} if the given function returns {@code true} for every element in the given collection. */
public final class IsEvery implements Function {
   private final Signature signature;

   public IsEvery() {
      Type type = Types.generic("Type");
      signature = Signature.createSignature(booleanType(), functionType(booleanType(), type), listType(type));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Function f = arguments.first().evaluate(assignments, adfs);
      Node second = arguments.second();
      Type type = second.getType().getParameters().get(0);
      Collection<Object> candidates = second.evaluate(assignments, adfs);
      for (Object candidate : candidates) {
         ChildNodes childNodes = ChildNodes.createChildNodes(new ConstantNode(candidate, type));
         if (!(Boolean) f.evaluate(childNodes, assignments, adfs)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
