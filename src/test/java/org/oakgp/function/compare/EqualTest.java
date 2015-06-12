package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class EqualTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Equal(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(= 7 8)").to(FALSE);
      evaluate("(= 8 8)").to(TRUE);
      evaluate("(= 9 8)").to(FALSE);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases t) {
      t.put("(= v1 v1)", "true");
      t.put("(= 8 7)", "false");
      t.put("(= 8 8)", "true");
      t.put("(= 8 9)", "false");
      t.put("(= v1 8)", "(= 8 v1)");
      t.put("(= v1 v0)", "(= v0 v1)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(= 8 v0)", integerType());
      cannotSimplify("(= v0 v1)", integerType(), integerType());
   }
}
