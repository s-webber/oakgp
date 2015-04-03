package org.oakgp.function.hof;

import java.util.List;

import org.oakgp.function.AbstractOperatorTest;
import org.oakgp.function.Operator;

public class ReduceTest extends AbstractOperatorTest {
   @Override
   protected Operator getOperator() {
      return new Reduce();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      // TODO use symbol instead of class name
      testCases.put("(org.oakgp.function.hof.Reduce + 0 [2 12 8])", 22);
      testCases.put("(org.oakgp.function.hof.Reduce + 5 [2 12 8])", 27);

      testCases.put("(org.oakgp.function.hof.Reduce * 1 [2 12 8])", 192);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(org.oakgp.function.hof.Reduce + 9 [2 12 8])", "31");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
