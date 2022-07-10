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
package org.oakgp.examples.fulladder;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.entryType;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.function.bool.Xor;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.function.pair.CreatePair;
import org.oakgp.function.pair.Pair;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.TestDataBuilder;
import org.oakgp.rank.fitness.TestDataFitnessFunction;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;

/**
 * A full adder adds three binary numbers.
 * <p>
 * See: https://en.wikipedia.org/wiki/Adder_(electronics)#Full_adder
 */
public class FullAdderTest {
   private static final int TARGET_FITNESS = 0;
   private static final int NUM_GENERATIONS = 1000;
   private static final int INITIAL_POPULATION_SIZE = 100;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   @Test
   public void test() {
      FunctionSet functionSet = new FunctionSetBuilder().add(new CreatePair(), booleanType(), booleanType())
            .addAll(IsFalse.getSingleton(), And.getSingleton(), Or.getSingleton(), Xor.getSingleton()).build();
      Type returnType = entryType(booleanType());
      TestDataFitnessFunction<?> fitnessFunction = new TestDataBuilder().booleanValues().booleanValues().booleanValues()
            .rankEquality(FullAdderTest::getExpectedOutput);

      RankedCandidates output = new RunBuilder(). //
            setReturnType(returnType). //
            setConstants(new ConstantNode(true, booleanType()), new ConstantNode(false, booleanType())). //
            setVariables(booleanType(), booleanType(), booleanType()). //
            setFunctionSet(functionSet). //
            setFitnessFunction(fitnessFunction). //
            setInitialPopulationSize(INITIAL_POPULATION_SIZE). //
            setTreeDepth(INITIAL_POPULATION_MAX_DEPTH). //
            setTargetFitness(TARGET_FITNESS). //
            setMaxGenerations(NUM_GENERATIONS). //
            process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   private static Pair<Boolean, Boolean> getExpectedOutput(Assignments a) {
      int i1 = Boolean.TRUE.equals(a.get(0)) ? 1 : 0;
      int i2 = Boolean.TRUE.equals(a.get(1)) ? 1 : 0;
      int i3 = Boolean.TRUE.equals(a.get(2)) ? 1 : 0;
      int sum = i1 + i2 + i3;
      switch (sum) {
         case 0:
            return new Pair<>(false, false);
         case 1:
            return new Pair<>(true, false);
         case 2:
            return new Pair<>(false, true);
         case 3:
            return new Pair<>(true, true);
         default:
            throw new IllegalStateException();
      }
   }
}
