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

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.util.DummyRandom.random;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class SubtreeCrossoverTest {
   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = random().nextInt(2).returns(1).nextInt(5).returns(3).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

      GeneticOperator c = new SubtreeCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("(+ 9 (- 8 v5))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   @Test
   public void testConstantNodes() {
      DummyRandom dummyRandom = nextInt(1).returns(0);
      DummyNodeSelector dummySelector = new DummyNodeSelector("1", "2");

      GeneticOperator c = new SubtreeCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("2", result);
      dummySelector.assertEmpty();
   }

   /** Test crossover using trees that use a mix of types (booleans and integers) */
   @Test
   public void testMixedTypes() {
      String input = "(+ 4 5)";
      String output = "(if (< 6 7) 8 9)";

      DummyRandom dummyRandom = random().nextInt(2).returns(0, 1, 1, 1, 1, 1).nextInt(5).returns(0, 0, 1, 2, 3, 4).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output, input, output, input, output, input, output, input, output, input, output);

      GeneticOperator c = new SubtreeCrossover(dummyRandom);

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
      String input = "(+ (if (< 6 7) 8 9) 5)";
      String output = "(+ 1 2)";

      DummyRandom dummyRandom = nextInt(7).returns(2);
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output);

      GeneticOperator c = new SubtreeCrossover(dummyRandom);

      assertNodeEquals(input, c.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }
}
