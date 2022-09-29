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

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;

public class SubTreeMutationTest {
   @Test
   public void test() {
      Node input = readNode("(+ (+ (if (zero? v0) 7 8) v1) (+ 9 v2))");
      DummyNodeSelector selector = new DummyNodeSelector(input);
      ConstantNode result = integerConstant(42);
      GeneticOperator mutator = new MutateWrapper(new SubTreeMutation(), null, (t, d) -> result, nextInt(10).returns(5));
      assertNodeEquals("(+ (+ (if (zero? v0) 7 8) 42) (+ 9 v2))", mutator.evolve(selector));
   }
}
