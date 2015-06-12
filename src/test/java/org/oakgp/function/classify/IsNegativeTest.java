package org.oakgp.function.classify;

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
   public void testCannotSimplify() {
   }
}
