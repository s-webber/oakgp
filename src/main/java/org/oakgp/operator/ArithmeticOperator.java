package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;

abstract class ArithmeticOperator implements Operator {
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
