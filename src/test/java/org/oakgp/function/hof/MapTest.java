package org.oakgp.function.hof;

import static org.oakgp.TestUtils.createArguments;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class MapTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new org.oakgp.function.hof.Map();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(map pos? [2 -12 8 -3 -7 6])", createArguments("true", "false", "true", "false", "false", "true"));
      testCases.put("(map neg? [2 -12 8 -3 -7 6])", createArguments("false", "true", "false", "true", "true", "false"));
      testCases.put("(map zero? [2 -12 8 -3 -7 6])", createArguments("false", "false", "false", "false", "false", "false"));
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(map pos? [2 -12 8])", "[true false true]");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
