package org.oakgp;

import java.util.Collection;
import java.util.List;

import org.oakgp.node.Node;

/** Ranks and sorts the fitness of {@code Node}. */
@FunctionalInterface
public interface GenerationProcessor {
   /**
    * Returns the sorted result of evaluating the fitness of each of the specified nodes.
    *
    * @param input
    *           the {@code Node} instances to evaluate the fitness of
    * @return a {@code List} of {@code RankedCandidate} - one for each {@code Node} specified in {@code input} - sorted by fitness
    */
   List<RankedCandidate> process(Collection<Node> input);
}
