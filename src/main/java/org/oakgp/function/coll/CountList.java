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
package org.oakgp.function.coll;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.MapperFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Determines the number of elements contained in a collection. */
public final class CountList implements Function {
   private final Signature signature;

   /** Constructs a function to return the number of items in collections of the specified type. */
   public CountList() {
      Type type = Types.generic("ElementType");
      signature = Signature.createSignature(integerType(), listType(type));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Collection<?> a = arguments.first().evaluate(assignments);
      return a.size();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node child = functionNode.getChildren().first();
      if (child instanceof FunctionNode) {
         FunctionNode fn = (FunctionNode) child;
         if (fn.getFunction() instanceof MapperFunction) {
            ChildNodes cn = fn.getChildren();
            Node node = cn.getNode(cn.size() - 1);
            if (node.getType().getName().equals("Map")) { // TODO
               return new FunctionNode(CountMap.getSingleton(), integerType(), ChildNodes.createChildNodes(node));
            } else {
               return new FunctionNode(functionNode, ChildNodes.createChildNodes(node));
            }
         }
      }
      return null;
   }

   @Override
   public String getDisplayName() {
      return "count";
   }
}
