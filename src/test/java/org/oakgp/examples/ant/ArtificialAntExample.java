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
package org.oakgp.examples.ant;

import static org.oakgp.examples.ant.AntMovement.FORWARD;
import static org.oakgp.examples.ant.AntMovement.LEFT;
import static org.oakgp.examples.ant.AntMovement.RIGHT;
import static org.oakgp.examples.ant.BiSequence.BISEQUENCE;
import static org.oakgp.examples.ant.MutableState.STATE_TYPE;
import static org.oakgp.examples.ant.TriSequence.TRISEQUENCE;
import static org.oakgp.util.Void.VOID_CONSTANT;
import static org.oakgp.util.Void.VOID_TYPE;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.node.Node;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.util.RunBuilder;

public class ArtificialAntExample {
   private static final int TARGET_FITNESS = 0;
   private static final int NUM_GENERATIONS = 1000;
   private static final int INITIAL_POPULATION_SIZE = 100;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { new If(VOID_TYPE), new IsFoodAhead(), FORWARD, LEFT, RIGHT, BISEQUENCE, TRISEQUENCE };
      FitnessFunction fitnessFunction = new ArtificialAntFitnessFunction();

      RankedCandidates output = new RunBuilder().setReturnType(VOID_TYPE).setConstants(VOID_CONSTANT).setVariables(STATE_TYPE).setFunctions(functions)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setTargetFitness(TARGET_FITNESS).setMaxGenerations(NUM_GENERATIONS).process();
      Node best = output.best().getNode();
      System.out.println(best);
   }
}
