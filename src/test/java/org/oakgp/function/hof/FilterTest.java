package org.oakgp.function.hof;

import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;

public class FilterTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Filter(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(filter pos? [2 -12 8 -3 -7 6])").to(createArguments("2", "8", "6"));
      evaluate("(filter neg? [2 -12 8 -3 -7 6])").to(createArguments("-12", "-3", "-7"));
      evaluate("(filter zero? [2 -12 8 -3 -7 6])").to(createArguments());
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(filter pos? [2 -12 8])", "[2 8]");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new IsPositive(), new IsNegative(), new IsZero() };
   }
}
