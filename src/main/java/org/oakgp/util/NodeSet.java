package org.oakgp.util;

import java.util.HashSet;

import org.oakgp.NodeSimplifier;
import org.oakgp.node.Node;

public class NodeSet extends HashSet<Node> {
	private static final NodeSimplifier SIMPLIFIER = new NodeSimplifier();

	@Override
	public boolean add(Node n) {
		return super.add(SIMPLIFIER.simplify(n));
	}
}
