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
package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;

public class NodeComparatorTest {
   @Test
   public void testCompareVariables() {
      assertOrdered(createVariable(0), createVariable(1));
   }

   @Test
   public void testCompareConstants() {
      assertOrdered(integerConstant(3), integerConstant(7));
   }

   @Test
   public void testCompareFunctionsSameReturnType() {
      // ordering of function nodes is a bit arbitrary (relies on hashCode of Function class name and arguments)
      // the important thing is that it is consistent
      assertOrdered(readNode("(- 1 1)"), readNode("(+ 1 1)"));
      assertOrdered(readNode("(* 3 3)"), readNode("(* 3 4)"));
   }

   @Test
   public void testCompareFunctionsDifferentReturnTypes() {
      // ordering of function nodes is a bit arbitrary (relies on hashCode of Function class name and arguments)
      // the important thing is that it is consistent
      // pos? returns boolean, + returns integer
      assertOrdered(readNode("(pos? 1)"), readNode("(+ 1 1)"));
   }

   @Test
   public void testCompareConstantsToVariables() {
      assertOrdered(integerConstant(7), createVariable(3));
      assertOrdered(integerConstant(3), createVariable(7));
   }

   @Test
   public void testCompareConstantsToFunctions() {
      assertOrdered(integerConstant(7), readNode("(+ 1 1)"));
   }

   @Test
   public void testCompareVariablesToFunctions() {
      assertOrdered(createVariable(7), readNode("(+ 1 1)"));
   }

   private void assertOrdered(Node n1, Node n2) {
      assertEquals(0, NODE_COMPARATOR.compare(n1, n1));
      assertTrue(NODE_COMPARATOR.compare(n1, n2) < 0);
      assertTrue(NODE_COMPARATOR.compare(n2, n1) > 0);
   }

   @Test
   public void testSort() {
      Node f1 = readNode("(+ 1 1)");
      Node f2 = readNode("(- 1 1)");
      Node f3 = readNode("(* 1 1)");
      List<Node> nodes = new ArrayList<>();
      nodes.add(f1);
      nodes.add(f2);
      nodes.add(readNode("-1"));
      nodes.add(readNode("v1"));
      nodes.add(readNode("3"));
      nodes.add(f3);
      nodes.add(readNode("v0"));
      Collections.sort(nodes, NODE_COMPARATOR);

      assertEquals("-1", nodes.get(0).toString());
      assertEquals("3", nodes.get(1).toString());
      assertEquals("v0", nodes.get(2).toString());
      assertEquals("v1", nodes.get(3).toString());
      assertEquals(f2, nodes.get(4));
      assertEquals(f1, nodes.get(5));
      assertEquals(f3, nodes.get(6));
   }
}
