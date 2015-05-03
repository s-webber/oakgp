package org.oakgp;

import org.oakgp.function.Function;
import org.oakgp.node.Node;

/** Represents the range of possible functions and terminal nodes to use during a genetic programming run. */
public interface PrimitiveSet {
   boolean hasTerminals(Type type);

   boolean hasFunctions(Type type);

   /**
    * Returns a randomly selected terminal node.
    *
    * @return a randomly selected terminal node
    */
   public Node nextTerminal(Type type);

   /**
    * Returns a randomly selected terminal node that is not the same as the specified {@code Node}.
    *
    * @param current
    *           the current {@code Node} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected terminal node that is not the same as the specified {@code Node}
    */
   Node nextAlternativeTerminal(Node current);

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   Function nextFunction(Type type);

   /**
    * Returns a randomly selected {@code Function} that is not the same as the specified {@code Function}.
    *
    * @param current
    *           the current {@code Function} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected {@code Function} that is not the same as the specified {@code Function}
    */
   Function nextAlternativeFunction(Function current);
}
