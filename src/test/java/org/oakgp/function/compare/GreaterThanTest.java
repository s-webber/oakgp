package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.List;

import org.oakgp.function.AbstractOperatorTest;
import org.oakgp.function.Operator;

public class GreaterThanTest extends AbstractOperatorTest {
   @Override
   protected Operator getOperator() {
      return new GreaterThan();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases t) {
      t.put("(> 7 8)", FALSE);
      t.put("(> 8 8)", FALSE);
      t.put("(> 9 8)", TRUE);
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
}
