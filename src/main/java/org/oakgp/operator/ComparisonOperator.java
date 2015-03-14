package org.oakgp.operator;

import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

abstract class ComparisonOperator implements Operator {
	private static final Integer TRUE = 1;
	private static final Integer FALSE = 0;
	private static final ConstantNode TRUE_NODE = new ConstantNode(TRUE);
	private static final ConstantNode FALSE_NODE = new ConstantNode(FALSE);
	private static final Signature SIGNATURE = Signature.createSignature(BOOLEAN, INTEGER, INTEGER);

	private boolean equalsIsTrue;

	protected ComparisonOperator(boolean equalsIsTrue) {
		this.equalsIsTrue = equalsIsTrue;
	}

	@Override
	public final int evaluate(Arguments arguments, Assignments assignments) {
		if (evaluate(arguments.get(0).evaluate(assignments), arguments.get(1).evaluate(assignments))) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	protected abstract boolean evaluate(int arg1, int arg2);

	@Override
	public final Signature getSignature() {
		return SIGNATURE;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		if (arguments.get(0).equals(arguments.get(1))) {
			return Optional.of(equalsIsTrue ? TRUE_NODE : FALSE_NODE);
		} else {
			return Optional.empty();
		}
	}
}
