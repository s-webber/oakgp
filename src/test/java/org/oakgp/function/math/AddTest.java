package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

import java.math.BigDecimal;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class AddTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getAdd();
   }

   @Override
   public void testEvaluate() {
      // int
      evaluate("(+ 3 21)").to(24);
      evaluate("(+ 2147483647 1)").to(-2147483648);

      // long
      evaluate("(+ 3L 21L)").to(24L);
      evaluate("(+ 2147483647L 1L)").to(2147483648L);
      evaluate("(+ 9223372036854775807L 1L)").to(-9223372036854775808L);

      // BigDecimal
      evaluate("(+ 3D 21D)").to(new BigDecimal("24"));
      evaluate("(+ 2147483647D 1D)").to(new BigDecimal("2147483648"));
      evaluate("(+ 9223372036854775807D 1D)").to(new BigDecimal("9223372036854775808"));
      evaluate("(+ 7.5D -0.025D)").to(new BigDecimal("7.475"));
      evaluate("(+ 1.7976931348623157E308D 1D)").to(BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.ONE));
   }

   @Override
   public void testCanSimplify() {
      Type[] integers = { integerType(), integerType() };
      Object[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

      // constants get simplified to the result of adding them together
      simplify("(+ 8 3)").with(integers).to("11").verifyAll(assignedValues);

      // arguments should be consistently ordered
      simplify("(+ 4 v0)").with(integers).to("(+ 4 v0)").verifyAll(assignedValues);
      simplify("(+ v0 4)").with(integers).to("(+ 4 v0)").verifyAll(assignedValues);
      simplify("(+ v0 v1)").with(integers).to("(+ v0 v1)").verifyAll(assignedValues);
      simplify("(+ v1 v0)").with(integers).to("(+ v0 v1)").verifyAll(assignedValues);

      // anything plus zero is itself
      simplify("(+ v1 0)").with(integers).to("v1").verifyAll(assignedValues);
      simplify("(+ 0 v1)").with(integers).to("v1").verifyAll(assignedValues);

      // anything plus itself is equal to itself multiplied by 2
      simplify("(+ v1 v1)").with(integers).to("(* 2 v1)").verifyAll(assignedValues);

      // convert addition of negative numbers to subtraction
      simplify("(+ v1 -7)").with(integers).to("(- v1 7)").verifyAll(assignedValues);
      simplify("(+ -7 v1)").with(integers).to("(- v1 7)").verifyAll(assignedValues);

      simplify("(+ v0 (- 7 3))").with(integers).to("(+ 4 v0)").verifyAll(assignedValues);

      simplify("(+ (+ v0 v1) 0)").with(integers).to("(+ v0 v1)").verifyAll(assignedValues);
      simplify("(+ 0 (+ v0 v1))").with(integers).to("(+ v0 v1)").verifyAll(assignedValues);

      simplify("(+ (+ 4 v0) (+ v0 (* 2 v1)))").with(integers).to("(+ 4 (+ (* 2 v1) (* 2 v0)))").verifyAll(assignedValues);
      simplify("(+ (- 10 v0) (+ 1 v0))").with(integers).to("11").verifyAll(assignedValues);
      simplify("(+ (+ 10 v0) (- 1 v0))").with(integers).to("11").verifyAll(assignedValues);
      simplify("(+ (- 10 v0) (+ 1 v1))").with(integers).to("(+ v1 (- 11 v0))").verifyAll(assignedValues);
      simplify("(+ (+ 10 v0) (- 1 v1))").with(integers).to("(+ v0 (- 11 v1))").verifyAll(assignedValues);
      simplify("(+ (- 10 v0) (- 1 v0))").with(integers).to("(- 11 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ (+ 10 v0) (+ 1 v0))").with(integers).to("(+ 11 (* 2 v0))").verifyAll(assignedValues);

      simplify("(+ 1 (+ 1 v0))").with(integers).to("(+ 2 v0)").verifyAll(assignedValues);
      simplify("(+ 1 (- 1 v0))").with(integers).to("(- 2 v0)").verifyAll(assignedValues);
      simplify("(+ 6 (+ 4 v0))").with(integers).to("(+ 10 v0)").verifyAll(assignedValues);
      simplify("(+ 6 (- 4 v0))").with(integers).to("(- 10 v0)").verifyAll(assignedValues);
      simplify("(+ (+ 1 v0) (+ 2 v0))").with(integers).to("(+ 3 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ (+ 3 v0) (+ 4 v0))").with(integers).to("(+ 7 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ (+ v0 3) (+ v0 4))").with(integers).to("(+ 7 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ (+ v0 3) (+ 4 v0))").with(integers).to("(+ 7 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ (+ 3 v0) (+ v0 4))").with(integers).to("(+ 7 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ v0 (* 2 v0))").with(integers).to("(* 3 v0)").verifyAll(assignedValues);
      simplify("(+ v0 (* 14 v0))").with(integers).to("(* 15 v0)").verifyAll(assignedValues);
      simplify("(+ 2 (+ (* 2 v0) 8))").with(integers).to("(+ 10 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ 2 (+ 8 (* 2 v0)))").with(integers).to("(+ 10 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ v0 (+ v0 8))").with(integers).to("(+ 8 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ v0 (+ 8 v0))").with(integers).to("(+ 8 (* 2 v0))").verifyAll(assignedValues);
      simplify("(+ v0 (- v0 2))").with(integers).to("(- (* 2 v0) 2)").verifyAll(assignedValues);
      simplify("(+ v0 (- 2 v0))").with(integers).to("2").verifyAll(assignedValues);

      simplify("(+ 5 (+ v0 5))").with(integers).to("(+ 10 v0)").verifyAll(assignedValues);
      simplify("(+ 5 (- v0 5))").with(integers).to("v0").verifyAll(assignedValues);
      simplify("(+ 5 (- 5 v0))").with(integers).to("(- 10 v0)").verifyAll(assignedValues);
      simplify("(+ 5 (- v0 2))").with(integers).to("(+ 3 v0)").verifyAll(assignedValues);

      simplify("(+ 9 (+ v1 (+ v0 8)))").with(integers).to("(+ v1 (+ 17 v0))").verifyAll(assignedValues);
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(+ 1 v0)", integerType());
      cannotSimplify("(+ v0 (* v0 v1))", integerType(), integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply(),
            BigDecimalUtils.BIG_DECIMAL_UTILS.getAdd(), BigDecimalUtils.BIG_DECIMAL_UTILS.getMultiply(), LongUtils.LONG_UTILS.getAdd(),
            LongUtils.LONG_UTILS.getMultiply() };
   }
}
