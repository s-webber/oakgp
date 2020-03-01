/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.node.Node;
import org.oakgp.primitive.VariableSet;

// TODO review these tests
public class BooleanFunctionUtilsTest {
   private final VariableSet variableSet = VariableSet.createVariableSet(integerType(), integerType(), integerType(), integerType(), booleanType(),
         booleanType());

   @Test
   public void testReplace() {
      Node input = readNode("(+ v0 (+ v1 (+ v2 v0)))");
      Node output = BooleanFunctionUtils.replace(Collections.emptyMap(), input);
      assertEquals("(+ v0 (+ v1 (+ v2 v0)))", output.toString());

      Map<Node, Boolean> map = new HashMap<>();
      map.put(readNode("v0"), true);
      map.put(readNode("v2"), false);
      Node output2 = BooleanFunctionUtils.replace(map, input);
      assertEquals("(+ true (+ v1 (+ false true)))", output2.toString());
   }

   @Test
   public void testReplaceWithNegation() {
      Node input = readNode("(+ v0 (+ v1 (+ v2 v0)))");
      Node output = BooleanFunctionUtils.replaceWithNegation(Collections.emptyMap(), input);
      assertEquals("(+ v0 (+ v1 (+ v2 v0)))", output.toString());

      Map<Node, Boolean> map = new HashMap<>();
      map.put(readNode("v0"), true);
      map.put(readNode("v2"), false);
      Node output2 = BooleanFunctionUtils.replaceWithNegation(map, input);
      assertEquals("(+ false (+ v1 (+ true false)))", output2.toString());
   }

   @Test
   public void testGetFactsWhenTrue() {
      Node n = readNode("v0");
      Map<Node, Boolean> facts = BooleanFunctionUtils.getFactsWhenTrue(n);
      assertEquals(Collections.singletonMap(n, true), facts);
   }

   @Test
   public void testGetFactsWhenFalse() {
      Node n = readNode("v0");
      Map<Node, Boolean> facts = BooleanFunctionUtils.getFactsWhenFalse(n);
      assertEquals(Collections.singletonMap(n, false), facts);
   }

   @Test
   public void testGetConsequences() {
      Node n = readNode("v0");
      Map<Node, Boolean> facts = BooleanFunctionUtils.getConsequences(n);
      assertEquals(Collections.singletonMap(n, true), facts);
   }

   @Test
   public void testGetOpposite() {
      Node n = readNode("v0");
      assertNull(BooleanFunctionUtils.getOpposite(n));
   }

   @Test
   public void testIsOpposite() {
      Node n1 = readNode("v0");
      Node n2 = readNode("v1");
      assertFalse(BooleanFunctionUtils.isOpposite(n1, n1));
      assertFalse(BooleanFunctionUtils.isOpposite(n1, n2));
   }

   @Test
   public void testGetUnion() {
      Node n1 = readNode("v0");
      Node n2 = readNode("v1");
      assertNull(BooleanFunctionUtils.getUnion(n1, n1));
      assertNull(BooleanFunctionUtils.getUnion(n1, n2));
   }

   @Test
   public void testGetIntersection() {
      Node n1 = readNode("v0");
      Node n2 = readNode("v1");
      assertNull(BooleanFunctionUtils.getIntersection(n1, n1));
      assertNull(BooleanFunctionUtils.getIntersection(n1, n2));
   }

   private Node readNode(String input) {
      return TestUtils.readNode(input, variableSet);
   }
}
