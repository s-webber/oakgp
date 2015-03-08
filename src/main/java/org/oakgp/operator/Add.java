package org.oakgp.operator;

import org.oakgp.Arguments;
import org.oakgp.Assignments;

/** Performs addition. */
public final class Add implements Operator {
	/**
	 * Returns the result of adding the two elements of the specified arguments.
	 *
	 * @return the result of adding the two elements of {@code arguments}
	 */
	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) + arguments.get(1).evaluate(assignments);
	}
}
