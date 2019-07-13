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

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.listType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

public class Sort implements Function {
   private final Signature signature;

   public Sort(Type t) {
      signature = Signature.createSignature(listType(t), listType(t));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Collection<Comparable<?>> input = arguments.first();
      List<Comparable<?>> output = new ArrayList<>(input);
      output.sort(null);
      return output;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(ChildNodes children) {
      Node n = children.first();
      if (isFunction(n) && ((FunctionNode) n).getFunction() == this) {
         return n;
      } else {
         return null;
      }
   }
}
