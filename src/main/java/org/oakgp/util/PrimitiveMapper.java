package org.oakgp.util;

// TODO rename and test
public final class PrimitiveMapper {
   private PrimitiveMapper() {
   }

   public static Class<?> mapPrimitive(Class<?> input) {
      if (!input.isPrimitive()) {
         return input;
      } else if (input == void.class) {
         return Void.class;
      } else if (input == boolean.class) {
         return Boolean.class;
      } else if (input == char.class) {
         return Character.class;
      } else if (input == byte.class) {
         return Byte.class;
      } else if (input == short.class) {
         return Short.class;
      } else if (input == int.class) {
         return Integer.class;
      } else if (input == long.class) {
         return Long.class;
      } else if (input == double.class) {
         return Double.class;
      } else if (input == float.class) {
         return Float.class;
      } else {
         throw new RuntimeException(input.toString()); // TODO
      }
   }
}
