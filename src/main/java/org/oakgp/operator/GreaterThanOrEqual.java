package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class GreaterThanOrEqual extends ComparisonOperator {
	public GreaterThanOrEqual() {
		super(true);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 >= arg2;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		Optional<Node> o = super.simplify(arguments);
		if (!o.isPresent()) {
			return Optional.of(new FunctionNode(new LessThanOrEqual(), Arguments.createArguments(arguments.get(1), arguments.get(0))));
		} else {
			return o;
		}
	}
}
