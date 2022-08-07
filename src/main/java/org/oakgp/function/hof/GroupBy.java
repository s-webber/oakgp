/*
 * Copyright 2018 S. Webber
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

import static java.util.Collections.unmodifiableMap;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.mapType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Uses a function to group elements of a collection. */
public final class GroupBy implements Function {
   private final Signature signature;

   public GroupBy() {
      Type key = Types.generic("Key");
      Type input = Types.generic("Input");
      signature = Signature.createSignature(mapType(key, listType(input)), functionType(key, input), listType(input));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Function f = arguments.first().evaluate(assignments);
      Node second = arguments.second();
      Type type = second.getType().getParameter(0);
      Collection<Object> elements = second.evaluate(assignments);
      LinkedHashMap<Object, ArrayList<Object>> result = new LinkedHashMap<>();
      for (Object element : elements) {
         ChildNodes childNodes = ChildNodes.createChildNodes(new ConstantNode(element, type));
         Object evaluateResult = f.evaluate(childNodes, assignments);
         result.computeIfAbsent(evaluateResult, k -> new ArrayList<>()).add(element);
      }
      return unmodifiableMap(result);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
