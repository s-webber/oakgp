package org.oakgp.operator;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.Node;

/** Represents an operation. */
public interface Operator {
	/**
	 * Returns the result of applying this operation to the specified {@code Arguments} and {@code Assignments}.
	 *
	 * @param arguments
	 *            represents the arguments to apply to the operation
	 * @param assignments
	 *            represents values assigned to variables belonging to {@code arguments}
	 * @return the result of applying this operation to the {@code arguments} and {@code assignments}
	 */
	Object evaluate(Arguments arguments, Assignments assignments);

	Signature getSignature();

	default Optional<Node> simplify(Arguments arguments) {
		return Optional.empty();
	}
}
