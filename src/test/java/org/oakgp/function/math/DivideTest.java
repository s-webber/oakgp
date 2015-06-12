package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class DivideTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getDivide();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(/ 12 1)", 12);
      testCases.put("(/ 12 12)", 1);
      testCases.put("(/ 12 0)", 1);
      testCases.put("(/ 12 3)", 4);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(/ 4 0)", "1");
      testCases.put("(/ 4 1)", "4");
      testCases.put("(/ 4 -1)", "-4");
      testCases.put("(/ -4 -1)", "4");
      testCases.put("(/ v0 -1)", "(- 0 v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(/ 1 v0)", integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getSubtract() };
   }
}
