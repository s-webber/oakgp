package org.oakgp.function.classify;

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
   public void testCannotSimplify() {
   }
}
