package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.Node;

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

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		if (ZERO.equals(arguments.get(0))) {
			return Optional.of(arguments.get(1));
		} else if (ZERO.equals(arguments.get(1))) {
			return Optional.of(arguments.get(0));
		} else {
			return Optional.empty();
		}
	}
}
