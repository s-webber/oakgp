package org.oakgp.operator;

import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

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
		Node arg1 = arguments.get(0);
		Node arg2 = arguments.get(1);

		if (new NodeComparator().compare(arg1, arg2) > 0) {
			return Optional.of(new FunctionNode(this, Arguments.createArguments(arg2, arg1)));
		} else if (ZERO.equals(arg1)) {
			return Optional.of(arg2);
		} else if (ZERO.equals(arg2)) {
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else if (arg1.equals(arg2)) {
			// x + x = 2 * x
			return Optional.of(times2(arg1));
		} else if (arg1 instanceof ConstantNode && ((int) arg1.evaluate(null)) < 0) {
			return Optional.of(new FunctionNode(new Subtract(), Arguments.createArguments(arg2, createConstant(-((int) arg1.evaluate(null))))));
		} else if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
			FunctionNode fn2 = (FunctionNode) arg2;
			if (fn2.getArguments().get(0) instanceof ConstantNode
					&& (fn2.getOperator().getClass() == Add.class || fn2.getOperator().getClass() == Subtract.class)) {
				int i1 = (int) arg1.evaluate(null);
				int i2 = (int) fn2.getArguments().get(0).evaluate(null);
				int result;
				Operator op = fn2.getOperator();
				if (fn2.getOperator().getClass() == Subtract.class) {
					result = i1 + i2;
				} else {
					result = i1 + i2;
				}
				return Optional.of(new FunctionNode(op, Arguments.createArguments(createConstant(result), fn2.getArguments().get(1))));
			}
		}

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
