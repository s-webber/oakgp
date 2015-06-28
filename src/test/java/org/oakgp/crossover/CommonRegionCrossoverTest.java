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
package org.oakgp.crossover;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class CommonRegionCrossoverTest {
   // TODO test mixed types

   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = nextInt(4).returns(1);
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 5 (+ 1 2))", "(* 7 (- 8 v5))");

      GeneticOperator c = new CommonRegionCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("(+ 5 (+ 8 2))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }
}
