/*
 * Copyright 2019 S. Webber
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

import static org.oakgp.type.Types.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.oakgp.type.Types.Type;

public final class CommonTypes {
   private static final Type COMPARABLE = type(Comparable.class, Types.generic("E"));
   private static final Type NUMBER = type(Number.class);
   private static final Type STRING = type(String.class);
   private static final Type BOOLEAN = type(Boolean.class);
   private static final Type INTEGER = type(Integer.class);
   private static final Type LONG = type(Long.class);
   private static final Type DOUBLE = type(Double.class);
   private static final Type BIG_INTEGER = type(BigInteger.class);
   private static final Type BIG_DECIMAL = type(BigDecimal.class);
   private static final Type VOID = type(Void.class);
   private static final Type NULLABLE = TypeBuilder.name("Nullable").parameters(Types.generic("E")).build();

   public static Type comparableType(Class<?> c) {
      return comparableType(Types.type(c));
   }

   public static Type comparableType(Type t) {
      return Types.type(Comparable.class, t);
   }

   /** Returns the type associated with implementations of {@code java.lang.Comparable}. */
   public static Type comparableType() {
      return COMPARABLE;
   }

   /** Returns the type associated with implementations of {@code java.lang.Number}. */
   public static Type numberType() {
      return NUMBER;
   }

   /** Returns the type associated with instances of {@code java.lang.String}. */
   public static Type stringType() {
      return STRING;
   }

   /** Returns the type associated with instances of {@code java.lang.Boolean}. */
   public static Type booleanType() {
      return BOOLEAN;
   }

   /** Returns the type associated with instances of {@code java.lang.Integer}. */
   public static Type integerType() {
      return INTEGER;
   }

   /** Returns the type associated with instances of {@code java.lang.Long}. */
   public static Type longType() {
      return LONG;
   }

   /** Returns the type associated with instances of {@code java.lang.Double}. */
   public static Type doubleType() {
      return DOUBLE;
   }

   /** Returns the type associated with instances of {@code java.math.BigInteger}. */
   public static Type bigIntegerType() {
      return BIG_INTEGER;
   }

   /** Returns the type associated with instances of {@code java.math.BigDecimal}. */
   public static Type bigDecimalType() {
      return BIG_DECIMAL;
   }

   /** Returns the type associated with a {@code java.util.List} containing elements of the specified type. */
   public static Type listType(Type t) {
      return type(List.class, t);
   }

   /** Returns the type associated with a {@code java.util.List} containing elements of type {@link #integerType()}. */
   public static Type integerListType() {
      return listType(integerType());
   }

   /** Returns the type associated with a {@code java.util.List} containing elements of type {@link #booleanType()}. */
   public static Type booleanListType() {
      return listType(booleanType());
   }

   /** Returns the type associated with a map collection containing entries of the specified key and value types. */
   public static Type mapType(Type key, Type value) {
      return type(Map.class, key, value);
   }

   /** Returns the type associated with a key-value pair. */
   public static Type entryType(Type type) {
      return entryType(type, type);
   }

   /** Returns the type associated with a key-value pair. */
   public static Type entryType(Type key, Type value) {
      return type(Map.Entry.class, key, value);
   }

   /**
    * Returns the type associated with instances of {@code org.oakgp.function.Function} with the specified signature.
    *
    * @param returnType
    *           the type of the value returned by the function
    * @param inputType
    *           the type of the argument passed to the function
    */
   public static Type functionType(Type returnType, Type inputType) {
      return type(java.util.function.Function.class, returnType, inputType);
   }

   /**
    * Returns the type associated with {@code org.oakgp.function.Function} instances that accept a {@link #integerType()} and return {@link #booleanType()}.
    */
   public static Type integerToBooleanFunctionType() {
      return functionType(booleanType(), integerType());
   }

   /**
    * Returns a type that represents values that can be either the given {@code Type} or {@code null}.
    * <p>
    * e.g. The result of calling {@code nullableType(stringType())} is a type that represents values that can be <i>either</i> a {@code java.lang.String}
    * <i>or</i> {@code null}.
    *
    * @see #isNullable(Type)
    */
   public static Type nullableType(Type t) {
      return type(NULLABLE.getName(), t);
   }

   /**
    * Returns {@code true} if the given {@code Type} represents values that may be {@code null}.
    *
    * @see #nullableType(Type)
    */
   public static boolean isNullable(Type t) { // TODO move to utils
      return t.getName().equals(NULLABLE.getName()); // TODO and check parameters.length == 1
   }

   public static Type voidType() {
      return VOID;
   }
}
