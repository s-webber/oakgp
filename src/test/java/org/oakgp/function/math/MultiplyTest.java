package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class MultiplyTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getMultiply();
   }

   @Override
   public void testEvaluate() {
      evaluate("(* 3 21)").to(63);
   }

   @Override
   public void testCanSimplify() {
      Type[] integers = { integerType(), integerType() };

      // constants get simplified to the result of multiplying them
      simplify("(* 8 3)").with(integers).to("24");

      // arguments should be consistently ordered
      simplify("(* 2 v0)").with(integers).to("(* 2 v0)");
      simplify("(* v0 2)").with(integers).to("(* 2 v0)");
      simplify("(* v0 v1)").with(integers).to("(* v0 v1)");
      simplify("(* v1 v0)").with(integers).to("(* v0 v1)");

      // anything multiplied by zero is zero
      simplify("(* v1 0)").with(integers).to("0");
      simplify("(* 0 v1)").with(integers).to("0");

      // anything multiplied by one is itself
      simplify("(* v1 1)").with(integers).to("v1");
      simplify("(* 1 v1)").with(integers).to("v1");

      // (* 3 (- 0 (+ 1 v2))) = 3*(0-(1+x)) = -3-3x
      simplify("(* 3 (- 0 (+ 1 v0)))").with(integers).to("(- (* -3 v0) 3)");

      simplify("(* 3 (+ 9 v0))").with(integers).to("(+ 27 (* 3 v0))");
      simplify("(* 3 (* 9 v0))").with(integers).to("(* 27 v0)");
      simplify("(* 3 (- 9 v0))").with(integers).to("(- 27 (* 3 v0))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(* 2 v0)", integerType());
      cannotSimplify("(* -1 v0)", integerType());
      cannotSimplify("(* v0 v1)", integerType(), integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract() };
   }
}
