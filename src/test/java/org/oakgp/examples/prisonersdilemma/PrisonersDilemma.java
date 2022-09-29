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
package org.oakgp.examples.prisonersdilemma;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.entryType;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.function.coll.CountList;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Identity;
import org.oakgp.function.hof.IsAny;
import org.oakgp.function.hof.IsEvery;
import org.oakgp.function.hof.IsNone;
import org.oakgp.function.pair.Key;
import org.oakgp.function.pair.Pair;
import org.oakgp.function.pair.Value;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.tournament.TwoPlayerGame;
import org.oakgp.rank.tournament.TwoPlayerGameResult;
import org.oakgp.serialize.NodeWriter;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;

// https://en.wikipedia.org/wiki/Prisoner%27s_dilemma#Friend_or_Foe?
public class PrisonersDilemma {
   private static final int NUM_GENERATIONS = 100;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 5;
   private static final Opponent[] OPPONENTS = new Opponent[] { new Opponent(a -> true), new Opponent(a -> false), new Opponent(a -> false),
         new Opponent(a -> new Random().nextInt(2) == 1), new Opponent(a -> {
            List<Pair<Boolean, Boolean>> history = (List<Pair<Boolean, Boolean>>) a.get(0);
            if (history.size() == 0) {
               return true;
            } else {
               return history.iterator().next().getValue();
            }
         }) };

   @Test
   public void test() {
      CountList countList = new CountList();
      Type historyType = entryType(booleanType());
      FunctionSet functionSet = new FunctionSetBuilder(). //
            add(GreaterThan.getSingleton(), integerType()).add(GreaterThanOrEqual.getSingleton(), integerType()). //
            add(Equal.getSingleton(), integerType()).add(NotEqual.getSingleton(), integerType()). //
            add(Equal.getSingleton(), booleanType()).add(NotEqual.getSingleton(), booleanType()). //
            add(new Key(), booleanType(), booleanType()).add(new Value(), booleanType(), booleanType()). //
            add(org.oakgp.function.hof.Map.getSingleton(), booleanType(), historyType). //
            add(new IsEvery(), booleanType()). //
            add(new IsAny(), booleanType()). //
            add(new IsNone(), booleanType()). //
            add(countList, historyType). //
            add(countList, booleanType()). //
            add(IsFalse.getSingleton()).add(NotEqual.getSingleton(), integerType()). //
            build();

      RankedCandidates output = new RunBuilder(). //
            setReturnType(booleanType()). //
            setConstants(new ConstantNode(true, booleanType()), new ConstantNode(false, booleanType()), new ConstantNode(new Pair(true, true), historyType),
                  new ConstantNode(0, integerType()), new ConstantNode(1, integerType()), new ConstantNode(2, integerType()),
                  new ConstantNode(3, integerType()), new ConstantNode(new Key(), functionType(booleanType(), historyType)),
                  new ConstantNode(new Value(), functionType(booleanType(), historyType)),
                  new ConstantNode(IsFalse.getSingleton(), functionType(booleanType(), booleanType())),
                  new ConstantNode(new Identity(), functionType(booleanType(), booleanType())))
            .setVariables(listType(historyType)). //
            setFunctionSet(functionSet). //
            setFitnessFunction(PrisonersDilemma::fitness). //
            setInitialPopulationSize(INITIAL_POPULATION_SIZE). //
            setTreeDepth(INITIAL_POPULATION_MAX_DEPTH). //
            setMaxGenerations(NUM_GENERATIONS). //
            process();
      Node best = output.best().getNode();
      System.out.println(new NodeWriter().writeNode(best));
      Node wrapper = new Opponent(a -> {
         Boolean b = best.evaluate(a, null);
         System.out.println(b);
         return b;
      });
      System.out.println(fitness(wrapper));
      for (Opponent o : OPPONENTS) {
         System.out.println(fitness(o));
      }
   }

   private static double fitness(Node n) {
      Game game = new Game();
      double score = 0;
      for (Opponent o : OPPONENTS) {
         score += game.evaluate(n, o).getFitness1();
      }
      return score;
   }

   private static class Game implements TwoPlayerGame {
      private static final int NUM_ROUNDS = 10;

      @Override
      public TwoPlayerGameResult evaluate(Node player1, Node player2) {
         LinkedList<Pair<Boolean, Boolean>> history1 = new LinkedList<>();
         LinkedList<Pair<Boolean, Boolean>> history2 = new LinkedList<>();
         double result1 = 0;
         double result2 = 0;

         for (int round = 0; round < NUM_ROUNDS; round++) {
            Boolean output1 = player1.evaluate(Assignments.createAssignments(history1), null);
            Boolean output2 = player2.evaluate(Assignments.createAssignments(history2), null);
            history1.add(new Pair<>(output1, output2));
            history2.add(new Pair<>(output2, output1));

            if (output1 && output2) { // both cooperate
               result1 += 2;
               result2 += 2;
            } else if (!output1 && !output2) {
               result1 += 5;
               result2 += 5;
            } else if (output1) {
               result1 += 10;
            } else if (output2) {
               result2 += 10;
            }
         }

         return new TwoPlayerGameResult(result1 + result2, result2 + result1);
      }
   }

   private static class Opponent implements Node { // TODO merge with DummyNode and move to main
      private final java.util.function.Function<Assignments, Boolean> logic;

      Opponent(java.util.function.Function<Assignments, Boolean> logic) {
         this.logic = logic;
      }

      @Override
      public Boolean evaluate(Assignments assignments, AutomaticallyDefinedFunctions adfs) {
         return logic.apply(assignments);
      }

      @Override
      public int getNodeCount() {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getHeight() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Type getType() {
         throw new UnsupportedOperationException();
      }

      @Override
      public NodeType getNodeType() {
         throw new UnsupportedOperationException();
      }
   }
}
