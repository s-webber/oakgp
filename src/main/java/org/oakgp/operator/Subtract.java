package org.oakgp.operator;

import org.oakgp.Arguments;
import org.oakgp.Assignments;

/** Performs subtraction. */
public final class Subtract implements Operator {
	/**
	 * Returns the result of subtracting the second element of the specified arguments from the first.
	 *
	 * @return the result of subtracting the second element of {@code arguments} from the first
	 */
	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) - arguments.get(1).evaluate(assignments);
	}
}