package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class NotEqualTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new NotEqual();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases t) {
      t.put("(!= 7 8)", TRUE);
      t.put("(!= 8 8)", FALSE);
      t.put("(!= 9 8)", TRUE);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases t) {
      t.put("(!= v1 v1)", "false");
      t.put("(!= 8 7)", "true");
      t.put("(!= 8 8)", "false");
      t.put("(!= 8 9)", "true");
      t.put("(!= v1 8)", "(!= 8 v1)");
      t.put("(!= v1 v0)", "(!= v0 v1)");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> t) {
      t.add("(!= 8 v1)");
      t.add("(!= v0 v1)");
   }
}
