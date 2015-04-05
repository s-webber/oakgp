package org.oakgp.function.hof;

import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.Type.integerType;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class FilterTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Filter(integerType());
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(filter pos? [2 -12 8 -3 -7 6])", createArguments("2", "8", "6"));
      testCases.put("(filter neg? [2 -12 8 -3 -7 6])", createArguments("-12", "-3", "-7"));
      testCases.put("(filter zero? [2 -12 8 -3 -7 6])", createArguments());
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(filter pos? [2 -12 8])", "[2 8]");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}