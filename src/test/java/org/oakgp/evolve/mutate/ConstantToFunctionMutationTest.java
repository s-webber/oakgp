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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.integerType;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.Random;

public class ConstantToFunctionMutationTest {
   @Test
   public void testTerminal() {
      ConstantNode constant = integerConstant(1);
      Node subTree = readNode("(if true 8 4)");
      TreeGenerator mockTreeGenerator = mock(TreeGenerator.class);
      given(mockTreeGenerator.generate(integerType(), 2)).willReturn(subTree);
      Node result = mutate(nextInt(1).returns(0), mockTreeGenerator, constant);
      assertSame(subTree, result);
   }

   @Test
   public void testTree() {
      Node input = readNode("(if (< 6 v0) 3 7)");
      Node subTree = readNode("(if true 8 4)");
      TreeGenerator mockTreeGenerator = mock(TreeGenerator.class);
      given(mockTreeGenerator.generate(integerType(), 2)).willReturn(subTree);

      assertNodeEquals("(if (< (if true 8 4) v0) 3 7)", mutate(nextInt(4).returns(0), mockTreeGenerator, input));
      assertNodeEquals("(if (< 6 (if true 8 4)) 3 7)", mutate(nextInt(4).returns(1), mockTreeGenerator, input));
      assertNodeEquals("(if (< 6 v0) (if true 8 4) 7)", mutate(nextInt(4).returns(2), mockTreeGenerator, input));
      assertNodeEquals("(if (< 6 v0) 3 (if true 8 4))", mutate(nextInt(4).returns(3), mockTreeGenerator, input));
   }

   private Node mutate(Random random, TreeGenerator treeGenerator, Node input) {
      DummyNodeSelector selector = new DummyNodeSelector(input);
      Node result = new ConstantToFunctionMutation(random, treeGenerator).evolve(selector);
      selector.assertEmpty();
      return result;
   }
}
