package org.oakgp.util;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.pair.Pair;
import org.oakgp.primitive.VariableSet;
import org.oakgp.type.Types.Type;

// TODO test
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
      private List<Type> types = new ArrayList<>();

      private AnonymousVariableSetBuilder() {
      }

      public AnonymousVariableSetBuilder add(Type type) {
         types.add(type);
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
         return VariableSet.createVariableSet(types.toArray(new Type[types.size()]));
      }
   }

   public static class NamedVariableSetBuilder {
      private List<Pair<String, Type>> types = new ArrayList<>();

      private NamedVariableSetBuilder() {
      }

      public NamedVariableSetBuilder add(String name, Type type) {
         types.add(new Pair<>(name, type));
         return this;
      }

      public VariableSet build() {
         return VariableSet.createVariableSet(types.toArray(new Pair[types.size()]));
      }
   }
}
