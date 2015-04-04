package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import java.util.function.Supplier;

import org.junit.Test;

public class TypeTest {
   @Test
   public void testString() {
      assertType("string", Type::stringType);
   }

   @Test
   public void testInteger() {
      assertType("integer", Type::integerType);
   }

   @Test
   public void testBoolean() {
      assertType("boolean", Type::booleanType);
   }

   @Test
   public void testIntegerArray() {
      assertArrayType("integer", Type::integerArrayType);
   }

   @Test
   public void testBooleanArray() {
      assertArrayType("boolean", Type::booleanArrayType);
   }

   @Test
   public void testIntegerToBooleanFunction() {
      Type t = Type.integerToBooleanFunctionType();
      assertSame(t, Type.integerToBooleanFunctionType());
      assertSame(t, Type.functionType(Type.booleanType(), Type.integerType()));
      assertEquals("function [boolean, integer]", t.toString());
   }

   @Test
   public void testNotEquals() {
      // different core types
      assertNotEquals(Type.booleanType(), Type.stringType());
      // array vs. core type
      assertNotEquals(Type.booleanType(), Type.booleanArrayType());
      // different arrays
      assertNotEquals(Type.booleanArrayType(), Type.integerArrayType());
      // different functions
      assertNotEquals(Type.functionType(Type.integerType(), Type.stringType()), Type.functionType(Type.stringType(), Type.integerType()));
   }

   @Test
   public void testUserDefinedType() {
      Type t = Type.type("qwerty", Type.integerType());
      assertEquals("qwerty [integer]", t.toString());
      assertEquals(t, Type.type("qwerty", Type.integerType()));
      assertSame(t, Type.type("qwerty", Type.integerType()));
      assertNotEquals(t, Type.type("Qwerty", Type.integerType()));
      assertNotEquals(t, Type.type("qwe-rty", Type.integerType()));
      assertNotEquals(t, Type.type("qwe rty", Type.integerType()));
      assertNotEquals(t, Type.type(" qwerty", Type.integerType()));
      assertNotEquals(t, Type.type("qwerty ", Type.integerType()));
      assertNotEquals(t, Type.type("qwerty"));
      assertNotEquals(t, Type.type("qwerty", Type.integerType(), Type.integerType()));
      assertNotEquals(t, Type.type("qwerty", Type.stringType()));
   }

   private void assertType(String name, Supplier<Type> s) {
      Type t = s.get();
      assertEquals(t, s.get());
      assertSame(t, s.get());
      assertEquals(t, Type.type(name));
      assertSame(t, Type.type(name));
      assertEquals(name, t.toString());
   }

   private void assertArrayType(String name, Supplier<Type> s) {
      Type t = s.get();
      assertSame(t, s.get());
      assertSame(t, Type.arrayType(Type.type(name)));
      assertEquals("array [" + name + "]", t.toString());
   }
}
