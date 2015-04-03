package org.oakgp.function.math;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class SubtractTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Subtract();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(- 3 21)", -18);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      Object[][] assignedValues = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

      // constants get simplified to the result of subtracting the second from the first
      testCases.put("(- 8 3)", "5", assignedValues);

      // anything minus zero is itself
      testCases.put("(- v0 0)", "v0", assignedValues);

      // anything minus itself is zero
      testCases.put("(- v0 v0)", "0", assignedValues);

      // simplify "zero minus ?" expressions
      testCases.put("(- 0 (- v0 v1))", "(- v1 v0)", assignedValues);

      // convert double negatives to addition
      testCases.put("(- v0 -7)", "(+ 7 v0)", assignedValues);

      testCases.put("(- 1 (+ 1 v0))", "(- 0 v0)", assignedValues);
      testCases.put("(- 1 (- 1 v0))", "v0", assignedValues);
      testCases.put("(- 6 (+ 4 v0))", "(- 2 v0)", assignedValues);
      testCases.put("(- 6 (- 4 v0))", "(+ 2 v0)", assignedValues);

      // (1 + x) - (2 + y) evaluates to -1+x-y
      testCases.put("(- (+ 1 v0) (+ 2 v1))", "(- v0 (+ 1 v1))", assignedValues);
      testCases.put("(- (+ 1 v0) (+ 2 v0))", "-1", assignedValues);

      // (1 + x) - (12 - y) evaluates to -11+x+y
      testCases.put("(- (+ 1 v0) (- 12 v1))", "(- v0 (- 11 v1))", assignedValues);
      testCases.put("(- (+ 1 v0) (- 12 v0))", "(- (* 2 v0) 11)", assignedValues);

      testCases.put("(- (+ 1 v0) v0)", "1", assignedValues);

      testCases.put("(- (- (- (* 2 v0) 9) v1) v1)", "(- (- (* 2 v0) 9) (* 2 v1))", assignedValues);
      testCases.put("(- (- (+ (* 2 v0) 9) v1) v1)", "(- (+ 9 (* 2 v0)) (* 2 v1))", assignedValues);

      testCases.put("(- (- (- (* 2 v0) 9) v1) v1)", "(- (- (* 2 v0) 9) (* 2 v1))", assignedValues);

      testCases.put("(- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v0 2)))", "(- 4 (+ v0 (* 2 v1)))", assignedValues);
      testCases.put("(- (* 2 v0) (- v1 v0))", "(- (* 3 v0) v1)", assignedValues);
      testCases.put("(- (* -2 v0) (- v1 v0))", "(- (* -1 v0) v1)", assignedValues);

      // (- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1)) =
      // (5-((((2*y)+(2*x))-1)-(x-2)))-(2 * x) =
      // -3x+4-2y
      testCases.put("(- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1))", "(- (- 4 (* 2 v0)) (* 3 v1))", assignedValues);

      testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- 8 v1))", "(- v0 8)", assignedValues);
      testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- v1 v0))", "(- (* 2 v0) (* 2 v1))", assignedValues);

      // (- v0 (- v1 (+ (* 2 v0) (* -2 v1)))) = x-(y-((2*x)+(-2*y))) = 3x-3y = (+ (* 3 v0) (* -3 v1))
      testCases.put("(- v0 (- v1 (+ (* 2 v0) (* -2 v1))))", "(+ (* -3 v1) (* 3 v0))", assignedValues);

      testCases.put("(- v1 (- (* 2 v0) v1))", "(- (* 2 v1) (* 2 v0))", assignedValues);
      testCases.put("(- v1 (- 4 (+ v1 (* 2 v0))))", "(- (+ (* 2 v0) (* 2 v1)) 4)", assignedValues);

      testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- 8 v1))", "(- v0 8)", assignedValues);
      testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- v1 v0))", "(- (* 2 v0) (* 2 v1))", assignedValues);

      testCases.put("(- (- v0 v1) (- v1 v0))", "(- (* 2 v0) (* 2 v1))", assignedValues);

      testCases.put("(- 0 (* 2 v0))", "(* -2 v0)", assignedValues);

      testCases.put("(- 0 (* -162 v0))", "(* 162 v0)", assignedValues);
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      testCases.add("(- v0 1)");
      testCases.add("(- 0 v0)");
   }
}
