package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
   public void testLong() {
      assertType("long", Type::longType);
   }

   @Test
   public void testBigDecimal() {
      assertType("BigDecimal", Type::bigDecimalType);
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
   public void testNullableInteger() {
      assertNullableType("integer", Type::integerType);
   }

   @Test
   public void testNullableBooleanArray() {
      assertNullableType("array [boolean]", Type::booleanArrayType);
   }

   @Test
   public void testIsNullable() {
      String nullableName = "nullable";
      Type integerType = Type.integerType();
      Type booleanType = Type.booleanType();

      assertNullable(Type.type(nullableName, integerType));
      assertNullable(Type.type(nullableName, booleanType));

      assertNotNullable(integerType);
      assertNotNullable(Type.type(nullableName, integerType, integerType));
   }

   private void assertNullable(Type t) {
      assertTrue(Type.isNullable(t));
   }

   private void assertNotNullable(Type t) {
      assertFalse(Type.isNullable(t));
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

   @Test
   public void testSameTypes() {
      Type[] t1 = { Type.booleanType(), Type.integerType(), Type.booleanType() };

      assertSameTypes(t1, new Type[] { Type.booleanType(), Type.integerType(), Type.booleanType() });

      // same types, but in a different order
      assertNotSameTypes(t1, new Type[] { Type.integerType(), Type.booleanType(), Type.booleanType() });

      // different type for final element
      assertNotSameTypes(t1, new Type[] { Type.booleanType(), Type.integerType(), Type.stringType() });

      // too few types
      assertNotSameTypes(t1, new Type[] { Type.booleanType(), Type.integerType() });

      // too many types
      assertNotSameTypes(t1, new Type[] { Type.booleanType(), Type.integerType(), Type.booleanType(), Type.booleanType() });
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

   private void assertNullableType(String name, Supplier<Type> s) {
      Type t = Type.type("nullable", s.get());
      assertSame(t, Type.type("nullable", s.get()));
      assertSame(t, Type.nullableType(s.get()));
      assertEquals("nullable [" + name + "]", t.toString());
   }

   private void assertSameTypes(Type[] a, Type[] b) {
      assertTrue(Type.sameTypes(a, a));
      assertTrue(Type.sameTypes(b, b));
      assertTrue(Type.sameTypes(a, b));
      assertTrue(Type.sameTypes(b, a));
   }

   private void assertNotSameTypes(Type[] a, Type[] b) {
      assertTrue(Type.sameTypes(a, a));
      assertTrue(Type.sameTypes(b, b));
      assertFalse(Type.sameTypes(a, b));
      assertFalse(Type.sameTypes(b, a));
   }
}
