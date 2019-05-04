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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.integerType;
import static org.oakgp.function.math.IntegerUtils.INTEGER_UTILS;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class FunctionNodeTest {
   @Test
   public void testConstructors() {
      Function function = INTEGER_UTILS.getMultiply();
      ConstantNode arg1 = integerConstant(42);
      VariableNode arg2 = createVariable(0);

      // construct using Node array
      FunctionNode n1 = new FunctionNode(function, arg1, arg2);

      // Construct using Arguments
      ChildNodes arguments = ChildNodes.createChildNodes(arg1, arg2);
      FunctionNode n2 = new FunctionNode(function, arguments);

      // assert the result is the same
      assertEquals(n1, n2);
   }

   @Test
   public void testEvaluate() {
      Function function = INTEGER_UTILS.getMultiply();
      ChildNodes arguments = ChildNodes.createChildNodes(integerConstant(42), createVariable(0));
      FunctionNode functionNode = new FunctionNode(function, arguments);

      assertSame(function, functionNode.getFunction());
      assertSame(arguments, functionNode.getChildren());

      Assignments assignments = createAssignments(3);
      assertEquals(new Integer(126), functionNode.evaluate(assignments));
   }

   @Test
   public void testCountAndHeight() {
      assertCountAndHeight("(* 7 7)", 3, 2);
      assertCountAndHeight("(* (+ 8 9) 7)", 5, 3);
      assertCountAndHeight("(* 7 (+ 8 9))", 5, 3);
      assertCountAndHeight("(zero? (+ (* 4 5) (- 6 (+ 7 8))))", 10, 5);
      assertCountAndHeight("(zero? (+ (- 6 (+ 7 8)) (* 4 5)))", 10, 5);
      assertCountAndHeight("(if (zero? v0) v1 v2)", 5, 3);
      assertCountAndHeight("(if (zero? v0) v1 (+ v0 (* v1 v2)))", 9, 4);
      assertCountAndHeight("(if (zero? v0) (+ v0 (* v1 v2)) v1)", 9, 4);
   }

   private void assertCountAndHeight(String expression, int nodeCount, int height) {
      Node n = readNode(expression);
      assertEquals(nodeCount, n.getNodeCount());
      assertEquals(height, n.getHeight());
   }

   @Test
   public void testGetType() {
      FunctionNode n = createFunctionNode();
      assertSame(integerType(), n.getType());
   }

   @Test
   public void testEqualsAndHashCode1() {
      final FunctionNode n1 = createFunctionNode();
      final FunctionNode n2 = createFunctionNode();
      assertNotSame(n1, n2); // just to sanity check createFunctionNode() doesn't return cached versions
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
      assertEquals(n2, n1);
   }

   @Test
   public void testEqualsAndHashCode2() {
      Node n1 = readNode("(* 288 v1)");
      Node n2 = readNode("(* 288 v1)");
      assertNotSame(n1, n2); // just to sanity check readNode doesn't return cached versions
      assertEquals(n1, n1);
      assertEquals(n1, n2);
      assertEquals(n2, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
   }

   @Test
   public void testNotEquals() {
      Function add = INTEGER_UTILS.getAdd();

      final FunctionNode n = new FunctionNode(add, createVariable(0), integerConstant(7));

      // verify (sanity-check) that equals will return true when it should
      assertEquals(n, new FunctionNode(add, createVariable(0), integerConstant(7)));

      // test different function
      Function multiply = INTEGER_UTILS.getMultiply();
      assertNotEquals(n, new FunctionNode(multiply, createVariable(0), integerConstant(7)));

      // test different first argument
      assertNotEquals(n, new FunctionNode(add, createVariable(1), integerConstant(7)));

      // test different second argument
      assertNotEquals(n, new FunctionNode(add, createVariable(0), integerConstant(6)));

      // test same arguments but different order
      assertNotEquals(n, new FunctionNode(add, integerConstant(7), createVariable(0)));

      // test wrong arguments but different order
      assertNotEquals(n, new FunctionNode(add, integerConstant(0), createVariable(7)));

      // test extra argument
      assertNotEquals(n, new FunctionNode(add, createVariable(0), integerConstant(7), integerConstant(7)));

      // test one less argument
      assertNotEquals(n, new FunctionNode(add, createVariable(0)));

      // test no arguments
      assertNotEquals(n, new FunctionNode(add));

      // test not equal to other Node implementations
      assertNotEquals(n, integerConstant(7));

      // test not equal to other non-Node instances
      assertNotEquals(n, new Object());

      assertFalse(n.equals(null));
   }

   /**
    * Tests that for two {@code FunctionNode} instances to be considered equal they must share the same instance of {@code Function} (i.e. it is not enough for
    * them to have separate instances of the same {@code Function} class).
    */
   @Test
   public void testEqualityRequiresSameFunctionInstance() {
      class DummyFunction implements Function {
         @Override
         public Object evaluate(Arguments arguments) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }
      }

      Function f1 = new DummyFunction();
      Function f2 = new DummyFunction();
      ChildNodes arguments = ChildNodes.createChildNodes(integerConstant(1));
      FunctionNode fn1 = new FunctionNode(f1, arguments);
      FunctionNode fn2 = new FunctionNode(f2, arguments);

      assertSame(f1.getClass(), f2.getClass());
      assertNotEquals(fn1, fn2);
   }

   @Test
   public void testHashCode() {
      // In Java the following results in two lists that have the same hashCode (even though they are different);
      // List a = new ArrayList();
      // a.add(Arrays.asList(9, 1));
      // a.add(Arrays.asList(2, 9));
      //
      // List b = new ArrayList();
      // b.add(Arrays.asList(9, 2));
      // b.add(Arrays.asList(1, 9));
      //
      // assertEquals(a.hashCode(), b.hashCode());

      // This is also true of Clojure's PersistentVector:
      // user=> (def x [[9 1] [2 9]])
      // #'user/x
      // user=> (def y [[9 2] [1 9]])
      // #'user/y
      // user=> (.hashCode x)
      // 40464
      // user=> (.hashCode y)
      // 40464

      // test that result of reading the following expressions is nodes with different hash codes:
      Node n1 = readNode("(- (- (* -1 v3) 0) (- 13 v1))");
      Node n2 = readNode("(- (- (* -1 v3) 13) (- 0 v1))");
      assertNotEquals(n1.hashCode(), n2.hashCode());
   }

   @Test
   public void testLargeNumberOfArguments() {
      Node[] args = new Node[1000];
      for (int i = 0; i < args.length; i++) {
         args[i] = integerConstant(i);
      }

      FunctionNode n = new FunctionNode(mock(Function.class), args);

      assertEquals(args.length, n.getChildren().size());
      for (int i = 0; i < args.length; i++) {
         assertSame(args[i], n.getChildren().getNode(i));
      }
   }

   /** Returns representation of: {@code (x*y)+z+1} */
   private FunctionNode createFunctionNode() {
      return new FunctionNode(INTEGER_UTILS.getAdd(), new FunctionNode(INTEGER_UTILS.getMultiply(), createVariable(0), createVariable(1)),
            new FunctionNode(INTEGER_UTILS.getAdd(), createVariable(2), integerConstant(1)));
   }
}
