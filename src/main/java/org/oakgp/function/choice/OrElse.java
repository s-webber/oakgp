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
package org.oakgp.function.choice;

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.nullableType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Returns the first argument if not {@code null}, else returns the second argument. */
public final class OrElse implements Function {
   private final Signature signature;

   /** Constructs a selection operator that returns values of the specified type. */
   public OrElse() {
      Type type = Types.generic("Type");
      signature = Signature.createSignature(type, nullableType(type), type);
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Object result = arguments.first();
      if (result == null) {
         return arguments.second();
      } else {
         return result;
      }
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      List<Node> nodes = new ArrayList<>();
      nodes.add(children.first());
      Node next = children.second();
      int indexOfLastDuplicate = 0;
      Node nodeAfterLastDuplicate = null;
      while (isFunction(next) && ((FunctionNode) next).getFunction() == this) {
         FunctionNode fn = ((FunctionNode) next);
         ChildNodes childsChildren = fn.getChildren();
         if (nodes.contains(childsChildren.first())) {
            indexOfLastDuplicate = nodes.size();
            nodeAfterLastDuplicate = childsChildren.second();
         } else {
            nodes.add(childsChildren.first());
         }
         next = childsChildren.second();
      }

      if (indexOfLastDuplicate == 0) {
         return null;
      }

      Node n = nodeAfterLastDuplicate;
      for (int i = indexOfLastDuplicate - 1; i > -1; i--) {
         n = new FunctionNode(this, returnType, nodes.get(i), n);
      }
      return n;
   }
}
