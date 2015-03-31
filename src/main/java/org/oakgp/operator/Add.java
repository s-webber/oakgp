package org.oakgp.operator;

import static org.oakgp.operator.ArithmeticExpressionSimplifier.ZERO;
import static org.oakgp.operator.ArithmeticExpressionSimplifier.createConstant;
import static org.oakgp.operator.ArithmeticExpressionSimplifier.isAddOrSubtract;
import static org.oakgp.operator.ArithmeticExpressionSimplifier.multiplyByTwo;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
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
	public Node simplify(Arguments arguments) {
		Node arg1 = arguments.get(0);
		Node arg2 = arguments.get(1);

		if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
			// as for addition the order of the arguments is not important, order arguments in a consistent way
			// e.g. (+ v1 1) -> (+ 1 v1)
			return new FunctionNode(this, arg2, arg1);
		} else if (ZERO.equals(arg1)) {
			// anything plus zero is itself
			// e.g. (+ 0 v0) -> v0
			return arg2;
		} else if (ZERO.equals(arg2)) {
			// should never get here to to earlier ordering of arguments
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else if (arg1.equals(arg2)) {
			// anything plus itself is equal to itself multiplied by two
			// e.g. (+ x x) -> (* 2 x)
			return multiplyByTwo(arg1);
		} else if (arg1 instanceof ConstantNode && ((int) arg1.evaluate(null)) < 0) {
			// convert addition of negative numbers to subtraction
			// e.g. (+ -3 x) -> (- x 3)
			return new FunctionNode(new Subtract(), arg2, createConstant(-((int) arg1.evaluate(null))));
		} else if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			// should never get here as, due to the earlier ordering of arguments,
			// the only time the second argument will be a constant is when the first argument is also a constant -
			// in which case it would of already been simplified to the result of the addition.
			// e.g. (+ 2 7) would have already been simplified to 9 before it got this far
			throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
		} else if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
			FunctionNode fn2 = (FunctionNode) arg2;
			if (fn2.getArguments().get(0) instanceof ConstantNode && isAddOrSubtract(fn2.getOperator())) {
				int i1 = (int) arg1.evaluate(null);
				int i2 = (int) fn2.getArguments().get(0).evaluate(null);
				Operator op = fn2.getOperator();
				return new FunctionNode(op, createConstant(i1 + i2), fn2.getArguments().get(1));
			}
		}

		return ArithmeticExpressionSimplifier.simplify(this, arg1, arg2);
	}
}
