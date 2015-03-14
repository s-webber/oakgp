package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;

abstract class ArithmeticOperator implements Operator {
	public static final ConstantNode ZERO = new ConstantNode(0);
	public static final ConstantNode ONE = new ConstantNode(1);
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, INTEGER, INTEGER);

	@Override
	public final int evaluate(Arguments arguments, Assignments assignments) {
		return evaluate(arguments.get(0).evaluate(assignments), arguments.get(1).evaluate(assignments));
	}

	protected abstract int evaluate(int arg1, int arg2);

	@Override
	public final Signature getSignature() {
		return SIGNATURE;
	}
}
