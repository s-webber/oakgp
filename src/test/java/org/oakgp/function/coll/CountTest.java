package org.oakgp.function.coll;

import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class CountTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Count(integerType());
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      // testCases.put("(count [])", 0); TODO
      testCases.put("(count [2 -12 8])", 3);
      testCases.put("(count [2 -12 8 -3 -7])", 5);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(count [2 -12 8])", "3");
   }

   @Override
   public void testCannotSimplify() {
   }
}
