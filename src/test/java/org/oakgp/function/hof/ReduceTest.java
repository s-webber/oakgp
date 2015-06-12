package org.oakgp.function.hof;

import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.math.IntegerUtils;

public class ReduceTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Reduce(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(reduce + 0 [2 12 8])").to(22);
      evaluate("(reduce + 5 [2 12 8])").to(27);
      evaluate("(reduce * 1 [2 12 8])").to(192);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(reduce + 9 [2 12 8])", "31");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getMultiply() };
   }
}
