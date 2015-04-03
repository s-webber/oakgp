package org.oakgp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Type implements Comparable<Type> {
   private static final Map<Type, Type> typeCache = new HashMap<>();

   private final String name;
   private final Type[] args;

   private Type(String name, Type... args) {
      this.name = name;
      this.args = args;
   }

   public static Type stringType() {
      return add(new Type("string"));
   }

   public static Type booleanType() {
      return add(new Type("boolean"));
   }

   public static Type integerType() {
      return add(new Type("integer"));
   }

   public static Type functionType() {
      return add(new Type("function"));
   }

   public static Type integerArrayType() {
      return add(arrayType(integerType()));
   }

   public static Type booleanArrayType() {
      return add(arrayType(booleanType()));
   }

   public static Type arrayType(Type t) {
      return add(new Type("array", t));
   }

   private static Type add(Type t) {
      Type e = typeCache.get(t);
      if (e == null) {
         typeCache.put(t, t);
         return t;
      } else {
         return e;
      }
   }

   @Override
   public int compareTo(Type t) {
      return name.compareTo(t.name);
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Type) {
         Type t = (Type) o;
         return this.name.equals(t.name) && Arrays.equals(this.args, args);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      // TODO include args in hashCode? cache hashCode?
      return name.hashCode();
   }

   @Override
   public String toString() {
      return name;
   }
}
