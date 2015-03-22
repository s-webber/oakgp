package org.oakgp;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.selector.NodeSelectorFactory;

/** Creates a new generation of {@code Node} instances evolved from an existing generation. */
public final class GenerationEvolver {
	private final int elitismSize;
	private final NodeSelectorFactory selectorFactory;
	private final Map<NodeEvolver, Long> nodeEvolvers;

	public GenerationEvolver(int elitismSize, NodeSelectorFactory selectorFactory, Map<NodeEvolver, Long> nodeEvolvers) {
		this.elitismSize = elitismSize;
		this.selectorFactory = selectorFactory;
		this.nodeEvolvers = nodeEvolvers;
	}

	/**
	 * Returns a new generation of {@code Node} instances evolved from the specified existing generation.
	 *
	 * @param oldGeneration
	 *            the existing generation to use as a basis for evolving a new generation
	 * @return a new generation of {@code Node} instances evolved from the existing generation specified by {@code oldGeneration}
	 */
	public Collection<Node> process(List<RankedCandidate> oldGeneration) {
		NodeSelector selector = selectorFactory.getSelector(oldGeneration);
		List<Node> newGeneration = new ArrayList<>();

		final int elitismSizeForGeneration = min(elitismSize, oldGeneration.size());
		for (int i = 0; i < elitismSizeForGeneration; i++) {
			newGeneration.add(oldGeneration.get(i).getNode());
		}

		for (Map.Entry<NodeEvolver, Long> e : nodeEvolvers.entrySet()) {
			NodeEvolver nodeEvolver = e.getKey();
			long count = e.getValue();
			for (int i = 0; i < count; i++) {
				newGeneration.add(nodeEvolver.evolve(selector));
			}
		}

		return newGeneration;
	}
}
