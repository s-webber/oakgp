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
package org.oakgp.evolve.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.readFunctionNode;
import static org.oakgp.util.DummyRandom.random;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class SubtreeCrossoverTest {
   private static final int DEFAULT_DEPTH = 9;

   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = random().nextInt(2).returns(1).nextInt(5).returns(3).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

      GeneticOperator c = new CrossoverWrapper(new SubtreeCrossover(DEFAULT_DEPTH), dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("(+ 9 (- 8 v5))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   @Test
   public void testConstantNodes() {
      DummyRandom dummyRandom = nextInt(1).returns(0);
      DummyNodeSelector dummySelector = new DummyNodeSelector("1", "2");

      GeneticOperator c = new CrossoverWrapper(new SubtreeCrossover(DEFAULT_DEPTH), dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("2", result);
      dummySelector.assertEmpty();
   }

   /** Test crossover using trees that use a mix of types (booleans and integers) */
   @Test
   public void testMixedTypes() {
      String input = "(+ 4 5)";
      String output = "(if (> 6 7) 8 9)";

      DummyRandom dummyRandom = random().nextInt(2).returns(0, 1, 1, 1, 1, 1).nextInt(5).returns(0, 0, 1, 2, 3, 4).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output, input, output, input, output, input, output, input, output, input, output);

      GeneticOperator c = new CrossoverWrapper(new SubtreeCrossover(DEFAULT_DEPTH), dummyRandom);

      assertNodeEquals("(+ 6 5)", c.evolve(dummySelector));
      assertNodeEquals("(+ 4 6)", c.evolve(dummySelector));
      assertNodeEquals("(+ 4 7)", c.evolve(dummySelector));
      assertNodeEquals("(+ 4 8)", c.evolve(dummySelector));
      assertNodeEquals("(+ 4 9)", c.evolve(dummySelector));
      assertNodeEquals("(+ 4 " + output + ")", c.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   /** Test attempted crossover when selected node in first parent has a type that is not present in the second parent */
   @Test
   public void testNoMatchingTypes() {
      String input = "(+ (if (> 6 7) 8 9) 5)";
      String output = "(+ 1 2)";

      DummyRandom dummyRandom = nextInt(7).returns(2);
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output);

      GeneticOperator c = new CrossoverWrapper(new SubtreeCrossover(DEFAULT_DEPTH), dummyRandom);

      assertNodeEquals(input, c.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   /** Test max depth limit not exceeded */
   @Test
   public void testMaxDepthLimit() {
      assertCrossoverPossibilities(7);
      assertCrossoverPossibilities(6);
      assertCrossoverPossibilities(5);
      assertCrossoverPossibilities(4);
   }

   private void assertCrossoverPossibilities(int maxDepth) {
      String[] possibilities = { "(+ (+ 9 (+ 3 4)) 5)", "(+ (+ 7 (+ 3 4)) 5)", "(+ (+ 8 (+ 3 4)) 5)", "(+ (+ (* 7 8) (+ 3 4)) 5)",
            "(+ (+ (* 9 (* 7 8)) (+ 3 4)) 5)", "(+ (+ 6 (+ 3 4)) 5)", "(+ (+ (* (* 9 (* 7 8)) 6) (+ 3 4)) 5)" };
      List<FunctionNode> f = Arrays.stream(possibilities).map(s -> readFunctionNode(s)).filter(n -> n.getHeight() <= maxDepth).collect(Collectors.toList());
      int numPossibilities = f.size();
      assertTrue(numPossibilities > 0);
      for (int i = 0; i < numPossibilities; i++) {
         Node result = subtreeCrossover(maxDepth, 6, 0, numPossibilities, i);
         assertEquals(f.get(i), result);
         assertTrue(result.getHeight() <= maxDepth);
      }
   }

   private Node subtreeCrossover(int maxDepth, int first, int second, int third, int fourth) {
      String input = "(+ (+ 2 (+ 3 4)) 5)";
      String output = "(* (* 9 (* 7 8)) 6)";
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output);
      DummyRandom dummyRandom;
      if (first == third) {
         dummyRandom = random().nextInt(first).returns(second, fourth).build();
      } else {
         dummyRandom = random().nextInt(first).returns(second).nextInt(third).returns(fourth).build();
      }

      GeneticOperator c = new CrossoverWrapper(new SubtreeCrossover(maxDepth), dummyRandom);
      Node result = c.evolve(dummySelector);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();

      return result;
   }
}
