package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class NotEqualTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new NotEqual(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(!= 7 8)").to(TRUE);
      evaluate("(!= 8 8)").to(FALSE);
      evaluate("(!= 9 8)").to(TRUE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(!= v0 v0)").with(integerType()).to("false");
      simplify("(!= 8 7)").to("true");
      simplify("(!= 8 8)").to("false");
      simplify("(!= 8 9)").to("true");
      simplify("(!= v0 8)").with(integerType()).to("(!= 8 v0)");
      simplify("(!= v1 v0)").with(integerType(), integerType()).to("(!= v0 v1)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(!= 8 v0)", integerType());
      cannotSimplify("(!= v0 v1)", integerType(), integerType());
   }
}
