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
package org.oakgp.evaluate.fitness;

import java.util.Map;
import java.util.function.ToDoubleBiFunction;

import org.oakgp.Assignments;
import org.oakgp.node.Node;

/**
 * Calculates the fitness of a potential solution by comparing its results against the expected values.
 *
 * @param <T>
 *           the type of the expected values
 */
public final class TestDataFitnessFunction<T> implements FitnessFunction {
   private final Map<Assignments, T> tests;
   private final ToDoubleBiFunction<T, T> rankingFunction;

   /**
    * Constructs a new {@code FitnessFunction} which uses the specified test data to assess the fitness of potential solutions.
    *
    * @param tests
    *           test data which associates a collection of inputs with their expected outcomes
    */
   public static TestDataFitnessFunction<Integer> createIntegerTestDataFitnessFunction(Map<Assignments, Integer> tests) {
      return new TestDataFitnessFunction<>(tests, (e, a) -> Math.abs(e - a));
   }

   public TestDataFitnessFunction(Map<Assignments, T> tests, ToDoubleBiFunction<T, T> rankingFunction) {
      this.tests = tests;
      this.rankingFunction = rankingFunction;
   }

   /**
    * Evaluates the specified {@code Node} using the test data specified when this {@code FitnessFunction} was constructed.
    *
    * @param node
    *           the potential solution that whose fitness will be determined
    * @return the accumulative difference between the expected and actual outputs of evaluating {@code node} using each of the inputs of the test data
    */
   @Override
   public double evaluate(Node node) {
      // TODO check for overflow
      double diff = 0;
      for (Map.Entry<Assignments, T> test : tests.entrySet()) {
         Assignments input = test.getKey();
         T expected = test.getValue();
         T actual = node.evaluate(input);
         diff += rankingFunction.applyAsDouble(expected, actual);
      }
      return diff;
   }
}
