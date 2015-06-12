package org.oakgp.function.hof;

import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;

public class MapTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Map(integerType(), booleanType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(map pos? [2 -12 8 -3 -7 6])").to(createArguments("true", "false", "true", "false", "false", "true"));
      evaluate("(map neg? [2 -12 8 -3 -7 6])").to(createArguments("false", "true", "false", "true", "true", "false"));
      evaluate("(map zero? [2 -12 8 -3 -7 6])").to(createArguments("false", "false", "false", "false", "false", "false"));
   }

   @Override
   public void testCanSimplify() {
      simplify("(map pos? [2 -12 8])").to("[true false true]");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new IsPositive(), new IsNegative(), new IsZero() };
   }
}
