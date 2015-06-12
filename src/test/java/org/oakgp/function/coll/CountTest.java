package org.oakgp.function.coll;

import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class CountTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Count(integerType());
   }

   @Override
   public void testEvaluate() {
      // testCases.put("(count [])", 0); TODO
      evaluate("(count [2 -12 8])").to(3);
      evaluate("(count [2 -12 8 -3 -7])").to(5);
   }

   @Override
   public void testCanSimplify() {
      simplify("(count [2 -12 8])").to("3");
   }

   @Override
   public void testCannotSimplify() {
   }
}
