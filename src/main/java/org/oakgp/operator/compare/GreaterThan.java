package org.oakgp.operator;

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
	public Node simplify(Arguments arguments) {
		Node simplifiedVersion = super.simplify(arguments);
		if (simplifiedVersion == null) {
			return new FunctionNode(new LessThan(), arguments.get(1), arguments.get(0));
		} else {
			return simplifiedVersion;
		}
	}
}
