/*
 * Copyright 2019 S. Webber
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
package org.oakgp.examples.poker;

import static org.oakgp.examples.poker.PokerTypes.STARTING_HAND_TYPE;
import static org.oakgp.type.CommonTypes.booleanType;

import java.io.IOException;

import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.function.bool.Xor;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

/**
 *
 * Each player is dealt two cards.
 *
 * ANother five cards appear face up ( Aim is to evolve a function that determines whether we
 *
 * @see https://en.wikipedia.org/wiki/Texas_hold_%27em
 * @see https://app.pluralsight.com/library/courses/genetic-algorithms-genetic-programming/table-of-contents
 */
public class Poker {
   private static final int TARGET_FITNESS = 0;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 9;

   public static void main(String[] args) throws IOException {
      FunctionSet functionSet = new FunctionSetBuilder().add(And.getSingleton()).add(Or.getSingleton()).add(Xor.getSingleton()).add(IsFalse.getSingleton())
            .add(new IsPair()).add(IsSuited.SINGLETON).add(IsConnected.SINGLETON).add(new If(), booleanType()).build();
      Type[] variableTypes = { STARTING_HAND_TYPE };
      FitnessFunction fitnessFunction = n -> 0;

      RankedCandidates output = new RunBuilder().setReturnType(booleanType()).setConstants(Utils.TRUE_NODE, Utils.FALSE_NODE).setVariables(variableTypes)
            .setFunctionSet(functionSet).setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE)
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH).setTargetFitness(TARGET_FITNESS).process();
      Node best = output.best().getNode();
      System.out.println(NodeSimplifier.simplify(best));
   }
}

class IsPair implements Function {
   private final Signature signature = Signature.createSignature(booleanType(), STARTING_HAND_TYPE);

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      StartingHand hand = arguments.first().evaluate(assignments, adfs);
      return hand.first().getRank() == hand.second().getRank();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}

class IsSuited implements BooleanFunction {
   static final IsSuited SINGLETON = new IsSuited();
   private final Signature signature = Signature.createSignature(booleanType(), STARTING_HAND_TYPE);

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      StartingHand hand = arguments.first().evaluate(assignments, adfs);
      return hand.first().getSuit() == hand.second().getSuit();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}

class IsConnected implements BooleanFunction {
   static final IsConnected SINGLETON = new IsConnected();
   private final Signature signature = Signature.createSignature(booleanType(), STARTING_HAND_TYPE);

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      StartingHand hand = arguments.first().evaluate(assignments, adfs);
      return Math.abs(hand.first().getRank().high() - hand.second().getRank().high()) == 1
            || Math.abs(hand.first().getRank().low() - hand.second().getRank().low()) == 1;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
