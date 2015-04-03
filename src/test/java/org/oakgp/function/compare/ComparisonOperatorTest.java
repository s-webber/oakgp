package org.oakgp.function.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import org.junit.Test;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public class ComparisonOperatorTest {
   @Test
   public void testGetSignature() {
      Function f = new ComparisonOperator(true) {
         @Override
         protected boolean evaluate(int arg1, int arg2) {
            throw new UnsupportedOperationException();
         }
      };
      Signature signature = f.getSignature();
      assertSame(booleanType(), signature.getReturnType());
      assertEquals(2, signature.getArgumentTypesLength());
      assertSame(integerType(), signature.getArgumentType(0));
      assertSame(integerType(), signature.getArgumentType(1));
   }
}
