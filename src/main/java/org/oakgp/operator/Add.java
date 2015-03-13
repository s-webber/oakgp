package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;

/** Performs addition. */
public final class Add implements Operator {
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, INTEGER, INTEGER);

	/**
	 * Returns the result of adding the two elements of the specified arguments.
	 *
	 * @return the result of adding the two elements of {@code arguments}
	 */
	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) + arguments.get(1).evaluate(assignments);
	}

	@Override
	public Signature getSignature() {
		return SIGNATURE;
	}
}
