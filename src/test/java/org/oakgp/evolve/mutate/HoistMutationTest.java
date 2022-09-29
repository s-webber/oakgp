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
package org.oakgp.evolve.mutate;

import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.Random;

public class HoistMutationTest {
   @Test
   public void testTerminal() {
      ConstantNode constant = integerConstant(1);
      Node result = hoistMutate(DummyRandom.EMPTY, constant);
      assertSame(constant, result);
   }

   @Test
   public void testFunctionNodeNoAlternatives() {
      // tests when the return type of the root node (in this case a boolean)
      // is not same type of any of the other nodes in the tree (in this case they are all integers)
      Node input = readNode("(zero? (+ (+ v0 v1) (+ 9 v2)))");
      Node result = hoistMutate(DummyRandom.EMPTY, input);
      assertSame(input, result);
   }

   @Test
   public void testFunctionNode() {
      Node input = readNode("(+ (+ (if (zero? v0) 7 8) v1) (+ 9 v2))");
      DummyNodeSelector selector = DummyNodeSelector.repeat(9, input);
      DummyRandom random = nextInt(9).returns(3, 4, 5, 2, 1, 8, 6, 7, 0);
      GeneticOperator mutator = new MutateWrapper(new HoistMutation(), null, null, random);

      assertNodeEquals("(if (zero? v0) 7 8)", mutator.evolve(selector));
      assertNodeEquals("v1", mutator.evolve(selector));
      assertNodeEquals("(+ (if (zero? v0) 7 8) v1)", mutator.evolve(selector));
      assertNodeEquals("8", mutator.evolve(selector));
      assertNodeEquals("7", mutator.evolve(selector));
      assertNodeEquals("(+ 9 v2)", mutator.evolve(selector));
      assertNodeEquals("9", mutator.evolve(selector));
      assertNodeEquals("v2", mutator.evolve(selector));
      assertNodeEquals("v0", mutator.evolve(selector));

      selector.assertEmpty();
      random.assertEmpty();
   }

   private Node hoistMutate(Random random, Node input) {
      DummyNodeSelector selector = new DummyNodeSelector(input);
      Node result = new MutateWrapper(new HoistMutation(), null, null, random).evolve(selector);
      selector.assertEmpty();
      return result;
   }
}
