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
package org.oakgp.examples.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.oakgp.examples.ant.AntMovement.FORWARD;
import static org.oakgp.examples.ant.AntMovement.LEFT;
import static org.oakgp.examples.ant.AntMovement.RIGHT;
import static org.oakgp.examples.ant.BiSequence.BISEQUENCE;
import static org.oakgp.examples.ant.TriSequence.TRISEQUENCE;
import static org.oakgp.type.CommonTypes.voidType;
import static org.oakgp.util.Utils.VOID_NODE;

import org.junit.Test;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class BiSequenceTest {
   private final Node stateVariable = new VariableNode(0, null, MutableState.STATE_TYPE);
   private final Node forward = new FunctionNode(FORWARD, voidType(), stateVariable);
   private final Node left = new FunctionNode(LEFT, voidType(), stateVariable);
   private final Node right = new FunctionNode(RIGHT, voidType(), stateVariable);

   @Test
   public void testSimplifyWhenLeftAndRight() {
      assertEquals(VOID_NODE, simplify(left, right));
   }

   @Test
   public void testSimplifyWhenRightAndLeft() {
      assertEquals(VOID_NODE, simplify(right, left));
   }

   @Test
   public void testFirstArgBi() {
      Node firstArg = new FunctionNode(BISEQUENCE, voidType(), left, forward);
      Node secondArg = right;
      Node expected = new FunctionNode(TRISEQUENCE, voidType(), left, forward, secondArg);
      assertEquals(expected, simplify(firstArg, secondArg));
   }

   @Test
   public void testSecondArgBi() {
      Node firstArg = right;
      Node secondArg = new FunctionNode(BISEQUENCE, voidType(), forward, left);
      Node expected = new FunctionNode(TRISEQUENCE, voidType(), firstArg, forward, left);
      assertEquals(expected, simplify(firstArg, secondArg));
   }

   @Test
   public void testBothArgsBi() {
      Node firstArg = new FunctionNode(BISEQUENCE, voidType(), left, forward);
      Node secondArg = new FunctionNode(BISEQUENCE, voidType(), forward, right);
      Node expected = new FunctionNode(TRISEQUENCE, voidType(), left, forward, secondArg);
      assertEquals(expected, simplify(firstArg, secondArg));
   }

   @Test
   public void testCannotSimplify() {
      assertCannotSimplify(left, left);
      assertCannotSimplify(right, right);
      assertCannotSimplify(left, forward);
      assertCannotSimplify(forward, right);
      assertCannotSimplify(forward, left);
      assertCannotSimplify(right, forward);
      assertCannotSimplify(forward, forward);
   }

   private void assertCannotSimplify(Node first, Node second) {
      assertNull(simplify(first, second));
   }

   private Node simplify(Node first, Node second) {
      return BISEQUENCE.simplify(new FunctionNode(BISEQUENCE, voidType(), ChildNodes.createChildNodes(first, second)));
   }
}
