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

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class CommonTypesTest {
   @Test
   public void testComparable() {
      assertType(CommonTypes.comparableType(), "Comparable");
   }

   @Test
   public void testNumber() {
      assertType(CommonTypes.numberType(), "Number", CommonTypes.comparableType());
   }

   @Test
   public void testString() {
      assertType(CommonTypes.stringType(), "String", CommonTypes.comparableType());
   }

   @Test
   public void testInteger() {
      assertType(CommonTypes.integerType(), "Integer", CommonTypes.numberType(), CommonTypes.comparableType());
   }

   @Test
   public void testLong() {
      assertType(CommonTypes.longType(), "Long", CommonTypes.numberType(), CommonTypes.comparableType());
   }

   @Test
   public void testDouble() {
      assertType(CommonTypes.doubleType(), "Double", CommonTypes.numberType(), CommonTypes.comparableType());
   }

   @Test
   public void testBigInteger() {
      assertType(CommonTypes.bigIntegerType(), "BigInteger", CommonTypes.numberType(), CommonTypes.comparableType());
   }

   @Test
   public void testBigDecimal() {
      assertType(CommonTypes.bigDecimalType(), "BigDecimal", CommonTypes.numberType(), CommonTypes.comparableType());
   }

   @Test
   public void testBoolean() {
      assertType(CommonTypes.booleanType(), "Boolean", CommonTypes.comparableType());
   }

   @Test
   public void testList() {
      Type elementType = uniqueType();
      Type list = CommonTypes.listType(elementType);

      assertSame(list, Types.type("List", elementType));
      assertParameters(list, elementType);
      assertNoParents(list);
   }

   @Test
   public void testIntegerList() {
      assertSame(CommonTypes.integerListType(), CommonTypes.listType(CommonTypes.integerType()));
   }

   @Test
   public void testBooleanList() {
      assertSame(CommonTypes.booleanListType(), CommonTypes.listType(CommonTypes.booleanType()));
   }

   @Test
   public void testMap() {
      Type keyType = uniqueType();
      Type valueType = uniqueType();
      Type map = CommonTypes.mapType(keyType, valueType);

      assertSame(map, Types.type("Map", keyType, valueType));
      assertParameters(map, keyType, valueType);
      assertNoParents(map);
   }

   @Test
   public void testFunction() {
      Type returnType = uniqueType();
      Type inputType = uniqueType();
      Type function = CommonTypes.functionType(returnType, inputType);

      assertSame(function, Types.type("Function", returnType, inputType));
      assertParameters(function, returnType, inputType);
      assertNoParents(function);
   }

   @Test
   public void testIntegerToBooleanFunction() {
      assertSame(CommonTypes.integerToBooleanFunctionType(), CommonTypes.functionType(CommonTypes.booleanType(), CommonTypes.integerType()));
   }

   @Test
   public void testBiFunction() {
      Type returnType = uniqueType();
      Type inputType1 = uniqueType();
      Type inputType2 = uniqueType();
      Type function = CommonTypes.biFunctionType(returnType, inputType1, inputType2);

      assertSame(function, Types.type("Function", returnType, inputType1, inputType2));
      assertParameters(function, returnType, inputType1, inputType2);
      assertNoParents(function);
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
