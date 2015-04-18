package org.oakgp.node;

import java.util.function.Function;

import org.oakgp.Assignments;
import org.oakgp.Type;

/** A node represents a single point of a tree structure. */
public interface Node {
   /**
    * Returns the result of evaluating this {@code Node} using the values of the specified {@code Assignments}.
    *
    * @param assignments
    *           represents the values to assign to any variables for this particular evaluation of this {@code Node}
    * @return the result of evaluating this {@code Node} using the values of the specified {@code Assignments}
    */
   <T> T evaluate(Assignments assignments);

   /**
    * Returns the total number of nodes represented by this {@code Node} - including any child-nodes.
    *
    * @return the total number of nodes represented by this {@code Node} - including any child-nodes
    */
   int getNodeCount();

   /**
    * Returns a new {@code Node} resulting from replacing the {@code Node} at position {@code index} with the result of {@code replacement}.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by this object, that needs to be replaced
    * @param replacement
    *           the function to apply to the {@code Node} at {@code index} to determine the {@code Node} that should replace it
    * @return A new {@code Node} derived from replacing the {@code Node} at {@code index} with the result of {@code replacement}
    */
   Node replaceAt(int index, Function<Node, Node> replacement);

   /**
    * Returns a {@code Node} from the tree structure represented by this object.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by this object, that needs to be returned
    * @return the {@code Node} at {@code index} of the tree structure represented by this object
    */
   Node getAt(int index);

   int getDepth();

   Type getType();
}
