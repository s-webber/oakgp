package org.oakgp.function.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.booleanType;

import org.junit.Test;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;

public class ComparisonOperatorTest {
   @Test
   public void testGetSignature() {
      Type type = Type.type("ComparisonOperatorTest");
      Function f = new ComparisonOperator(type, true) {
         @Override
         protected boolean evaluate(int diff) {
            throw new UnsupportedOperationException();
         }
      };
      Signature signature = f.getSignature();
      assertSame(booleanType(), signature.getReturnType());
      assertEquals(2, signature.getArgumentTypesLength());
      assertSame(type, signature.getArgumentType(0));
      assertSame(type, signature.getArgumentType(1));
   }
}
