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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.function.math.IntegerUtils.INTEGER_UTILS;
import static org.oakgp.util.Utils.TRUE_NODE;

import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.node.VariableNode;

public class StrategyWalkTest {
   @Test
   public void testGetNodeCount_FunctionNode() {
      Node tree = readNode("(+ (+ 1 v0) (+ (+ v0 v1) 2))");
      assertEquals(3, StrategyWalk.getNodeCount(tree, NodeType::isVariable));
      assertEquals(2, StrategyWalk.getNodeCount(tree, NodeType::isConstant));
      assertEquals(4, StrategyWalk.getNodeCount(tree, NodeType::isFunction));
   }

   @Test
   public void testGetNodeCount_ConstantNode() {
      final ConstantNode c = integerConstant(7);
      assertEquals(1, StrategyWalk.getNodeCount(c, n -> n == c));
      assertEquals(0, StrategyWalk.getNodeCount(c, n -> n != c));
   }

   @Test
   public void testGetNodeCount_VariableNode() {
      final VariableNode v = createVariable(0);
      assertEquals(1, StrategyWalk.getNodeCount(v, n -> n == v));
      assertEquals(0, StrategyWalk.getNodeCount(v, n -> n != v));
   }

   @Test
   public void testGetAt_FunctionNode() {
      VariableNode v0 = createVariable(0);
      VariableNode v1 = createVariable(1);
      VariableNode v2 = createVariable(2);
      ConstantNode c1 = integerConstant(0);
      Function f = INTEGER_UTILS.getAdd();
      FunctionNode branch1 = new FunctionNode(f, v0, c1);
      FunctionNode branch2 = new FunctionNode(f, v2, v1);
      FunctionNode tree = new FunctionNode(f, branch1, branch2);

      assertSame(v0, StrategyWalk.getAt(tree, 0, NodeType::isVariable));
      assertSame(v2, StrategyWalk.getAt(tree, 1, NodeType::isVariable));
      assertSame(v1, StrategyWalk.getAt(tree, 2, NodeType::isVariable));

      assertSame(c1, StrategyWalk.getAt(tree, 0, NodeType::isConstant));

      assertSame(branch1, StrategyWalk.getAt(tree, 0, NodeType::isFunction));
      assertSame(branch2, StrategyWalk.getAt(tree, 1, NodeType::isFunction));
      assertSame(tree, StrategyWalk.getAt(tree, 2, NodeType::isFunction));
   }

   @Test
   public void testGetAt_ConstantNode() {
      final ConstantNode c = integerConstant(7);
      assertTerminalNodeGetAt(c);
   }

   @Test
   public void testGetAt_VariableNode() {
      final VariableNode v = createVariable(0);
      assertTerminalNodeGetAt(v);
   }

   private void assertTerminalNodeGetAt(Node terminalNode) {
      assertTrue(NodeType.isTerminal(terminalNode));
      assertSame(terminalNode, StrategyWalk.getAt(terminalNode, 0, n -> true));
      assertSame(terminalNode, StrategyWalk.getAt(terminalNode, 0, n -> false));
      assertSame(terminalNode, StrategyWalk.getAt(terminalNode, 9, n -> true));
      assertSame(terminalNode, StrategyWalk.getAt(terminalNode, 9, n -> false));
   }

   @Test
   public void testReplaceAt_FunctionNode() {
      String input = "(+ (+ 1 v0) (+ (+ v0 v1) 2))";

      assertFunctionNodeReplaceAt(input, 0, NodeType::isVariable, "(+ (+ 1 true) (+ (+ v0 v1) 2))");
      assertFunctionNodeReplaceAt(input, 1, NodeType::isVariable, "(+ (+ 1 v0) (+ (+ true v1) 2))");
      assertFunctionNodeReplaceAt(input, 2, NodeType::isVariable, "(+ (+ 1 v0) (+ (+ v0 true) 2))");

      assertFunctionNodeReplaceAt(input, 0, NodeType::isConstant, "(+ (+ true v0) (+ (+ v0 v1) 2))");
      assertFunctionNodeReplaceAt(input, 1, NodeType::isConstant, "(+ (+ 1 v0) (+ (+ v0 v1) true))");

      assertFunctionNodeReplaceAt(input, 0, NodeType::isFunction, "(+ true (+ (+ v0 v1) 2))");
      assertFunctionNodeReplaceAt(input, 1, NodeType::isFunction, "(+ (+ 1 v0) (+ true 2))");
      assertFunctionNodeReplaceAt(input, 2, NodeType::isFunction, "(+ (+ 1 v0) true)");
      assertFunctionNodeReplaceAt(input, 3, NodeType::isFunction, "true");
   }

   private void assertFunctionNodeReplaceAt(String input, int index, Predicate<Node> treeWalkerStrategy, String expected) {
      Node actual = StrategyWalk.replaceAt(readNode(input), index, n -> TRUE_NODE, treeWalkerStrategy);
      assertNodeEquals(expected, actual);
   }

   @Test
   public void testReplaceAt_ConstantNode() {
      final ConstantNode c = integerConstant(7);
      assertTerminalNodeReplaceAt(c);
   }

   @Test
   public void testReplaceAt_VariableNode() {
      final VariableNode v = createVariable(0);
      assertTerminalNodeReplaceAt(v);
   }

   private void assertTerminalNodeReplaceAt(Node terminalNode) {
      assertTrue(NodeType.isTerminal(terminalNode));
      final Node r = mock(Node.class);
      assertSame(r, StrategyWalk.replaceAt(terminalNode, 0, n -> r, n -> true));
      assertSame(r, StrategyWalk.replaceAt(terminalNode, 0, n -> r, n -> false));
      assertSame(r, StrategyWalk.replaceAt(terminalNode, 0, n -> r, n -> true));
      assertSame(r, StrategyWalk.replaceAt(terminalNode, 0, n -> r, n -> false));
   }
}
