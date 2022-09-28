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
package org.oakgp.node.walk;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readFunctionNode;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.walk.DepthWalk.DepthWalkStrategy;

public class DepthWalkTest {
   private static final FunctionNode INPUT = readFunctionNode("(+ (+ 6 (+ 7 8)) 9)");

   @Test
   public void testNodeCountFunctionNode() {
      assertEquals(7, DepthWalk.getNodeCount(INPUT, (n, d) -> d > 0));
      assertEquals(6, DepthWalk.getNodeCount(INPUT, (n, d) -> d > 1));
      assertEquals(4, DepthWalk.getNodeCount(INPUT, (n, d) -> d > 2));
      assertEquals(2, DepthWalk.getNodeCount(INPUT, (n, d) -> d > 3));
      assertEquals(0, DepthWalk.getNodeCount(INPUT, (n, d) -> d > 4));
      assertEquals(5, DepthWalk.getNodeCount(INPUT, (n, d) -> d < 4));
   }

   @Test
   public void testNodeCountTerminakNode() {
      Integer value = 9;
      ConstantNode input = integerConstant(value);
      assertEquals(1, DepthWalk.getNodeCount(input, (n, d) -> d == 1 && value == input.evaluate(null, null)));
      assertEquals(0, DepthWalk.getNodeCount(input, (n, d) -> d != 1 || value != input.evaluate(null, null)));
   }

   @Test
   public void testGetAt() {
      DepthWalkStrategy strategy = (n, d) -> d > 2;
      assertNodeEquals("6", DepthWalk.getAt(INPUT, 0, strategy));
      assertNodeEquals("7", DepthWalk.getAt(INPUT, 1, strategy));
      assertNodeEquals("8", DepthWalk.getAt(INPUT, 2, strategy));
      assertNodeEquals("(+ 7 8)", DepthWalk.getAt(INPUT, 3, strategy));
   }

   @Test
   public void testReplaceAt() {
      assertReplaceAt(0, "(+ (+ 3 (+ 7 8)) 9)");
      assertReplaceAt(1, "(+ (+ 6 (+ 4 8)) 9)");
      assertReplaceAt(2, "(+ (+ 6 (+ 7 4)) 9)");
      assertReplaceAt(3, "(+ (+ 6 3) 9)");
      assertReplaceAt(4, "(+ 2 9)");
      assertReplaceAt(5, "(+ (+ 6 (+ 7 8)) 2)");
      assertReplaceAt(6, "1");
   }

   private void assertReplaceAt(int index, String expected) {
      assertNodeEquals(expected, DepthWalk.replaceAt(INPUT, index, (n, d) -> integerConstant(d)));
   }
}
