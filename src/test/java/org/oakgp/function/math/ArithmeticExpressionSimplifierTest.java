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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.readFunctionNode;
import static org.oakgp.TestUtils.readNode;

import java.util.Optional;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class ArithmeticExpressionSimplifierTest {
   private static final ArithmeticExpressionSimplifier SIMPLIFIER = IntegerUtils.INTEGER_UTILS.getSimplifier();

   @Test
   public void testCombineWithChildNodes() {
      // constants
      assertCombineWithChildNodes("3", "7", true, "10");
      assertCombineWithChildNodes("3", "7", false, "-4");

      // adding constant to a function
      assertCombineWithChildNodes("(+ 1 v0)", "7", true, "(+ 8 v0)");
      assertCombineWithChildNodes("(+ 1 v0)", "7", false, "(+ -6 v0)");
      assertCombineWithChildNodes("(+ 1 (- (- v0 9) 8))", "7", true, "(+ 8 (- (- v0 9) 8))");
      assertCombineWithChildNodes("(- 1 v0)", "7", true, "(- 8 v0)");
      assertCombineWithChildNodes("(- 1 v0)", "7", false, "(- -6 v0)");
      assertCombineWithChildNodes("(- 1 (- (- v0 9) 8))", "7", true, "(- 8 (- (- v0 9) 8))");

      // adding variable to function
      assertCombineWithChildNodes("(+ 1 (- v0 9))", "v0", true, "(+ 1 (- (* 2 v0) 9))");
      assertCombineWithChildNodes("(+ 1 (- v0 9))", "v0", false, "(+ 1 (- 0 9))");

      // multiplication of variable
      assertCombineWithChildNodes("(* 3 v0)", "v0", true, "(* 4 v0)");
      assertCombineWithChildNodes("(* 3 v0)", "v0", false, "(* 2 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "v0", true, "(* -2 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "v0", false, "(* -4 v0)");

      // combination of multiplication of the same variable
      assertCombineWithChildNodes("(* 3 v0)", "(* 7 v0)", true, "(* 10 v0)");
      assertCombineWithChildNodes("(* 3 v0)", "(* -7 v0)", true, "(* -4 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "(* 7 v0)", true, "(* 4 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "(* -7 v0)", true, "(* -10 v0)");
      assertCombineWithChildNodes("(* 3 v0)", "(* 7 v0)", false, "(* -4 v0)");
      assertCombineWithChildNodes("(* 3 v0)", "(* -7 v0)", false, "(* 10 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "(* 7 v0)", false, "(* -10 v0)");
      assertCombineWithChildNodes("(* -3 v0)", "(* -7 v0)", false, "(* 4 v0)");

      // adding to a sub-node of a function
      assertCombineWithChildNodes("(+ 1 (- v0 9))", "v0", true, "(+ 1 (- (* 2 v0) 9))");
      assertCombineWithChildNodes("(+ 1 (- v0 9))", "v0", false, "(+ 1 (- 0 9))");
      assertCombineWithChildNodes("(+ 1 (* 2 v0))", "v0", true, "(+ 1 (* 3 v0))");
      assertCombineWithChildNodes("(+ 1 (* 2 v0))", "v0", false, "(+ 1 (* 1 v0))");
      assertCombineWithChildNodes("(+ 1 (* 2 v0))", "(* 3 v0)", true, "(+ 1 (* 5 v0))");
      assertCombineWithChildNodes("(+ 1 (* 2 v0))", "(* 3 v0)", false, "(+ 1 (* -1 v0))");
      assertCombineWithChildNodes("(+ 1 (- v0 9))", "(- v0 9)", true, "(+ 1 (* 2 (- v0 9)))");
      assertCombineWithChildNodes("(+ 1 (- 8 (- v0 9)))", "(- v0 9)", true, "(+ 1 (- 8 0))");
      assertCombineWithChildNodes("(+ 1 (- (- v0 9) 8))", "(- v0 9)", true, "(+ 1 (- (* 2 (- v0 9)) 8))");

      assertCannotCombineWithChildNodes("(- v0 9)", "(+ 1 (- v0 9))");
      assertCannotCombineWithChildNodes("(* 3 v0)", "v1");
      assertCannotCombineWithChildNodes("(* v0 v1)", "7");
   }

   private void assertCombineWithChildNodes(String first, String second, boolean isPos, String expected) {
      Node result = SIMPLIFIER.combineWithChildNodes(readNode(first), readNode(second), isPos);
      assertNodeEquals(expected, result);
   }

   private void assertCannotCombineWithChildNodes(String first, String second) {
      assertNull(SIMPLIFIER.combineWithChildNodes(readNode(first), readNode(second), true));
      assertNull(SIMPLIFIER.combineWithChildNodes(readNode(first), readNode(second), false));
   }

   @Test
   public void testSimplify() {
      assertSimplify("(+ 1 1)", "(+ 1 1)");
      assertSimplify("(- 1 1)", "(- 1 1)");

      assertAdditionSimplification("v0", "(+ 1 v0)", "(+ 1 (* 2 v0))");

      assertAdditionSimplification("v0", "(+ v1 (+ v1 (+ v0 9)))", "(+ v1 (+ v1 (+ (* 2 v0) 9)))");

      assertAdditionSimplification("v1", "(+ v1 (+ v1 (+ v0 9)))", "(+ (* 2 v1) (+ v1 (+ v0 9)))");

      assertAdditionSimplification("v0", "(* 1 v0)", "(* 2 v0)");

      assertSimplify("(- 1 1)", "(- 1 1)");

      assertSimplify("(+ v0 (- 1 v0))", "(- 1 0)");

      assertSimplify("(- v0 (- v1 (- v0 9)))", "(- 0 (- v1 (- (* 2 v0) 9)))");
      assertSimplify("(- v0 (- v1 (- v1 (- v0 9))))", "(- 0 (- v1 (- v1 (- 0 9))))");

      assertAdditionSimplification("9", "(+ v0 3)", "(+ v0 12)");

      assertAdditionSimplification("9", "(- v0 3)", "(- v0 -6)");

      assertSimplify("(- 4 (- v1 (- v0 9)))", "(- 0 (- v1 (- v0 5)))");
      assertSimplify("(- 4 (- v1 (+ v0 9)))", "(- 0 (- v1 (+ v0 13)))");

      assertSimplify("(- (+ 4 v0) 3)", "(+ 1 v0)");
      assertSimplify("(- (- v0 1) v1)", "(- (- v0 1) v1)");

      assertSimplify("(- (- v0 1) (- v0 1))", "(- (- 0 0) (- 1 1))");
      assertSimplify("(- (+ v0 1) (+ v0 1))", "(- (+ 0 0) (+ -1 1))");
      assertSimplify("(+ (- v0 1) (- v0 1))", "(+ (- 0 0) (- (* 2 v0) 2))");
      assertSimplify("(+ (+ v0 1) (+ v0 1))", "(+ (+ 0 0) (+ (* 2 v0) 2))");
      assertSimplify("(- (+ v0 1) (- v0 1))", "(- (+ 0 0) (- -1 1))");
   }

   private void assertAdditionSimplification(String firstArg, String secondArg, String expectedOutput) {
      assertSimplify("(+ " + firstArg + " " + secondArg + ")", expectedOutput);
   }

   private void assertSimplify(String input, String expectedOutput) {
      FunctionNode in = readFunctionNode(input);
      Node simplifiedVersion = simplify(in, in.getChildren()).orElse(in);
      assertNodeEquals(expectedOutput, simplifiedVersion);
      if (!simplifiedVersion.equals(in)) {
         int[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };
         for (int[] assignedValue : assignedValues) {
            Assignments assignments = Assignments.createAssignments(assignedValue[0], assignedValue[1]);
            if (!in.evaluate(assignments, null).equals(simplifiedVersion.evaluate(assignments, null))) {
               throw new RuntimeException(expectedOutput);
            }
         }
      }
   }

   private Optional<Node> simplify(FunctionNode in, ChildNodes args) {
      return Optional.ofNullable(SIMPLIFIER.simplify(in.getFunction(), args.first(), args.second()));
   }

   @Test
   public void testEvaluateToSameResultSuccess() {
      Node a = readNode("(* 7 (+ 1 2))");
      Node b = readNode("(+ 9 12)");
      ArithmeticExpressionSimplifier.assertEvaluateToSameResult(a, b);
   }

   @Test
   public void testEvaluateToSameResultFailure() {
      Node a = readNode("(* 7 (- 1 2))");
      Node b = readNode("(+ 9 12)");
      try {
         ArithmeticExpressionSimplifier.assertEvaluateToSameResult(a, b);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("(* 7 (- 1 2)) = -7 (+ 9 12) = 21", e.getMessage());
      }
   }
}
