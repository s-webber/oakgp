package org.oakgp.node;

import java.util.function.Function;

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
   public Node replaceAt(int index, Function<Node, Node> replacement) {
      return replacement.apply(this);
   }

   @Override
   public Node getAt(int index) {
      return this;
   }

   @Override
   public Type getType() {
      return type;
   }

   // TODO cache hashCode (e.g. (id+1)*31) and remove equals method
   @Override
   public int hashCode() {
      return id;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof VariableNode && this.id == ((VariableNode) o).id;
   }

   @Override
   public String toString() {
      return "v" + id;
   }
}
