package org.oakgp.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.function.math.Add;

public class NodeTypeTest {
   private static final ConstantNode CONSTANT_NODE = TestUtils.integerConstant(7);
   private static final VariableNode VARIABLE_NODE = TestUtils.createVariable(1);
   private static final FunctionNode FUNCTION_NODE = new FunctionNode(new Add(), CONSTANT_NODE, VARIABLE_NODE);

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
