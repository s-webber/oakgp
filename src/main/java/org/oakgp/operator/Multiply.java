package org.oakgp.operator;

import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

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
		Node arg1 = arguments.get(0);
		Node arg2 = arguments.get(1);

		if (new NodeComparator().compare(arg1, arg2) > 0) {
			return Optional.of(new FunctionNode(this, Arguments.createArguments(arg2, arg1)));
		} else if (ZERO.equals(arg1)) {
			return Optional.of(ZERO);
		} else if (ZERO.equals(arg2)) {
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else if (ONE.equals(arg1)) {
			return Optional.of(arg2);
		} else if (ONE.equals(arg2)) {
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else {
			FunctionNode in = new FunctionNode(this, Arguments.createArguments(arg1, arg2));
			Node out = new ArithmeticExpressionSimplifier().simplify(in);
			if (!in.equals(out)) {
				assertEvaluateToSameResult(in, out);
				return Optional.of(out);
			} else {
				return Optional.empty();
			}
		}
	}
}
