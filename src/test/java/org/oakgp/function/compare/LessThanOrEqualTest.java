package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class LessThanOrEqualTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new LessThanOrEqual(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(<= 7 8)").to(TRUE);
      evaluate("(<= 8 8)").to(TRUE);
      evaluate("(<= 9 8)").to(FALSE);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases t) {
      t.put("(<= v1 v1)", "true");
      t.put("(<= 8 7)", "false");
      t.put("(<= 8 8)", "true");
      t.put("(<= 8 9)", "true");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(<= v0 8)", integerType());
      cannotSimplify("(<= 8 v0)", integerType());
      cannotSimplify("(<= v0 v1)", integerType(), integerType());
   }
}
