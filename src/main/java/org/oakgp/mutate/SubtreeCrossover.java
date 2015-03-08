package org.oakgp.mutate; // TODO move to org.oakgp.crossover?

import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;

/** Performs subtree crossover. */
public final class SubtreeCrossover implements NodeEvolver {
	private final Random random;

	public SubtreeCrossover(Random random) {
		this.random = random;
	}

	@Override
	public Node evolve(NodeSelector selector) {
		Node parent1 = selector.next();
		Node parent2 = selector.next();
		// TODO don't allow removal of root node?
		int to = random.nextInt(parent1.getNodeCount());
		int from = random.nextInt(parent2.getNodeCount());
		return parent1.replaceAt(to, t -> parent2.getAt(from));
	}
}
