package org.oakgp.function.classify;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsNegativeTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsNegative();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(neg? -27)", true);
      testCases.put("(neg? -1)", true);
      testCases.put("(neg? 0)", false);
      testCases.put("(neg? 1)", false);
      testCases.put("(neg? 27)", false);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(neg? -1)", "true");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
