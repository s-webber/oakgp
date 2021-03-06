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

import static org.oakgp.Type.integerType;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class SubtractTest extends AbstractFunctionTest {
   @Override
   protected Subtract getFunction() {
      return IntegerUtils.INTEGER_UTILS.getSubtract();
   }

   @Override
   public void testEvaluate() {
      // integer
      evaluate("(- 3 21)").to(-18);

      // long
      evaluate("(- 3L 21L)").to(-18L);

      // big integer
      evaluate("(- 3I 21I)").to(BigInteger.valueOf(-18));

      // double
      evaluate("(- 3.0 21.0)").to(-18d);

      // big decimal
      evaluate("(- 3D 21D)").to(BigDecimal.valueOf(-18));
   }

   @Override
   public void testCanSimplify() {
      Object[][] assignedValues = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

      // constants get simplified to the result of subtracting the second from the first
      simplify("(- 8 3)").to("5").verifyAll(assignedValues);

      // anything minus zero is itself
      simplify("(- v0 0)").to("v0").verifyAll(assignedValues);

      // anything minus itself is zero
      simplify("(- v0 v0)").to("0").verifyAll(assignedValues);

      // simplify "zero minus ?" expressions
      simplify("(- 0 (- v0 v1))").to("(- v1 v0)").verifyAll(assignedValues);

      // convert double negatives to addition
      simplify("(- v0 -7)").to("(+ 7 v0)").verifyAll(assignedValues);

      // test when second argument is an addition expression
      simplify("(- 1 (+ 1 v0))").to("(- 0 v0)").verifyAll(assignedValues);
      simplify("(- 6 (+ 4 v0))").to("(- 2 v0)").verifyAll(assignedValues);

      // test when second argument is a subtraction expression
      simplify("(- 1 (- 7 v0))").to("(- v0 6)").verifyAll(assignedValues);
      simplify("(- 1 (- 1 v0))").to("v0").verifyAll(assignedValues);
      simplify("(- 6 (- 4 v0))").to("(+ 2 v0)").verifyAll(assignedValues);
      simplify("(- 1 (- 0 v0))").to("(+ 1 v0)").verifyAll(assignedValues);

      // test when second argument is a multiplication expression
      simplify("(- 0 (* -3 v0))").to("(* 3 v0)").verifyAll(assignedValues);
      simplify("(- 7 (* -3 v0))").to("(+ 7 (* 3 v0))").verifyAll(assignedValues);

      // (1 + x) - (2 + y) evaluates to -1+x-y
      simplify("(- (+ 1 v0) (+ 2 v1))").to("(- v0 (+ 1 v1))").verifyAll(assignedValues);
      simplify("(- (+ 1 v0) (+ 2 v0))").to("-1").verifyAll(assignedValues);

      // (1 + x) - (12 - y) evaluates to -11+x+y
      simplify("(- (+ 1 v0) (- 12 v1))").to("(- v0 (- 11 v1))").verifyAll(assignedValues);
      simplify("(- (+ 1 v0) (- 12 v0))").to("(- (* 2 v0) 11)").verifyAll(assignedValues);

      simplify("(- (+ 1 v0) v0)").to("1").verifyAll(assignedValues);

      simplify("(- (- (- (* 2 v0) 9) v1) v1)").to("(- (- (* 2 v0) 9) (* 2 v1))").verifyAll(assignedValues);
      simplify("(- (- (+ (* 2 v0) 9) v1) v1)").to("(- (+ 9 (* 2 v0)) (* 2 v1))").verifyAll(assignedValues);

      simplify("(- (- (- (* 2 v0) 9) v1) v1)").to("(- (- (* 2 v0) 9) (* 2 v1))").verifyAll(assignedValues);

      simplify("(- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v0 2)))").to("(- 4 (+ v0 (* 2 v1)))").verifyAll(assignedValues);
      simplify("(- (* 2 v0) (- v1 v0))").to("(- (* 3 v0) v1)").verifyAll(assignedValues);
      simplify("(- (* -2 v0) (- v1 v0))").to("(- (* -1 v0) v1)").verifyAll(assignedValues);

      // (- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1)) =
      // (5-((((2*y)+(2*x))-1)-(x-2)))-(2 * x) =
      // -3x+4-2y
      simplify("(- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1))").to("(- (- 4 (* 2 v0)) (* 3 v1))").verifyAll(assignedValues);

      simplify("(- (+ 9 (- v0 (+ 9 v1))) (- 8 v1))").to("(- v0 8)").verifyAll(assignedValues);
      simplify("(- (+ 9 (- v0 (+ 9 v1))) (- v1 v0))").to("(- (* 2 v0) (* 2 v1))").verifyAll(assignedValues);

      // (- v0 (- v1 (+ (* 2 v0) (* -2 v1)))) = x-(y-((2*x)+(-2*y))) = 3x-3y = (+ (* 3 v0) (* -3 v1))
      simplify("(- v0 (- v1 (+ (* 2 v0) (* -2 v1))))").to("(+ (* 3 v0) (* -3 v1))").verifyAll(assignedValues);

      simplify("(- v1 (- (* 2 v0) v1))").to("(- (* 2 v1) (* 2 v0))").verifyAll(assignedValues);
      simplify("(- v1 (- 4 (+ v1 (* 2 v0))))").to("(- (+ (* 2 v1) (* 2 v0)) 4)").verifyAll(assignedValues);

      simplify("(- (+ 9 (- v0 (+ 9 v1))) (- 8 v1))").to("(- v0 8)").verifyAll(assignedValues);
      simplify("(- (+ 9 (- v0 (+ 9 v1))) (- v1 v0))").to("(- (* 2 v0) (* 2 v1))").verifyAll(assignedValues);

      simplify("(- (- v0 v1) (- v1 v0))").to("(- (* 2 v0) (* 2 v1))").verifyAll(assignedValues);

      simplify("(- 0 (* 2 v0))").to("(* -2 v0)").verifyAll(assignedValues);

      simplify("(- 0 (* -162 v0))").to("(* 162 v0)").verifyAll(assignedValues);

      // TODO "(- (- 0 v2) (- -3 v0))" is simplified to "(- (- 3 v2) (- 0 v0))" and "(- (- 3 v2) (- 0 v0))" is simplified to "(- (- 0 v2) (- -3 v0))"
      // - resolve this behaviour so results are consistent and stable
      // simplify("(- (- 0 v2) (- -3 v0))").to("(- (- 3 v2) (- 0 v0))");
      // simplify("(- (- 3 v2) (- 0 v0))").to("(- (- 0 v2) (- -3 v0))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(- v0 1)", integerType());
      cannotSimplify("(- 0 v0)", integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getMultiply(), LongUtils.LONG_UTILS.getSubtract(),
            DoubleUtils.DOUBLE_UTILS.getSubtract(), BigIntegerUtils.BIG_INTEGER_UTILS.getSubtract(), BigDecimalUtils.BIG_DECIMAL_UTILS.getSubtract() };
   }
}
