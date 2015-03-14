package org.oakgp.operator;

import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public final class If implements Operator {
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, BOOLEAN, INTEGER, INTEGER);
	private static final Integer TRUE = 1;

	@Override
	public int evaluate(Arguments arguments, Assignments assignments) {
		int index = getOutcomeArgumentIndex(arguments, assignments);
		return arguments.get(index).evaluate(assignments);
	}

	@Override
	public Signature getSignature() {
		return SIGNATURE;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		if (arguments.get(0) instanceof ConstantNode) {
			int index = getOutcomeArgumentIndex(arguments, null);
			return Optional.of(arguments.get(index));
		} else {
			return Optional.empty();
		}
	}

	private int getOutcomeArgumentIndex(Arguments arguments, Assignments assignments) {
		return arguments.get(0).evaluate(assignments) == TRUE ? 1 : 2;
	}
}
