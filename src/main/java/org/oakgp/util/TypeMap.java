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
package org.oakgp.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.oakgp.type.Types.Type;

public class TypeMap<T> {
   private final ConcurrentHashMap<Type, List<T>> valuesByType;

   public TypeMap(Collection<T> input, Function<T, Type> valueToKey) {
      Map<Type, List<T>> valuesGroupedByType = Utils.groupBy(input, valueToKey);
      valuesByType = new ConcurrentHashMap<>();
      for (Map.Entry<Type, List<T>> e : valuesGroupedByType.entrySet()) {
         valuesByType.put(e.getKey(), rename(e.getKey(), valuesGroupedByType));
      }
   }

   public List<T> getByType(Type type) {
      return valuesByType.computeIfAbsent(type, k -> rename(k, valuesByType));
   }

   private static <T> List<T> rename(Type type, Map<Type, List<T>> input) { // TODO rename method
      // use LinkedHashSet so order is predictable and so easier to unit-test
      LinkedHashSet<T> result = new LinkedHashSet<>();
      for (Entry<Type, List<T>> e : input.entrySet()) {
         if (e.getKey().isAssignable(type)) {
            result.addAll(e.getValue());
         }
      }
      // TODO add method to Utils that takes a collection and returns emptyList or unmodifiableList
      return result.isEmpty() ? emptyList() : unmodifiableList(new ArrayList<>(result));
   }
}
