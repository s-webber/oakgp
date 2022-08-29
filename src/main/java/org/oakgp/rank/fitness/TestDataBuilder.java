/*
 * Copyright 2022 S. Webber
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
package org.oakgp.rank.fitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;

import org.oakgp.Assignments;

public final class TestDataBuilder {
   private final List<Object[]> inputs = new ArrayList<>();

   public TestDataBuilder booleanValues() {
      return values(Boolean.TRUE, Boolean.FALSE);
   }

   public TestDataBuilder values(Integer[] values) {
      return values((Object[]) values);
   }

   public TestDataBuilder values(Double[] values) {
      return values((Object[]) values);
   }

   public TestDataBuilder values(Object... values) {
      inputs.add(values);
      return this;
   }

   public TestDataBuilder values(Collection<?> values) {
      return values(values.toArray());
   }

   // TODO have version that takes integer
   public TestDataFitnessFunction<Double> rankCloseness(Function<Assignments, Double> f) {
      return rank(f, (e, a) -> Math.abs(e - a));
   }

   public <T> TestDataFitnessFunction<T> rankEquality(Function<Assignments, T> f) {
      return rank(f, (e, a) -> Objects.equals(e, a) ? 0 : 1);
   }

   public <T> TestDataFitnessFunction<T> rank(Function<Assignments, T> f, ToDoubleBiFunction<T, T> rankingFunction) {
      Map<Assignments, T> testCases = new LinkedHashMap<>();
      int i = 0;
      int[] next = new int[inputs.size()];
      Object[] permutation = new Object[inputs.size()];
      while (i > -1) {
         if (i == permutation.length) {
            Assignments assignments = Assignments.createAssignments(permutation);
            testCases.put(assignments, f.apply(assignments));
            i--;
         } else if (next[i] == inputs.get(i).length) {
            next[i] = 0;
            i--;
         } else {
            permutation[i] = inputs.get(i)[next[i]];
            next[i]++;
            i++;
         }
      }

      return new TestDataFitnessFunction<>(testCases, rankingFunction);
   }

   // TODO add selectAll(), select(int) and select(int,Random)
}
