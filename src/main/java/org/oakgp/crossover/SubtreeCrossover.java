package org.oakgp.crossover;

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
		int to = selectCrossoverPoint(parent1);
		int from = random.nextInt(parent2.getNodeCount());
		Node replacementSubTree = parent2.getAt(from);
		return parent1.replaceAt(to, t -> {
			if (t.getType() == replacementSubTree.getType()) {
				return replacementSubTree;
			} else {
				return t;
			}
		});
	}

	private int selectCrossoverPoint(Node parent1) {
		int nodeCount = parent1.getNodeCount();
		if (nodeCount == 1) {
			return 0;
		} else {
			return random.nextInt(nodeCount - 1); // -1 to avoid selecting root node
		}
	}
}
