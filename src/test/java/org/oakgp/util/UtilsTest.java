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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.mockNode;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public class UtilsTest {
   @Test
   public void testGroupByType() {
      Node n1 = mockNode(integerType());
      Node n2 = mockNode(stringType());
      Node n3 = mockNode(integerType());
      Node n4 = mockNode(integerType());
      Node n5 = mockNode(booleanType());
      Node[] values = { n1, n2, n3, n4, n5 };
      Map<Type, List<Node>> groups = Utils.groupByType(values);
      assertEquals(3, groups.size());
      assertEquals(asList(n1, n3, n4), groups.get(integerType()));
      assertEquals(asList(n2), groups.get(stringType()));
      assertEquals(asList(n5), groups.get(booleanType()));
   }

   @Test
   public void testGroupBy() {
      String[] values = { "aardvark", "apple", "bag", "cat", "cake", "caterpillar" };
      Map<Character, List<String>> groups = Utils.groupBy(values, s -> s.charAt(0));
      assertEquals(3, groups.size());
      assertEquals(asList("aardvark", "apple"), groups.get('a'));
      assertEquals(asList("bag"), groups.get('b'));
      assertEquals(asList("cat", "cake", "caterpillar"), groups.get('c'));
   }

   @Test
   public void testSelectSubNodeIndexFunctionNode() {
      assertSelectSubNodeIndex("(+ (+ 1 2) (+ 3 4))", 7, 3);
      assertSelectSubNodeIndex("(zero? 0)", 2, 0);
   }

   private void assertSelectSubNodeIndex(String input, int expectedNodeCount, int expectedIndex) {
      Node tree = readNode(input);
      assertEquals(expectedNodeCount, tree.getNodeCount());
      int actual = Utils.selectSubNodeIndex(nextInt(expectedNodeCount - 1).returns(expectedIndex), tree);
      assertEquals(expectedIndex, actual);
   }

   @Test
   public void testSelectSubNodeIndexTerminalNode() {
      ConstantNode terminal = integerConstant(1);
      assertEquals(0, Utils.selectSubNodeIndex(DummyRandom.EMPTY, terminal));
   }

   @Test
   public void testSelectSubNodeIndex() {
      int expected = 2;
      assertEquals(expected, Utils.selectSubNodeIndex(nextInt(4).returns(expected), 5));
   }

   @Test
   public void testCreateIntegerConstants() {
      int minInclusive = 7;
      int maxInclusive = 12;

      List<ConstantNode> result = Utils.createIntegerConstants(minInclusive, maxInclusive);

      assertEquals(6, result.size());
      for (int i = 0; i < result.size(); i++) {
         assertSame(integerType(), result.get(i).getType());
         assertEquals(new Integer(i + minInclusive), result.get(i).evaluate(null));
      }
   }

   @Test
   public void testCreateIntegerTypeArray() {
      assertIntegerTypeArray(0);
      assertIntegerTypeArray(1);
      assertIntegerTypeArray(2);
      assertIntegerTypeArray(3);
      assertIntegerTypeArray(100);
   }

   private void assertIntegerTypeArray(int size) {
      Type[] t = Utils.createIntegerTypeArray(size);
      assertEquals(size, t.length);
      for (Type element : t) {
         assertSame(integerType(), element);
      }
   }

   @Test
   public void testCopyOf() {
      String[] original = { "abc", "def", "ghi" };
      String[] copy = Utils.copyOf(original);
      assertNotSame(original, copy);
      assertTrue(Arrays.equals(original, copy));
   }

   @Test
   public void testCreateEnumConstants() {
      Type type = Types.declareType("testCreateEnumConstants");
      TestCreateEnumConstantsEnum[] input = TestCreateEnumConstantsEnum.values();

      List<ConstantNode> result = Utils.createEnumConstants(TestCreateEnumConstantsEnum.class, type);

      assertEquals(input.length, result.size());
      for (int i = 0; i < result.size(); i++) {
         assertSame(type, result.get(i).getType());
         assertSame(input[i], result.get(i).evaluate(null));
      }
   }

   private enum TestCreateEnumConstantsEnum {
      A, B, C
   }
}
