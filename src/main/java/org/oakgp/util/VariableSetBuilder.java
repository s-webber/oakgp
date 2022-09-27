package org.oakgp.util;

import org.oakgp.primitive.VariableSet;
import org.oakgp.type.Types.Type;

public class VariableSetBuilder {
   public AnonymousVariableSetBuilder add(Type type) {
      return new AnonymousVariableSetBuilder().add(type);
   }

   public AnonymousVariableSetBuilder add(Type type, int count) {
      return new AnonymousVariableSetBuilder().add(type, count);
   }

   public NamedVariableSetBuilder add(String name, Type type) {
      return new NamedVariableSetBuilder().add(name, type);
   }

   public static class AnonymousVariableSetBuilder {
      private AnonymousVariableSetBuilder() {
      }

      public AnonymousVariableSetBuilder add(Type type) {
         return this;
      }

      public AnonymousVariableSetBuilder add(Type type, int count) {
         if (count < 1) {
            throw new IllegalArgumentException(count + " < 1");
         }
         for (int i = 0; i < count; i++) {
            add(type);
         }
         return this;
      }

      public VariableSet build() {
         return null;
      }
   }

   public static class NamedVariableSetBuilder {
      private NamedVariableSetBuilder() {
      }

      public NamedVariableSetBuilder add(String name, Type type) {
         return this;
      }

      public VariableSet build() {
         return null;
      }
   }
}
