package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class GreaterThanTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new GreaterThan(integerType());
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases t) {
      t.put("(> 7 8)", FALSE);
      t.put("(> 8 8)", FALSE);
      t.put("(> 9 8)", TRUE);

      // TODO have similar String comparison tests in other comparison function tests
      t.put("(> \"dog\" \"zebra\")", FALSE);
      t.put("(> \"dog\" \"dog\")", FALSE);
      t.put("(> \"dog\" \"apple\")", TRUE);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases t) {
      t.put("(> v1 v1)", "false");
      t.put("(> 8 7)", "true");
      t.put("(> 8 8)", "false");
      t.put("(> 8 9)", "false");
      t.put("(> v1 8)", "(< 8 v1)");
      t.put("(> 8 v1)", "(< v1 8)");
      t.put("(> v0 v1)", "(< v1 v0)");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> t) {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new GreaterThan(stringType()) };
   }
}
