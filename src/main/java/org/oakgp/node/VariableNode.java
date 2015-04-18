package org.oakgp.node;

import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Assignments;
import org.oakgp.Type;

/**
 * Represents a variable.
 * <p>
 * The result of evaluating this node will vary based on the {@link Assignments} specified.
 */
public final class VariableNode implements Node {
   private final int id;
   private final Type type;
   private final int hashCode;

   /**
    * Constructs a new {@code VariableNode} with the specified ID.
    *
    * @param id
    *           represents the index to specify when getting the value for this variable from an {@link Assignments}
    * @param type
    *           the {@code Type} that the values represented by this node are of
    */
   public VariableNode(int id, Type type) {
      this.id = id;
      this.type = type;
      // +1 so never multiplying by 0
      // *997 as an alternative to *31 - so VariableNode with id 1 has a different hash code than a ConstantNode with an Integer with value 1
      this.hashCode = (id + 1) * 997;
   }

   public int getId() {
      return id;
   }

   /**
    * Returns the value assigned to this {@code VariableNode} by the specified {@code Assignments}.
    *
    * @return the value stored in {@code Assignments} at the index specified by the ID of this {@code VariableNode}
    */
   @Override
   public Object evaluate(Assignments assignments) {
      return assignments.get(id);
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
      return hashCode;
   }

   // NOTE: *not* over-riding equals(Object) as two VariableNode references are only "equal" if they refer to the same instance

   @Override
   public String toString() {
      return "v" + id;
   }
}
