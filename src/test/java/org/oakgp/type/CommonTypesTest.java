/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.uniqueType;
import static org.oakgp.type.TypeAssertions.assertName;
import static org.oakgp.type.TypeAssertions.assertNoParameters;
import static org.oakgp.type.TypeAssertions.assertNoParents;
import static org.oakgp.type.TypeAssertions.assertParameters;
import static org.oakgp.type.TypeAssertions.assertParents;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class CommonTypesTest {
   @Test
   public void testNumber() {
      assertType(CommonTypes.numberType(), "java.lang.Number", Types.type(Serializable.class), Types.type(Object.class));
   }

   @Test
   public void testString() {
      assertType(CommonTypes.stringType(), "java.lang.String", CommonTypes.comparableType(String.class), Types.type(Serializable.class),
            Types.type(CharSequence.class), Types.type(Object.class));
   }

   @Test
   public void testInteger() {
      assertType(CommonTypes.integerType(), "java.lang.Integer", CommonTypes.numberType(), CommonTypes.comparableType(Integer.class));
   }

   @Test
   public void testLong() {
      assertType(CommonTypes.longType(), "java.lang.Long", CommonTypes.numberType(), CommonTypes.comparableType(Long.class));
   }

   @Test
   public void testDouble() {
      assertType(CommonTypes.doubleType(), "java.lang.Double", CommonTypes.numberType(), CommonTypes.comparableType(Double.class));
   }

   @Test
   public void testBigInteger() {
      assertType(CommonTypes.bigIntegerType(), "java.math.BigInteger", CommonTypes.numberType(), CommonTypes.comparableType(BigInteger.class));
   }

   @Test
   public void testBigDecimal() {
      assertType(CommonTypes.bigDecimalType(), "java.math.BigDecimal", CommonTypes.numberType(), CommonTypes.comparableType(BigDecimal.class));
   }

   @Test
   public void testBoolean() {
      assertType(CommonTypes.booleanType(), "java.lang.Boolean", CommonTypes.comparableType(Boolean.class), Types.type(Serializable.class),
            Types.type(Object.class));
   }

   @Test
   public void testComparable() {
      Type elementType = uniqueType();
      Type comparable = CommonTypes.comparableType(elementType);

      assertSame(comparable, Types.type("java.lang.Comparable", elementType));
      assertParameters(comparable, elementType);
      assertNoParents(comparable);
   }

   @Test
   public void testList() {
      Type elementType = uniqueType();
      Type list = CommonTypes.listType(elementType);

      assertSame(list, Types.type("java.util.List", elementType));
      assertParameters(list, elementType);
      assertParents(list, Types.type(Collection.class, elementType));
   }

   @Test
   public void testIntegerList() {
      assertSame(CommonTypes.integerListType(), CommonTypes.listType(CommonTypes.integerType()));
   }

   @Test
   public void testSameTypeEntry() {
      Type argType = uniqueType();
      Type entry = CommonTypes.entryType(argType);

      assertSame(entry, Types.type("java.util.Map$Entry", argType, argType));
      assertParameters(entry, argType, argType);
      assertNoParents(entry);
   }

   @Test
   public void testMixedTypeEntry() {
      Type keyType = uniqueType();
      Type valueType = uniqueType();
      Type entry = CommonTypes.entryType(keyType, valueType);

      assertSame(entry, Types.type("java.util.Map$Entry", keyType, valueType));
      assertParameters(entry, keyType, valueType);
      assertNoParents(entry);
   }

   @Test
   public void testMap() {
      Type keyType = uniqueType();
      Type valueType = uniqueType();
      Type map = CommonTypes.mapType(keyType, valueType);

      assertSame(map, Types.type(java.util.Map.class, keyType, valueType));
      assertParameters(map, keyType, valueType);
      assertNoParents(map);
   }

   @Test
   public void testFunction() {
      Type returnType = uniqueType();
      Type inputType = uniqueType();
      Type function = CommonTypes.functionType(returnType, inputType);

      assertSame(function, Types.type("java.util.function.Function", returnType, inputType));
      assertParameters(function, returnType, inputType);
      assertNoParents(function);
   }

   @Test
   public void testIntegerToBooleanFunction() {
      assertSame(CommonTypes.integerToBooleanFunctionType(), CommonTypes.functionType(CommonTypes.booleanType(), CommonTypes.integerType()));
   }

   @Test
   public void testNullable() {
      Type elementType = uniqueType();
      Type nullable = CommonTypes.nullableType(elementType);

      assertSame(nullable, Types.type("Nullable", elementType));
      assertParameters(nullable, elementType);
      assertNoParents(nullable);
   }

   @Test
   public void testIsNullable() {
      Type elementType = uniqueType();

      assertNullable(CommonTypes.nullableType(elementType));
      assertNullable(CommonTypes.nullableType(CommonTypes.integerType()));

      assertNotNullable(elementType);
      assertNotNullable(CommonTypes.listType(CommonTypes.nullableType(elementType)));
   }

   private void assertType(Type type, String name, Type... parents) {
      assertName(type, name);
      assertSame(type, Types.type(name));
      assertNoParameters(type);
      assertParents(type, parents);
   }

   private void assertNullable(Type t) {
      assertTrue(CommonTypes.isNullable(t));
   }

   private void assertNotNullable(Type t) {
      assertFalse(CommonTypes.isNullable(t));
   }
}
