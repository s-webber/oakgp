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
package org.oakgp.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.function.math.IntegerUtils;

public class NodeTypeTest {
   private static final ConstantNode CONSTANT_NODE = TestUtils.integerConstant(7);
   private static final VariableNode VARIABLE_NODE = TestUtils.createVariable(1);
   private static final FunctionNode FUNCTION_NODE = new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerType(), CONSTANT_NODE, VARIABLE_NODE);

   @Test
   public void testIsConstant() {
      assertTrue(NodeType.isConstant(CONSTANT_NODE));
      assertFalse(NodeType.isConstant(FUNCTION_NODE));
      assertFalse(NodeType.isConstant(VARIABLE_NODE));
   }

   @Test
   public void testIsFunction() {
      assertTrue(NodeType.isFunction(FUNCTION_NODE));
      assertFalse(NodeType.isFunction(CONSTANT_NODE));
      assertFalse(NodeType.isFunction(VARIABLE_NODE));
   }

   @Test
   public void testIsVariable() {
      assertTrue(NodeType.isVariable(VARIABLE_NODE));
      assertFalse(NodeType.isVariable(CONSTANT_NODE));
      assertFalse(NodeType.isVariable(FUNCTION_NODE));
   }

   @Test
   public void testIsTerminal() {
      assertTrue(NodeType.isTerminal(CONSTANT_NODE));
      assertTrue(NodeType.isTerminal(VARIABLE_NODE));
      assertFalse(NodeType.isTerminal(FUNCTION_NODE));
   }

   @Test
   public void testAreTerminals() {
      assertTrue(NodeType.areTerminals(CONSTANT_NODE, CONSTANT_NODE));
      assertTrue(NodeType.areTerminals(VARIABLE_NODE, VARIABLE_NODE));
      assertTrue(NodeType.areTerminals(VARIABLE_NODE, CONSTANT_NODE));
      assertTrue(NodeType.areTerminals(CONSTANT_NODE, VARIABLE_NODE));

      assertFalse(NodeType.areTerminals(FUNCTION_NODE, CONSTANT_NODE));
      assertFalse(NodeType.areTerminals(CONSTANT_NODE, FUNCTION_NODE));
      assertFalse(NodeType.areTerminals(FUNCTION_NODE, VARIABLE_NODE));
      assertFalse(NodeType.areTerminals(VARIABLE_NODE, FUNCTION_NODE));
      assertFalse(NodeType.areTerminals(FUNCTION_NODE, FUNCTION_NODE));
   }

   @Test
   public void testAreFunctions() {
      assertTrue(NodeType.areFunctions(FUNCTION_NODE, FUNCTION_NODE));

      assertFalse(NodeType.areFunctions(CONSTANT_NODE, CONSTANT_NODE));
      assertFalse(NodeType.areFunctions(VARIABLE_NODE, VARIABLE_NODE));
      assertFalse(NodeType.areFunctions(VARIABLE_NODE, CONSTANT_NODE));
      assertFalse(NodeType.areFunctions(CONSTANT_NODE, VARIABLE_NODE));
      assertFalse(NodeType.areFunctions(FUNCTION_NODE, CONSTANT_NODE));
      assertFalse(NodeType.areFunctions(CONSTANT_NODE, FUNCTION_NODE));
      assertFalse(NodeType.areFunctions(FUNCTION_NODE, VARIABLE_NODE));
      assertFalse(NodeType.areFunctions(VARIABLE_NODE, FUNCTION_NODE));
   }
}
