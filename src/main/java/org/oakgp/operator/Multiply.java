package org.oakgp.operator;

import org.oakgp.Arguments;
import org.oakgp.Assignments;

/** Performs multiplication. */
public final class Multiply implements Operator {
	/**
	 * Returns the result of multiplying the two elements of the specified arguments.
	 *
	 * @return the result of multiplying the two elements of {@code arguments}
	 */
	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) * arguments.get(1).evaluate(assignments);
	}
}
