package org.oakgp.function.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.List;

import org.junit.Test;
import org.oakgp.Signature;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.math.IntegerUtils;

public class IfTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new If(integerType());
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(if (< 8 9) (+ 1 2) (* 6 3))", 3);
      testCases.put("(if (> 8 9) (+ 1 2) (* 6 3))", 18);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      // TODO test using SimplifyTestCases.put(String, String, Object[][])

      testCases.put("(if (< 1 2) 4 7)", "4");
      testCases.put("(if (> 1 2) 4 7)", "7");
      testCases.put("(if (> v0 v1) v2 v2)", "v2");

      testCases.put("(if (> v0 v1) v3 v4)", "(if (< v1 v0) v3 v4)");

      testCases.put("(if (< v0 v1) (if (< v0 v1) v2 v3) v4)", "(if (< v0 v1) v2 v4)");
      testCases.put("(if (< v0 v1) v2 (if (< v0 v1) v3 v4))", "(if (< v0 v1) v2 v4)");

      testCases.put("(if (< v0 v1) (+ v2 (if (< v0 v1) v3 v4)) v5)", "(if (< v0 v1) (+ v2 v3) v5)");
      testCases.put("(if (< v0 v1) v5 (+ v2 (if (< v0 v1) v3 v4)))", "(if (< v0 v1) v5 (+ v2 v4))");

      testCases.put("(if (< v0 v1) (if (< v2 v3) (if (< v0 v1) v4 v5) v6) v7)", "(if (< v0 v1) (if (< v2 v3) v4 v6) v7)");
      testCases.put("(if (< v0 v1) v7 (if (< v2 v3) (if (< v0 v1) v4 v5) v6))", "(if (< v0 v1) v7 (if (< v2 v3) v5 v6))");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      testCases.add("(if (< v0 v1) 1 2)");
      testCases.add("(if (< v0 v1) v2 v3)");
   }

   @Test
   public void testGetSignature() {
      Signature signature = getFunction().getSignature();
      assertSame(integerType(), signature.getReturnType());
      assertEquals(3, signature.getArgumentTypesLength());
      assertSame(booleanType(), signature.getArgumentType(0));
      assertSame(integerType(), signature.getArgumentType(1));
      assertSame(integerType(), signature.getArgumentType(2));
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getMultiply(), new LessThan(integerType()),
            new GreaterThan(integerType()) };
   }
}
