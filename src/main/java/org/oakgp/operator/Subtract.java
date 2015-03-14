package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.Node;

/** Performs subtraction. */
public final class Subtract extends ArithmeticOperator {
	/**
	 * Returns the result of subtracting the second element of the specified arguments from the first.
	 *
	 * @return the result of subtracting {@code arg2} from {@code arg1}
	 */
	@Override
	protected int evaluate(int arg1, int arg2) {
		return arg1 - arg2;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		if (ZERO.equals(arguments.get(1))) {
			return Optional.of(arguments.get(0));
		} else if (arguments.get(0).equals(arguments.get(1))) {
			return Optional.of(ZERO);
		} else {
			return Optional.empty();
		}
	}
}