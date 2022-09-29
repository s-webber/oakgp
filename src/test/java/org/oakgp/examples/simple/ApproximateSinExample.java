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

import static org.oakgp.rank.fitness.DoubleArrayBuilder.doubles;
import static org.oakgp.type.CommonTypes.doubleType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.math.DoubleUtils;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.TestDataBuilder;
import org.oakgp.rank.fitness.TestDataFitnessFunction;
import org.oakgp.util.ConstantSetBuilder;
import org.oakgp.util.RunBuilder;

public class ApproximateSinExample {
   private static final int MAX_GENERATIONS = 2000;
   private static final int INITIAL_POPULATION_SIZE = 200;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { DoubleUtils.DOUBLE_UTILS.getAdd(), DoubleUtils.DOUBLE_UTILS.getSubtract(), DoubleUtils.DOUBLE_UTILS.getMultiply(),
            DoubleUtils.DOUBLE_UTILS.getDivide() };
      ConstantSet constantSet = new ConstantSetBuilder().doubleRange(-5.0, 5.0, 0.1).build();
      TestDataFitnessFunction<Double> fitnessFunction = new TestDataBuilder() //
            .values(doubles().from(0).to(6.2).increment(.1).build()) //
            .rankCloseness(ApproximateSinExample::getExpectedOutput);

      RankedCandidates output = new RunBuilder().//
            setReturnType(doubleType()). //
            setConstantSet(constantSet). //
            setVariables(doubleType()). //
            setFunctions(functions). //
            setFitnessFunction(fitnessFunction). //
            setInitialPopulationSize(INITIAL_POPULATION_SIZE). //
            setTreeDepth(INITIAL_POPULATION_MAX_DEPTH). //
            setMaxGenerations(MAX_GENERATIONS) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   private static double getExpectedOutput(Assignments a) {
      double x = (double) a.get(0);
      return Math.sin(x);
   }
}
