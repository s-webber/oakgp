package org.oakgp.function.math;

import java.util.List;

import org.oakgp.function.AbstractOperatorTest;
import org.oakgp.function.Operator;

public class MultiplyTest extends AbstractOperatorTest {
   @Override
   protected Operator getOperator() {
      return new Multiply();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases t) {
      t.put("(* 3 21)", 63);
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
   protected void getCannotSimplifyTests(List<String> t) {
      t.add("(* 2 v1)");
      t.add("(* -1 v1)");
      t.add("(* v1 v2)");
   }
}
