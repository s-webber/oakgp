package org.oakgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelector;
import org.oakgp.selector.NodeSelectorFactory;

/** Creates a new generation of {@code Node} instances evolved from an existing generation. */
public final class GenerationEvolver {
	private final NodeSelectorFactory selectorFactory;
	private final Map<NodeEvolver, Long> nodeEvolvers;

	public GenerationEvolver(NodeSelectorFactory selectorFactory, Map<NodeEvolver, Long> nodeEvolvers) {
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
	public List<Node> process(List<RankedCandidate> oldGeneration) {
		NodeSelector selector = selectorFactory.getSelector(oldGeneration);
		List<Node> newGeneration = new ArrayList<>();

		// keep best 2 (elitism) TODO make this value configurable
		newGeneration.add(oldGeneration.get(0).getNode());
		newGeneration.add(oldGeneration.get(1).getNode());

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
