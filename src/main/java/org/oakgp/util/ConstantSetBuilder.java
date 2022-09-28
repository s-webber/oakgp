package org.oakgp.util;

import static org.oakgp.rank.fitness.DoubleArrayBuilder.doubles;
import static org.oakgp.rank.fitness.IntegerArrayBuilder.integers;
import static org.oakgp.type.Types.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.type.Types.Type;

// TODO test
public final class ConstantSetBuilder {
   private final List<ConstantNode> constants = new ArrayList<>();

   public ConstantSetBuilder doubleRange(double minInclusive, double maxInclusive, double step) {
      addAll(doubles().from(minInclusive).to(maxInclusive).increment(step).build());
      return this;
   }

   public ConstantSetBuilder integerRange(int minInclusive, int maxInclusive) {
      addAll(integers().from(minInclusive).to(maxInclusive).build());
      return this;
   }

   public ConstantSetBuilder addAll(Object first, Object... rest) {
      add(first);
      for (Object value : rest) {
         add(value);
      }
      return this;
   }

   public ConstantSetBuilder addAll(Object[] values) {
      for (Object value : values) {
         add(value);
      }
      return this;
   }

   public ConstantSetBuilder addAll(Collection<?> values) {
      for (Object value : values) {
         add(value);
      }
      return this;
   }

   public ConstantSetBuilder add(Object value) {
      if (value instanceof Type || value instanceof Class || value instanceof ConstantNode) {
         throw new IllegalArgumentException(value + " " + value.getClass()); // TODO
      }
      return add(value, value.getClass());
   }

   public ConstantSetBuilder add(Object value, Class<?> c) {
      return add(value, type(c));
   }

   public ConstantSetBuilder add(Object value, Type type) {
      // TODO use cache for true, false, void, etc.
      constants.add(new ConstantNode(value, type));
      return this;
   }

   public OfType with(Type t) {
      return new OfType(this, t);
   }

   public ConstantSet build() {
      return new ConstantSet(constants.toArray(new ConstantNode[constants.size()]));
   }

   public static class OfType {
      private final ConstantSetBuilder parent;
      private final Type type;

      private OfType(ConstantSetBuilder parent, Type type) {
         this.parent = parent;
         this.type = type;
      }

      public ConstantSetBuilder addAll(Object first, Object... rest) {
         parent.add(first, type);
         for (Object value : rest) {
            parent.add(value, type);
         }
         return parent;
      }

      public ConstantSetBuilder addAll(Collection<?> values) {
         for (Object value : values) {
            parent.add(value, type);
         }
         return parent;
      }

      public ConstantSetBuilder addAll(Object[] values) {
         for (Object value : values) {
            parent.add(value, type);
         }
         return parent;
      }
   }
}
