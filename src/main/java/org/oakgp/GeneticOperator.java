package org.oakgp;

import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;

/** Creates new {@code Node} instances evolved from existing instances. */
@FunctionalInterface
public interface GeneticOperator {
   /**
    * Returns a new {@code Node} evolved from existing instances.
    *
    * @param selector
    *           used to select the existing instances to use as a basis for evolving a new instance
    * @return a new {@code Node} evolved from existing instances obtained from {@code selector}
    */
   Node evolve(NodeSelector selector);
}
