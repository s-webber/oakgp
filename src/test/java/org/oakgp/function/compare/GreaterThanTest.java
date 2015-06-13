package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class GreaterThanTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new GreaterThan(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(> 7 8)").to(FALSE);
      evaluate("(> 8 8)").to(FALSE);
      evaluate("(> 9 8)").to(TRUE);

      // TODO have similar String comparison tests in other comparison function tests
      evaluate("(> \"dog\" \"zebra\")").to(FALSE);
      evaluate("(> \"dog\" \"dog\")").to(FALSE);
      evaluate("(> \"dog\" \"apple\")").to(TRUE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(> v0 v0)").to("false");
      simplify("(> 8 7)").to("true");
      simplify("(> 8 8)").to("false");
      simplify("(> 8 9)").to("false");
      simplify("(> v0 8)").to("(< 8 v0)");
      simplify("(> 8 v0)").to("(< v0 8)");
      simplify("(> v0 v1)").to("(< v1 v0)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new GreaterThan(stringType()), new LessThan(integerType()) };
   }
}
