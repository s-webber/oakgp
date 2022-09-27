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

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.nullableType;
import static org.oakgp.util.Utils.createEnumConstants;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.choice.If;
import org.oakgp.function.choice.SwitchEnum;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

public class TowersOfHanoiExample {
   static final Type STATE_TYPE = Types.type(TowersOfHanoi.class);
   static final Type MOVE_TYPE = Types.type(Move.class);
   static final Type POLE_TYPE = Types.type(Pole.class);

   private static final int TARGET_FITNESS = 0;
   private static final int NUM_GENERATIONS = 10000;
   private static final int INITIAL_POPULATION_SIZE = 200;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      FunctionSet functionSet = new FunctionSetBuilder() //
            .add(new SwitchEnum(Move.class, nullableType(MOVE_TYPE), MOVE_TYPE)) //
            .add(Equal.getSingleton(), MOVE_TYPE) //
            .add(new If(), MOVE_TYPE) //
            .add(GreaterThan.getSingleton(), integerType()) //
            .add(Equal.getSingleton(), integerType()) //
            .addMethods(TowersOfHanoi.class, "upperDisc", "isValid") //
            .build();
      List<ConstantNode> constants = createConstants();
      Type[] variables = { STATE_TYPE, nullableType(MOVE_TYPE) };
      FitnessFunction fitnessFunction = new TowersOfHanoiFitnessFunction(false);

      RankedCandidates output = new RunBuilder() //
            .setReturnType(MOVE_TYPE) //
            .setConstants(constants) //
            .setVariables(variables) //
            .setFunctionSet(functionSet) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setTargetFitness(TARGET_FITNESS) //
            .setMaxGenerations(NUM_GENERATIONS) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
      new TowersOfHanoiFitnessFunction(true).evaluate(best);
   }

   private static List<ConstantNode> createConstants() {
      List<ConstantNode> constants = new ArrayList<>();
      constants.add(IntegerUtils.INTEGER_UTILS.zero());
      constants.add(Utils.TRUE_NODE);
      constants.addAll(createEnumConstants(Move.class, MOVE_TYPE));
      constants.addAll(createEnumConstants(Pole.class, POLE_TYPE));
      return constants;
   }
}
