package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class GreaterThanOrEqualTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new GreaterThanOrEqual(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(>= 7 8)").to(FALSE);
      evaluate("(>= 8 8)").to(TRUE);
      evaluate("(>= 9 8)").to(TRUE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(>= v0 v0)").with(integerType()).to("true");
      simplify("(>= 8 7)").to("true");
      simplify("(>= 8 8)").to("true");
      simplify("(>= 8 9)").to("false");

      simplify("(>= v0 8)").with(integerType()).to("(<= 8 v0)");
      simplify("(>= 8 v0)").with(integerType()).to("(<= v0 8)");
      simplify("(>= v0 v1)").with(integerType(), integerType()).to("(<= v1 v0)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new LessThanOrEqual(integerType()) };
   }
}
