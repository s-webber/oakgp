package org.oakgp;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a data type.
 * <p>
 * e.g. integer, boolean, string, array, function.
 */
public final class Type implements Comparable<Type> {
   private static final ConcurrentHashMap<Type, Type> TYPE_CACHE = new ConcurrentHashMap<>();
   private static final Type[] EMPTY_ARRAY = new Type[0];

   private final String name;
   private final Type[] args;
   private final int hashCode;

   public static Type stringType() {
      return type("string");
   }

   public static Type booleanType() {
      return type("boolean");
   }

   public static Type integerType() {
      return type("integer");
   }

   public static Type integerToBooleanFunctionType() {
      return functionType(booleanType(), integerType());
   }

   public static Type optionalType(Type t) {
      return type("optional", t);
   }

   public static Type functionType(Type... signature) {
      if (signature.length < 2) {
         throw new IllegalArgumentException();
      }
      return type("function", signature);
   }

   public static Type integerArrayType() {
      return arrayType(integerType());
   }

   public static Type booleanArrayType() {
      return arrayType(booleanType());
   }

   public static Type arrayType(Type t) {
      return type("array", t);
   }

   public static Type type(String name) {
      return type(name, EMPTY_ARRAY);
   }

   public static Type type(String name, Type... args) {
      return type(new Type(name, args));
   }

   private static Type type(Type t) {
      return TYPE_CACHE.computeIfAbsent(t, (k) -> k);
   }

   /**
    * Returns {@code true} if the two arrays contain the same number of elements, and all corresponding pairs of elements in the two arrays are the same.
    * <p>
    * Assumes no {@code null} values. Compares elements using {@code ==} rather than {@code equals(Object)}. TODO provide details of performance test results
    * that caused this to be introduced.
    */
   public static boolean sameTypes(Type[] a, Type[] b) {
      int length = a.length;
      if (b.length != length) {
         return false;
      }

      for (int i = 0; i < length; i++) {
         if (a[i] != b[i]) {
            return false;
         }
      }

      return true;
   }

   private Type(String name, Type... args) {
      this.name = name;
      this.args = args;
      this.hashCode = (name.hashCode() * 31) * Arrays.hashCode(args);
   }

   @Override
   public int compareTo(Type t) {
      return name.compareTo(t.name);
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Type) {
         Type t = (Type) o;
         return this.name.equals(t.name) && sameTypes(this.args, t.args);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public String toString() {
      if (args.length == 0) {
         return name;
      } else {
         return name + " " + Arrays.toString(args);
      }
   }
}
