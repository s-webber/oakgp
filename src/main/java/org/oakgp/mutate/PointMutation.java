package org.oakgp.mutate;

import org.oakgp.FunctionSet;
import org.oakgp.NodeEvolver;
import org.oakgp.TerminalSet;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Operator;
import org.oakgp.selector.NodeSelector;

/** Performs mutation (also known as node replacement mutation). */
public final class PointMutation implements NodeEvolver {
	private final FunctionSet functionSet;
	private final TerminalSet terminalSet;

	public PointMutation(FunctionSet functionSet, TerminalSet terminalSet) {
		this.functionSet = functionSet;
		this.terminalSet = terminalSet;
	}

	@Override
	public Node evolve(NodeSelector selector) {
		// TODO don't always change the root node - instead randomly select any node in the tree
		Node original = selector.next();
		if (original instanceof FunctionNode) {
			FunctionNode n = (FunctionNode) original;
			Operator f = functionSet.nextAlternative(n.getOperator());
			return new FunctionNode(f, n.getArguments());
		} else {
			return terminalSet.nextAlternative(original);
		}
	}
}
