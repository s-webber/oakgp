package org.oakgp.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.Types.generic;
import static org.oakgp.type.Types.type;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class IsAssignableTest {
   @Test
   public void test2() {
      assertSame(type(Deck.class, type(String.class)), type(Deck.class, type(String.class)));
      assertNotSame(type(Deck.class, type(String.class)), type(Deck.class, type(StringBuilder.class)));
   }

   @Test
   public void testEnum() {
      Type x = type(X.class);
      Type e = type(Enum.class, Types.type(Object.class));
      Type e2 = type(Enum.class, Types.generic("E"));

      assertTrue(x.isAssignable(e));
      assertTrue(x.isAssignable(e2));
      assertFalse(e.isAssignable(x));
      assertFalse(e2.isAssignable(x));
   }

   @Test
   public void test() {
      Type listGeneric = type(List.class, generic("E", type(Number.class)));
      Type listInteger = type(List.class, type(Integer.class));
      Type listNumber = type(List.class, type(Number.class));
      Type listObject = type(List.class, type(Object.class));
      Type listString = type(List.class, type(String.class));
      Type listBoolean = type(List.class, type(Boolean.class));

      assertTrue(listInteger.isAssignable(listGeneric));
      assertTrue(listInteger.isAssignable(listNumber));
      assertTrue(listInteger.isAssignable(listObject));
      assertTrue(listInteger.isAssignable(type(Object.class)));
      assertFalse(listInteger.isAssignable(listBoolean));
      assertFalse(listInteger.isAssignable(listString));
      assertFalse(listInteger.isAssignable(type(Integer.class)));

      assertFalse(listBoolean.isAssignable(listGeneric));
      assertFalse(listObject.isAssignable(listGeneric));
      assertFalse(listString.isAssignable(listGeneric));
   }

   @Test
   public void test33() {
      assertTrue(type(Boolean.class).isAssignable(type(Boolean.class)));
      assertFalse(type(Boolean.class).isAssignable(type(Integer.class)));
      assertFalse(type(Integer.class).isAssignable(type(Boolean.class)));
   }

   static enum X {
      A, B, C
   }

   static class Deck<X extends CharSequence> extends HashMap<X, X> {

   }
}
