package org.oakgp.function.classify;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class IsZeroTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new IsZero();
   }

   @Override
   public void testEvaluate() {
      evaluate("(zero? 0)").to(true);
      evaluate("(zero? 27)").to(false);
      evaluate("(zero? 1)").to(false);
      evaluate("(zero? -1)").to(false);
      evaluate("(zero? -27)").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(zero? 0)").to("true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
