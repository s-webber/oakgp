package org.oakgp.operator;

/** Performs multiplication. */
public final class Multiply extends ArithmeticOperator {
	/**
	 * Returns the result of multiplying the two elements of the specified arguments.
	 *
	 * @return the result of multiplying {@code arg1} and {@code arg2}
	 */
	@Override
	protected int evaluate(int arg1, int arg2) {
		return arg1 * arg2;
	}
}
