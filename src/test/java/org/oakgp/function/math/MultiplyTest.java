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

import static org.oakgp.type.CommonTypes.integerType;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class MultiplyTest extends AbstractFunctionTest {
   @Override
   protected Multiply<Integer> getFunction() {
      return IntegerUtils.INTEGER_UTILS.getMultiply();
   }

   @Override
   public void testEvaluate() {
      // integer
      evaluate("(* 3 21)").to(63);

      // long
      evaluate("(* 3L 21L)").to(63L);

      // big integer
      evaluate("(* 3I 21I)").to(BigInteger.valueOf(63));

      // double
      evaluate("(* 3.0 21.0)").to(63d);

      // big decimal
      evaluate("(* 3D 21D)").to(BigDecimal.valueOf(63));
   }

   @Override
   public void testCanSimplify() {
      // constants get simplified to the result of multiplying them
      simplify("(* 8 3)").to("24");

      // arguments should be consistently ordered
      simplify("(* 2 v0)").to("(* 2 v0)");
      simplify("(* v0 2)").to("(* 2 v0)");
      simplify("(* v0 v1)").to("(* v0 v1)");
      simplify("(* v1 v0)").to("(* v0 v1)");

      // anything multiplied by zero is zero
      simplify("(* v1 0)").to("0");
      simplify("(* 0 v1)").to("0");

      // anything multiplied by one is itself
      simplify("(* v1 1)").to("v1");
      simplify("(* 1 v1)").to("v1");

      // (* 3 (- 0 (+ 1 v0))) = 3*(0-(1+x)) = -3-3x
      simplify("(* 3 (- 0 (+ 1 v0)))").to("(- (* -3 v0) 3)");

      simplify("(* 3 (+ 9 v0))").to("(+ 27 (* 3 v0))");
      simplify("(* 3 (* 9 v0))").to("(* 27 v0)");
      simplify("(* 3 (- 9 v0))").to("(- 27 (* 3 v0))");

      // TODO simplify (+ 3 (* v2 (* (* 81 v2) (* v2 (* 9 v2)))))
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(* 2 v0)", integerType());
      cannotSimplify("(* -1 v0)", integerType());
      cannotSimplify("(* v0 v1)", integerType(), integerType());
      cannotSimplify("(* 3 (/ 9 v0))", integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(),
            IntegerUtils.INTEGER_UTILS.getDivide(), LongUtils.LONG_UTILS.getMultiply(), DoubleUtils.DOUBLE_UTILS.getMultiply(),
            BigIntegerUtils.BIG_INTEGER_UTILS.getMultiply(), BigDecimalUtils.BIG_DECIMAL_UTILS.getMultiply() };
   }
}
