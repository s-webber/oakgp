package org.oakgp.operator;

/** Performs addition. */
public final class Add extends ArithmeticOperator {
	/**
	 * Returns the result of adding the two elements of the specified arguments.
	 *
	 * @return the result of adding {@code arg1} and {@code arg2}
	 */
	@Override
	protected int evaluate(int arg1, int arg2) {
		return arg1 + arg2;
	}
}
