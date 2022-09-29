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
import org.oakgp.primitive.DummyPrimitiveSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.Random;

public class ShrinkMutationTest {
   @Test
   public void testTerminal() {
      ConstantNode constant = integerConstant(1);
      Node result = shrinkMutate(DummyRandom.EMPTY, new DummyPrimitiveSet(), constant);
      assertSame(constant, result);
   }

   @Test
   public void testFunctionNodeWithTerminalArguments() {
      Node input = readNode("(+ v0 v1)");
      ConstantNode expectedResult = integerConstant(42);
      PrimitiveSet primitiveSet = new DummyPrimitiveSet() {
         @Override
         public Node nextAlternativeTerminal(Node nodeToReplace) {
            assertSame(input, nodeToReplace);
            return expectedResult;
         }
      };
      Node result = shrinkMutate(DummyRandom.EMPTY, primitiveSet, input);
      assertSame(expectedResult, result);
   }

   @Test
   public void testFunctionNode() {
      Node input = readNode("(+ (+ (if (zero? v0) 7 8) v1) (+ 9 v2))");
      DummyNodeSelector selector = DummyNodeSelector.repeat(4, input);
      ConstantNode expectedResult = integerConstant(42);
      PrimitiveSet primitiveSet = new DummyPrimitiveSet() {
         @Override
         public Node nextAlternativeTerminal(Node nodeToReplace) {
            return expectedResult;
         }
      };
      DummyRandom random = nextInt(4).returns(1, 3, 0, 2);
      GeneticOperator mutator = new MutateWrapper(new ShrinkMutation(), primitiveSet, null, random);

      assertNodeEquals("(+ (+ 42 v1) (+ 9 v2))", mutator.evolve(selector));
      assertNodeEquals("(+ (+ (if (zero? v0) 7 8) v1) 42)", mutator.evolve(selector));
      assertNodeEquals("(+ (+ (if 42 7 8) v1) (+ 9 v2))", mutator.evolve(selector));
      assertNodeEquals("(+ 42 (+ 9 v2))", mutator.evolve(selector));

      selector.assertEmpty();
      random.assertEmpty();
   }

   private Node shrinkMutate(Random random, PrimitiveSet primitiveSet, Node input) {
      DummyNodeSelector selector = new DummyNodeSelector(input);
      Node result = new MutateWrapper(new ShrinkMutation(), primitiveSet, null, random).evolve(selector);
      selector.assertEmpty();
      return result;
   }
}
