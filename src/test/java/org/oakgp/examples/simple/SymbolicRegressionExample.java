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

import static org.oakgp.rank.fitness.IntegerArrayBuilder.integers;
import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.TestDataBuilder;
import org.oakgp.rank.fitness.TestDataFitnessFunction;
import org.oakgp.util.ConstantSetBuilder;
import org.oakgp.util.RunBuilder;

/**
 * An example of using symbolic regression to evolve a program that best fits a given data set for the function {@code x2 + x + 1}.
 */
public class SymbolicRegressionExample {
   private static final int TARGET_FITNESS = 0;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      // the function set will be the addition, subtraction and multiplication arithmetic operators
      // the constant set will contain the integers in the range 0-10 inclusive
      // the variable set will contain a single variable - representing the integer value input to the function
      // the fitness function will compare candidates against a data set which maps inputs to their expected outputs

      Function[] functions = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply() };
      ConstantSet constantSet = new ConstantSetBuilder().integerRange(0, 10).build();
      TestDataFitnessFunction<Integer> fitnessFunction = new TestDataBuilder() //
            .values(integers().from(-10).to(10).build()) //
            .rankClosenessInteger(SymbolicRegressionExample::getExpectedOutput);

      RankedCandidates output = new RunBuilder() //
            .setReturnType(integerType()) //
            .setConstants(constantSet) //
            .setVariables(integerType()) //
            .setFunctions(functions) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setTargetFitness(TARGET_FITNESS).process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   private static int getExpectedOutput(Assignments a) {
      int x = (int) a.get(0);
      return (x * x) + x + 1;
   }
}
