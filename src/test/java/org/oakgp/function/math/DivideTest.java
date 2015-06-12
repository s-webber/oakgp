package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class DivideTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getDivide();
   }

   @Override
   public void testEvaluate() {
      evaluate("(/ 12 1)").to(12);
      evaluate("(/ 12 12)").to(1);
      evaluate("(/ 12 0)").to(1);
      evaluate("(/ 12 3)").to(4);
   }

   @Override
   public void testCanSimplify() {
      simplify("(/ 4 0)").to("1");
      simplify("(/ 4 1)").to("4");
      simplify("(/ 4 -1)").to("-4");
      simplify("(/ -4 -1)").to("4");
      simplify("(/ v0 -1)").with(integerType()).to("(- 0 v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(/ 1 v0)", integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getSubtract() };
   }
}
