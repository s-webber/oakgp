package org.oakgp.function.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import java.util.List;

import org.junit.Test;
import org.oakgp.Signature;
import org.oakgp.function.AbstractOperatorTest;
import org.oakgp.function.Function;

public class IfTest extends AbstractOperatorTest {
   @Override
   protected Function getFunction() {
      return new If();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.put("(if (< 8 9) (+ 1 2) (* 6 3))", 3);
      testCases.put("(if (> 8 9) (+ 1 2) (* 6 3))", 18);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      testCases.put("(if (< 1 2) 4 7)", "4");
      testCases.put("(if (> 1 2) 4 7)", "7");
      testCases.put("(if (> v0 v1) v2 v2)", "v2");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      testCases.add("(if (< v0 v1) 1 2)");
   }

   @Test
   public void testGetSignature() {
      Signature signature = getFunction().getSignature();
      assertSame(INTEGER, signature.getReturnType());
      assertEquals(3, signature.getArgumentTypesLength());
      assertSame(BOOLEAN, signature.getArgumentType(0));
      assertSame(INTEGER, signature.getArgumentType(1));
      assertSame(INTEGER, signature.getArgumentType(2));
   }
}
