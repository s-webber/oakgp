package org.oakgp;

import org.oakgp.node.Node;

/**
 * Creates tree data structures.
 * <p>
 * Can be used to create randomly generate the initial population of a genetic programming run.
 */
public interface TreeGenerator {
   /**
    * Constructs a new tree data structure.
    *
    * @param type
    *           the required return type of the tree
    * @param depth
    *           the maximum depth of any nodes of the tree
    * @return the newly created tree data structure
    */
   Node generate(Type type, int depth);
}
