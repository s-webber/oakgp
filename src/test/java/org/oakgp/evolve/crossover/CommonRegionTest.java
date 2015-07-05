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
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class CommonRegionTest {
   @Test
   public void test() {
      Node n1 = readNode("(+ (+ 1 2) (+ 3 4))");
      Node n2 = readNode("(+ 7 (+ 8 9))");
      int count = CommonRegion.getNodeCount(n1, n2);
      assertEquals(4, count);
      assertNodeEquals("(+ (+ 1 2) (+ 8 4))", CommonRegion.crossoverAt(n1, n2, 0));
      assertNodeEquals("(+ (+ 1 2) (+ 3 9))", CommonRegion.crossoverAt(n1, n2, 1));
      assertNodeEquals("(+ (+ 1 2) (+ 8 9))", CommonRegion.crossoverAt(n1, n2, 2));
      assertNodeEquals("(+ 7 (+ 8 9))", CommonRegion.crossoverAt(n1, n2, 3));
   }
}
