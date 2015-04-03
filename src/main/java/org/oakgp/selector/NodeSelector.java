package org.oakgp.selector;

import org.oakgp.node.Node;

/**
 * Used to obtain {@code Node} instances.
 * <p>
 * The strategy to determine what is returned, and in what order, will depend on the specific implementation of {@code NodeSelector} that is being used.
 */
@FunctionalInterface
public interface NodeSelector {
   /**
    * Returns a {@code Node}.
    *
    * @return a {@code Node}
    */
   Node next();
}
