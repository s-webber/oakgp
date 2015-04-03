package org.oakgp.function.classify;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsPositiveTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsPositive();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(pos? 27)", true);
      testCases.put("(pos? 1)", true);
      testCases.put("(pos? 0)", false);
      testCases.put("(pos? -1)", false);
      testCases.put("(pos? -27)", false);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(pos? 1)", "true");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
