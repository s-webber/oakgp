package org.oakgp.function.math;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class DivideTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getDivide();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(/ 12 1)", 12);
      testCases.put("(/ 12 12)", 1);
      testCases.put("(/ 12 0)", 1);
      testCases.put("(/ 12 3)", 4);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(/ 4 0)", "1");
      testCases.put("(/ 4 1)", "4");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      testCases.add("(/ 1 v0)");
   }
}
