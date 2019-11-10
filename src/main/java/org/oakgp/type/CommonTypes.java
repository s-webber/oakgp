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

import static org.oakgp.type.TypeBuilder.name;
import static org.oakgp.type.Types.generic;
import static org.oakgp.type.Types.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.type.Types.Type;

public final class CommonTypes {
   private static final Type COMPARABLE = name(Comparable.class).build();
   private static final Type NUMBER = name(Number.class).parents(COMPARABLE).build();
   private static final Type STRING = name(String.class).parents(COMPARABLE).build();
   private static final Type BOOLEAN = name(Boolean.class).parents(COMPARABLE).build();
   private static final Type INTEGER = name(Integer.class).parents(NUMBER).build();
   private static final Type LONG = name(Long.class).parents(NUMBER).build();
   private static final Type DOUBLE = name(Double.class).parents(NUMBER).build();
   private static final Type BIG_INTEGER = name(BigInteger.class).parents(NUMBER).build();
   private static final Type BIG_DECIMAL = name(BigDecimal.class).parents(NUMBER).build();
   private static final Type LIST = name(List.class).parameters(generic("T")).build();
   private static final Type MAP = name(Map.class).parameters(generic("K"), generic("V")).build();
   private static final Type FUNCTION = name(Function.class).parameters(generic("R"), generic("I")).build();
   private static final Type BI_FUNCTION = name(Function.class).parameters(generic("R"), generic("A"), generic("B")).build();
   private static final Type NULLABLE = name("Nullable").parameters(generic("T")).build();

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
      return type(LIST.getName(), t);
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
      return type(MAP.getName(), key, value);
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
      return type(FUNCTION.getName(), returnType, inputType);
   }

   /** Returns the type associated with {@code org.oakgp.function.Function} instances that accept a {@link #integerType()} and return {@link #booleanType()}. */
   public static Type integerToBooleanFunctionType() {
      return functionType(booleanType(), integerType());
   }

   /**
    * Returns the type associated with instances of {@code org.oakgp.function.BiFunction} with the specified signature.
    *
    * @param returnType
    *           the type of the value returned by the function
    * @param inputType1
    *           the type of the first argument passed to the function
    * @param inputType2
    *           the type of the second argument passed to the function
    */
   public static Type biFunctionType(Type returnType, Type inputType1, Type inputType2) {
      return type(BI_FUNCTION.getName(), returnType, inputType1, inputType2);
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
}
