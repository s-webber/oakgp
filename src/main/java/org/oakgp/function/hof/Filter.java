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
package org.oakgp.function.hof;

import static java.util.Collections.unmodifiableList;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.HigherOrderFunctionArguments;
import org.oakgp.function.Signature;
import org.oakgp.type.Types.Type;

/**
 * Uses a function to filter the elements of a collection.
 * <p>
 * Returns a new collection that exists of all the elements in the collection (specified by the second argument) for which the function (specified by the first
 * argument) returns {@code true}.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Filter_(higher-order_function)">Wikipedia</a>
 */
public final class Filter implements Function {
   private final Signature signature;

   /**
    * Creates a higher order functions that filters the elements of a collection.
    *
    * @param type
    *           the type of the elements contained in the collection
    */
   public Filter(Type type) {
      signature = Signature.createSignature(listType(type), functionType(booleanType(), type), listType(type));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Function f = arguments.first();
      Collection<Object> candidates = arguments.second();
      List<Object> result = new ArrayList<>();
      for (Object candidate : candidates) {
         if ((Boolean) f.evaluate(new HigherOrderFunctionArguments(candidate))) {
            result.add(candidate);
         }
      }
      return unmodifiableList(result);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
