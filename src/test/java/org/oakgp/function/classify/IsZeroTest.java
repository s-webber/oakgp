package org.oakgp.function.classify;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsZeroTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsZero();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(zero? 0)", true);
      testCases.put("(zero? 27)", false);
      testCases.put("(zero? 1)", false);
      testCases.put("(zero? -1)", false);
      testCases.put("(zero? -27)", false);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(zero? 0)", "true");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
