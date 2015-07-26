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
package org.oakgp.examples.simple;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.rank.fitness.TestDataFitnessFunction;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

/** An example of using symbolic regression to evolve a program that best fits a given data set for the function {@code x2 + x + 1}. */
public class SymbolicRegressionExample {
   private static final int TARGET_FITNESS = 0;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      // the function set will be the addition, subtraction and multiplication arithmetic operators
      Function[] functions = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply() };
      // the constant set will contain the integers in the range 0-10 inclusive
      ConstantNode[] constants = Utils.createIntegerConstants(0, 10);
      // the variable set will contain a single variable - representing the integer value input to the function
      Type[] variableTypes = { Type.integerType() };
      // the fitness function will compare candidates against a data set which maps inputs to their expected outputs
      FitnessFunction fitnessFunction = TestDataFitnessFunction.createIntegerTestDataFitnessFunction(createDataSet());

      new RunBuilder().setReturnType(Type.integerType()).setConstants(constants).setVariables(variableTypes).setFunctions(functions)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setTargetFitness(TARGET_FITNESS).process();
   }

   /**
    * Returns the data set used to assess the fitness of candidates.
    * <p>
    * Creates a map of input values in the range [-10,+10] to the corresponding expected output value.
    */
   private static Map<Assignments, Integer> createDataSet() {
      Map<Assignments, Integer> tests = new HashMap<>();
      for (int i = -10; i < 11; i++) {
         Assignments assignments = Assignments.createAssignments(i);
         tests.put(assignments, getExpectedOutput(i));
      }
      return tests;
   }

   private static int getExpectedOutput(int x) {
      return (x * x) + x + 1;
   }
}
