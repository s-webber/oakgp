package org.oakgp.mutate;

import org.oakgp.FunctionSet;
import org.oakgp.NodeEvolver;
import org.oakgp.TerminalSet;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Operator;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;

/** Performs mutation (also known as node replacement mutation). */
public final class PointMutation implements NodeEvolver {
	private final Random random;
	private final FunctionSet functionSet;
	private final TerminalSet terminalSet;

	public PointMutation(Random random, FunctionSet functionSet, TerminalSet terminalSet) {
		this.random = random;
		this.functionSet = functionSet;
		this.terminalSet = terminalSet;
	}

	@Override
	public Node evolve(NodeSelector selector) {
		Node root = selector.next();
		int mutationPoint = random.nextInt(root.getNodeCount());
		return root.replaceAt(mutationPoint, node -> {
			if (node instanceof FunctionNode) {
				FunctionNode functionNode = (FunctionNode) node;
				Operator operator = functionSet.nextAlternative(functionNode.getOperator());
				return new FunctionNode(operator, functionNode.getArguments());
			} else {
				return terminalSet.nextAlternative(node);
			}
		});
	}
}
