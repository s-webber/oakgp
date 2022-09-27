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
import static org.oakgp.type.CommonTypes.mapType;

import java.util.Map;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.MapperFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Determines the number of entries contained in a map. */
public final class CountMap implements Function {
   private static final CountMap SINGLETON = new CountMap();

   public static CountMap getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   /** Constructs a function to return the number of items in collections of the specified type. */
   private CountMap() {
      Type key = Types.generic("Key");
      Type value = Types.generic("Value");
      signature = Signature.createSignature(integerType(), mapType(key, value));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Map<?, ?> a = arguments.first().evaluate(assignments);
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
            if (node.getType().getName().equals(java.util.List.class.getName())) { // TODO
               throw new IllegalStateException();
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
