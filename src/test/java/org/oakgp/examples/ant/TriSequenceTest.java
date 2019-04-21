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
import static org.junit.Assert.assertSame;
import static org.oakgp.examples.ant.AntMovement.FORWARD;
import static org.oakgp.examples.ant.AntMovement.LEFT;
import static org.oakgp.examples.ant.AntMovement.RIGHT;
import static org.oakgp.examples.ant.BiSequence.BISEQUENCE;
import static org.oakgp.examples.ant.TriSequence.TRISEQUENCE;
import static org.oakgp.util.Void.VOID_CONSTANT;

import org.junit.Test;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class TriSequenceTest {
   private final Node stateVariable = new VariableNode(0, MutableState.STATE_TYPE);
   private final Node forward = new FunctionNode(FORWARD, stateVariable);
   private final Node left = new FunctionNode(LEFT, stateVariable);
   private final Node right = new FunctionNode(RIGHT, stateVariable);
   private final Node forwardTwice = new FunctionNode(BISEQUENCE, forward, forward);

   @Test
   public void testSimplifyWhenLeftAndRight() {
      Node a = simplify(left, right, forward);
      Node b = simplify(forward, left, right);
      Node c = simplify(right, left, forward);
      Node d = simplify(forward, right, left);

      assertAllSame(forward, a, b, c, d);
   }

   @Test
   public void testSimplifyWhenThreeLefts() {
      assertEquals(right, simplify(left, left, left));
   }

   @Test
   public void testSimplifyWhenThreeRights() {
      assertEquals(left, simplify(right, right, right));
   }

   @Test
   public void testSimplifyWhenVoid() {
      Node expected = new FunctionNode(BISEQUENCE, forward, forwardTwice);

      Node a = simplify(VOID_CONSTANT, forward, forwardTwice);
      Node b = simplify(forward, VOID_CONSTANT, forwardTwice);
      Node c = simplify(forward, forwardTwice, VOID_CONSTANT);

      assertAllEqual(expected, a, b, c);
   }

   @Test
   public void testCannotSimplify() {
      assertCannotSimplify(forward, forward, forward);
      assertCannotSimplify(forward, forward, left);
      assertCannotSimplify(forward, forward, right);
      assertCannotSimplify(forward, left, forward);
      assertCannotSimplify(forward, right, forward);
      assertCannotSimplify(left, forward, forward);
      assertCannotSimplify(right, forward, forward);
      assertCannotSimplify(left, forward, right);
      assertCannotSimplify(right, forward, left);
   }

   private void assertCannotSimplify(Node first, Node second, Node third) {
      assertNull(simplify(first, second, third));
   }

   private Node simplify(Node first, Node second, Node third) {
      return TRISEQUENCE.simplify(ChildNodes.createChildNodes(first, second, third));
   }

   private void assertAllSame(Object first, Object... rest) {
      for (Object o : rest) {
         assertSame(first, o);
      }
   }

   private void assertAllEqual(Object first, Object... rest) {
      for (Object o : rest) {
         assertEquals(first, o);
      }
   }
}
