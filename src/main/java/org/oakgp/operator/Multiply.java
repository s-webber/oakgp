package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.Node;

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

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		if (ZERO.equals(arguments.get(0)) || ZERO.equals(arguments.get(1))) {
			return Optional.of(ZERO);
		} else if (ONE.equals(arguments.get(0))) {
			return Optional.of(arguments.get(1));
		} else if (ONE.equals(arguments.get(1))) {
			return Optional.of(arguments.get(0));
		} else {
			return Optional.empty();
		}
	}
}
