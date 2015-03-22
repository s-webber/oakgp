package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class GreaterThan extends ComparisonOperator {
	public GreaterThan() {
		super(false);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 > arg2;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		Optional<Node> o = super.simplify(arguments);
		if (!o.isPresent()) {
			return Optional.of(new FunctionNode(new LessThan(), Arguments.createArguments(arguments.get(1), arguments.get(0))));
		} else {
			return o;
		}
	}
}
