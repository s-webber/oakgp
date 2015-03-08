package org.oakgp;

import org.oakgp.operator.Operator;
import org.oakgp.util.Random;

/** Represents the set of possible {@code Operator} implementations to use during a genetic programming run. */
public final class FunctionSet {
	private final Random random;
	private final Operator[] operators;

	// TODO defensive copy of operators?
	public FunctionSet(Random random, Operator[] operators) {
		this.random = random;
		this.operators = operators;
	}

	/**
	 * Returns a randomly selected {@code Operator}.
	 *
	 * @return a randomly selected {@code Operator}
	 */
	public Operator next() {
		return operators[random.nextInt(operators.length)];
	}

	/**
	 * Returns a randomly selected {@code Operator} that is not the same as the specified {@code Operator}.
	 *
	 * @param current
	 *            the current {@code Operator} that the returned result should be an alternative to (i.e. not the same as)
	 * @return a randomly selected {@code Operator} that is not the same as the specified {@code Operator}
	 */
	public Operator nextAlternative(Operator current) {
		int randomIndex = random.nextInt(operators.length);
		Operator next = operators[randomIndex];
		if (next == current) {
			int secondRandomIndex = random.nextInt(operators.length - 1);
			if (secondRandomIndex >= randomIndex) {
				return operators[secondRandomIndex + 1];
			} else {
				return operators[secondRandomIndex];
			}
		} else {
			return next;
		}
	}
}
