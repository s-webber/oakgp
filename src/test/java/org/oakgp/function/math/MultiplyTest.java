package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

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
   protected void getCanSimplifyTests(SimplifyTestCases t) {
      // constants get simplified to the result of multiplying them
      t.put("(* 8 3)", "24");

      // arguments should be consistently ordered
      t.put("(* 2 v1)", "(* 2 v1)");
      t.put("(* v1 2)", "(* 2 v1)");
      t.put("(* v0 v1)", "(* v0 v1)");
      t.put("(* v1 v0)", "(* v0 v1)");

      // anything multiplied by zero is zero
      t.put("(* v1 0)", "0");
      t.put("(* 0 v1)", "0");

      // anything multiplied by one is itself
      t.put("(* v1 1)", "v1");
      t.put("(* 1 v1)", "v1");

      // (* 3 (- 0 (+ 1 v2))) = 3*(0-(1+x)) = -3-3x
      t.put("(* 3 (- 0 (+ 1 v2)))", "(- (* -3 v2) 3)");

      t.put("(* 3 (+ 9 v0))", "(+ 27 (* 3 v0))");
      t.put("(* 3 (* 9 v0))", "(* 27 v0)");
      t.put("(* 3 (- 9 v0))", "(- 27 (* 3 v0))");
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
