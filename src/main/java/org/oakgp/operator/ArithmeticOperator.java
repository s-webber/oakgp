package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;

abstract class ArithmeticOperator implements Operator {
	public static final ConstantNode ZERO = new ConstantNode(0); // TODO move or make private
	public static final ConstantNode ONE = new ConstantNode(1); // TODO move or make private
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, INTEGER, INTEGER);

	@Override
	public final Object evaluate(Arguments arguments, Assignments assignments) {
		int i1 = (int) arguments.get(0).evaluate(assignments);
		int i2 = (int) arguments.get(1).evaluate(assignments);
		return evaluate(i1, i2);
	}

	protected abstract int evaluate(int arg1, int arg2);

	@Override
	public final Signature getSignature() {
		return SIGNATURE;
	}
}
