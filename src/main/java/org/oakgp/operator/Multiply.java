package org.oakgp.operator;

import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
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
			if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
				FunctionNode fn = (FunctionNode) arg2;
				Operator o = fn.getOperator();
				Arguments args = fn.getArguments();
				Node fnArg1 = args.get(0);
				Node fnArg2 = args.get(1);
				if (fnArg1 instanceof ConstantNode) {
					int i1 = (int) arg1.evaluate(null);
					int i2 = (int) fnArg1.evaluate(null);
					int result;
					if (o.getClass() == Add.class || o.getClass() == Subtract.class) {
						result = i1 * i2;
						Node n = new FunctionNode(o, Arguments.createArguments(createConstant(result),
								new FunctionNode(this, Arguments.createArguments(arg1, fnArg2))));
						return Optional.of(n);
					} else if (o.getClass() == Multiply.class) {
						return Optional.of(new FunctionNode(this, Arguments.createArguments(createConstant(i1 * i2), fnArg2)));
					} else {
						throw new IllegalArgumentException();
					}
				} else {
					if (o.getClass() == Add.class || o.getClass() == Subtract.class) {
						Node n = new FunctionNode(o, Arguments.createArguments(new FunctionNode(this, Arguments.createArguments(arg1, fnArg1)),
								new FunctionNode(this, Arguments.createArguments(arg1, fnArg2))));
						return Optional.of(n);
					}
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
}
