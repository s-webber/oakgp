package org.oakgp.function.classify;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsPositiveTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsPositive();
   }

   @Override
   public void testEvaluate() {
      evaluate("(pos? 27)").to(true);
      evaluate("(pos? 1)").to(true);
      evaluate("(pos? 0)").to(false);
      evaluate("(pos? -1)").to(false);
      evaluate("(pos? -27)").to(false);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(pos? 1)", "true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
