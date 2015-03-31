package org.oakgp.operator.choice;

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Operator;

public final class If implements Operator {
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, BOOLEAN, INTEGER, INTEGER);

	@Override
	public Object evaluate(Arguments arguments, Assignments assignments) {
		int index = getOutcomeArgumentIndex(arguments, assignments);
		return arguments.get(index).evaluate(assignments);
	}

	@Override
	public Signature getSignature() {
		return SIGNATURE;
	}

	@Override
	public Node simplify(Arguments arguments) {
		if (arguments.get(1).equals(arguments.get(2))) {
			return arguments.get(1);
		} else if (arguments.get(0) instanceof ConstantNode) {
			int index = getOutcomeArgumentIndex(arguments, null);
			return arguments.get(index);
		} else {
			return null;
		}
	}

	private int getOutcomeArgumentIndex(Arguments arguments, Assignments assignments) {
		return TRUE.equals(arguments.get(0).evaluate(assignments)) ? 1 : 2;
	}
}
