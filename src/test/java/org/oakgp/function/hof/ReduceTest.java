package org.oakgp.function.hof;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class ReduceTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Reduce();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(reduce + 0 [2 12 8])", 22);
      testCases.put("(reduce + 5 [2 12 8])", 27);

      testCases.put("(reduce * 1 [2 12 8])", 192);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(reduce + 9 [2 12 8])", "31");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
