package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

public final class NotEqual extends ComparisonOperator {
	public NotEqual() {
		super(false);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 != arg2;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		Optional<Node> o = super.simplify(arguments);
		if (!o.isPresent() && new NodeComparator().compare(arguments.get(0), arguments.get(1)) > 0) {
			return Optional.of(new FunctionNode(this, arguments.get(1), arguments.get(0)));
		} else {
			return o;
		}
	}
}
