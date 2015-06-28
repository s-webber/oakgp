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
package org.oakgp.function.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.integerType;

import org.junit.Test;
import org.oakgp.function.choice.If;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class NumberUtilsTest {
   private static final IntegerUtils NUMBER_UTILS = IntegerUtils.INTEGER_UTILS;

   @Test
   public void testNegate() {
      assertNegate("1", "-1");
      assertNegate("-1", "1");
      assertNegate("(+ v0 v1)", "(- 0 (+ v0 v1))");
   }

   private void assertNegate(String before, String after) {
      Node input = readNode(before);
      Node output = readNode(after);
      assertEquals(output, NUMBER_UTILS.negate(input));
   }

   @Test
   public void testMultiplyByTwo() {
      assertMultiplyByTwo("v0", "(* 2 v0)");
      assertMultiplyByTwo("(+ v0 v1)", "(* 2 (+ v0 v1))");
   }

   private void assertMultiplyByTwo(String before, String after) {
      Node input = readNode(before);
      Node output = readNode(after);
      assertEquals(output, NUMBER_UTILS.multiplyByTwo(input));
   }

   @Test
   public void testIsAdd() {
      assertIsAdd("(+ 1 2)", true);
      assertIsAdd("(- 1 2)", false);
      assertIsAdd("(* 1 2)", false);
      assertIsAdd("1", false);
      assertIsAdd("true", false);
   }

   private void assertIsAdd(String input, boolean expectedResult) {
      Node n = readNode(input);
      if (n instanceof FunctionNode) {
         assertEquals(expectedResult, NUMBER_UTILS.isAdd(((FunctionNode) n).getFunction()));
      } else {
         assertFalse(expectedResult);
      }
   }

   @Test
   public void testIsSubtract() {
      assertIsSubtract("(- 1 2)", true);
      assertIsSubtract("(+ 1 2)", false);
      assertIsSubtract("(* 1 2)", false);
      assertIsSubtract("1", false);
      assertIsSubtract("true", false);
   }

   private void assertIsSubtract(String input, boolean expectedResult) {
      Node n = readNode(input);
      assertEquals(expectedResult, NUMBER_UTILS.isSubtract(n));
      if (n instanceof FunctionNode) {
         assertEquals(expectedResult, NUMBER_UTILS.isSubtract((FunctionNode) n));
         assertEquals(expectedResult, NUMBER_UTILS.isSubtract(((FunctionNode) n).getFunction()));
      } else {
         assertFalse(expectedResult);
      }
   }

   @Test
   public void testIsMultiply() {
      assertIsMultiply("(* 1 2)", true);
      assertIsMultiply("(+ 1 2)", false);
      assertIsMultiply("(- 1 2)", false);
      assertIsMultiply("1", false);
      assertIsMultiply("true", false);
   }

   private void assertIsMultiply(String input, boolean expectedResult) {
      Node n = readNode(input);
      if (n instanceof FunctionNode) {
         assertEquals(expectedResult, NUMBER_UTILS.isMultiply((FunctionNode) n));
         assertEquals(expectedResult, NUMBER_UTILS.isMultiply(((FunctionNode) n).getFunction()));
      } else {
         assertFalse(expectedResult);
      }
   }

   @Test
   public void testIsAddOrSubtract() {
      assertTrue(NUMBER_UTILS.isAddOrSubtract(NUMBER_UTILS.getAdd()));
      assertTrue(NUMBER_UTILS.isAddOrSubtract(NUMBER_UTILS.getSubtract()));
      assertFalse(NUMBER_UTILS.isAddOrSubtract(NUMBER_UTILS.getMultiply()));
      assertFalse(NUMBER_UTILS.isAddOrSubtract(new If(integerType())));
   }
}
