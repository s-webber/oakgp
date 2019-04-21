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
import static org.oakgp.Type.functionType;
import static org.oakgp.Type.listType;
import static org.oakgp.Type.mapType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.HigherOrderFunctionArguments;
import org.oakgp.function.Signature;

/** Uses a function to group elements of a collection. */
public final class GroupBy implements Function {
   private final Signature signature;

   public GroupBy(Type input, Type key) {
      signature = Signature.createSignature(mapType(key, listType(input)), functionType(key, input), listType(input));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Function f = arguments.first();
      Collection<Object> elements = arguments.second();
      LinkedHashMap<Object, ArrayList<Object>> result = new LinkedHashMap<>();
      for (Object element : elements) {
         Object evaluateResult = f.evaluate(new HigherOrderFunctionArguments(element));
         result.computeIfAbsent(evaluateResult, k -> new ArrayList<>()).add(element);
      }
      return unmodifiableMap(result);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}