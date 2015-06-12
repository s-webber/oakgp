package org.oakgp.function.classify;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsNegativeTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsNegative();
   }

   @Override
   public void testEvaluate() {
      evaluate("(neg? -27)").to(true);
      evaluate("(neg? -1)").to(true);
      evaluate("(neg? 0)").to(false);
      evaluate("(neg? 1)").to(false);
      evaluate("(neg? 27)").to(false);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(neg? -1)", "true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
