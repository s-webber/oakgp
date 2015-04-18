package org.oakgp.node;

import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Assignments;
import org.oakgp.Type;

/**
 * Represents a constant value.
 * <p>
 * Return the same value each time is it evaluated.
 */
public final class ConstantNode implements Node {
   private final Object value;
   private final Type type;

   /**
    * Constructs a new {@code ConstantNode} that represents the specified value.
    *
    * @param value
    *           the value to be represented by the {@code ConstantNode}
    * @param type
    *           the {@code Type} that the value represented by this node is of
    */
   public ConstantNode(Object value, Type type) {
      this.value = value;
      this.type = type;
   }

   /**
    * Returns the value specified when this {@code ConstantNode} was constructed.
    */
   @Override
   public Object evaluate(Assignments assignments) {
      return value;
   }

   @Override
   public int getNodeCount() {
      return 1;
   }

   @Override
   public int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      return treeWalkerStrategy.test(this) ? 1 : 0;
   }

   @Override
   public Node replaceAt(int index, Function<Node, Node> replacement) {
      return replacement.apply(this);
   }

   @Override
   public Node getAt(int index) {
      return this;
   }

   @Override
   public int getDepth() {
      return 1;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public int hashCode() {
      return value.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof ConstantNode) {
         ConstantNode c = (ConstantNode) o;
         return this.type == c.type && this.value.equals(c.value);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return value.toString();
   }
}
