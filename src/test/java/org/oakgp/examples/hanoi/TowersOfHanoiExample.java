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
package org.oakgp.examples.hanoi;

import static org.oakgp.Type.integerType;
import static org.oakgp.Type.nullableType;
import static org.oakgp.Type.type;
import static org.oakgp.util.Utils.addArray;
import static org.oakgp.util.Utils.createEnumConstants;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Type;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.SwitchEnum;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

public class TowersOfHanoiExample {
   static final Type STATE_TYPE = type("gameState");
   static final Type MOVE_TYPE = type("move");
   static final Type POLE_TYPE = type("pole");

   private static final int TARGET_FITNESS = 0;
   private static final int NUM_GENERATIONS = 1000;
   private static final int INITIAL_POPULATION_SIZE = 100;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { new If(MOVE_TYPE), new Equal(MOVE_TYPE), new IsValid(), new SwitchEnum(Move.class, nullableType(MOVE_TYPE), MOVE_TYPE),
            new GreaterThan(integerType()), new LessThan(integerType()), new Equal(integerType()), new Next() };
      List<ConstantNode> constants = createConstants();
      Type[] variables = { STATE_TYPE, nullableType(MOVE_TYPE) };
      FitnessFunction fitnessFunction = new TowersOfHanoiFitnessFunction(false);

      Node best = new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(variables).setFunctionSet(functions)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setTargetFitness(TARGET_FITNESS).setMaxGenerations(NUM_GENERATIONS).process();

      new TowersOfHanoiFitnessFunction(true).evaluate(best);
   }

   private static List<ConstantNode> createConstants() {
      List<ConstantNode> constants = new ArrayList<>();
      constants.add(IntegerUtils.INTEGER_UTILS.zero());
      constants.add(Utils.TRUE_NODE);
      addArray(constants, createEnumConstants(Move.class, MOVE_TYPE));
      addArray(constants, createEnumConstants(Pole.class, POLE_TYPE));
      return constants;
   }
}
