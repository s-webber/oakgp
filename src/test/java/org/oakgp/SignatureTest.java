package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;

public class SignatureTest {
   @Test
   public void test() {
      Signature signature = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);

      assertEquals(3, signature.getArgumentTypesLength());
      assertSame(INTEGER, signature.getReturnType());
      assertSame(BOOLEAN, signature.getArgumentType(0));
      assertSame(INTEGER, signature.getArgumentType(1));
      assertSame(BOOLEAN, signature.getArgumentType(2));

      assertArrayIndexOutOfBoundsException(signature, -1);
      assertArrayIndexOutOfBoundsException(signature, 3);
   }

   private void assertArrayIndexOutOfBoundsException(Signature signature, int index) {
      try {
         signature.getArgumentType(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         // expected
      }
   }

   @Test
   public void testToString() {
      Signature signature = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);
      assertEquals("INTEGER [BOOLEAN, INTEGER, BOOLEAN]", signature.toString());
   }

   @Test
   public void testEquals() {
      Signature s1 = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);
      Signature s2 = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);
      assertEquals(s1.hashCode(), s2.hashCode());
      assertTrue(s1.equals(s1));
      assertTrue(s1.equals(s2));
      assertTrue(s2.equals(s1));
   }

   @Test
   public void testNotEquals() {
      Signature s1 = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);
      Signature s2 = createSignature(INTEGER, INTEGER, BOOLEAN, BOOLEAN);
      Signature s3 = createSignature(BOOLEAN, BOOLEAN, INTEGER, BOOLEAN);
      Signature s4 = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN, INTEGER);
      Signature s5 = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN, BOOLEAN);

      assertTrue(s1.equals(s1));
      assertFalse(s1.equals(s2));
      assertFalse(s1.equals(s3));
      assertFalse(s1.equals(s4));
      assertFalse(s1.equals(s5));

      assertTrue(s2.equals(s2));
      assertFalse(s2.equals(s1));
      assertFalse(s2.equals(s3));
      assertFalse(s2.equals(s4));
      assertFalse(s2.equals(s5));

      assertTrue(s3.equals(s3));
      assertFalse(s3.equals(s1));
      assertFalse(s3.equals(s2));
      assertFalse(s3.equals(s4));
      assertFalse(s3.equals(s5));

      assertTrue(s4.equals(s4));
      assertFalse(s4.equals(s1));
      assertFalse(s4.equals(s2));
      assertFalse(s4.equals(s3));
      assertFalse(s4.equals(s5));

      assertTrue(s5.equals(s5));
      assertFalse(s5.equals(s1));
      assertFalse(s5.equals(s2));
      assertFalse(s5.equals(s3));
      assertFalse(s5.equals(s4));
   }
}
