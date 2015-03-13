package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;

/** Performs subtraction. */
public final class Subtract implements Operator {
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, INTEGER, INTEGER);

	/**
	 * Returns the result of subtracting the second element of the specified arguments from the first.
	 *
	 * @return the result of subtracting the second element of {@code arguments} from the first
	 */
	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) - arguments.get(1).evaluate(assignments);
	}

	@Override
	public Signature getSignature() {
		return SIGNATURE;
	}
}