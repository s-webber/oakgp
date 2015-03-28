package org.oakgp.operator;

import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
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
		Node arg1 = arguments.get(0);
		Node arg2 = arguments.get(1);
		if (arg1.equals(arg2)) {
			return Optional.of(ZERO);
		} else if (ZERO.equals(arg2)) {
			return Optional.of(arg1);
		} else if (ZERO.equals(arg1) && isSubtract(arg2)) {
			FunctionNode fn2 = (FunctionNode) arg2;
			Arguments fn2Arguments = fn2.getArguments();
			return Optional.of(new FunctionNode(this, Arguments.createArguments(fn2Arguments.get(1), fn2Arguments.get(0))));
		} else if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(arg1, createConstant(-((int) arg2.evaluate(null))))));
		} else {
			if (ZERO.equals(arg1) && arg2 instanceof FunctionNode && ((FunctionNode) arg2).getOperator().getClass() == Multiply.class) {
				FunctionNode fn = (FunctionNode) arg2;
				Operator o = fn.getOperator();
				Arguments args = fn.getArguments();
				Node fnArg1 = args.get(0);
				Node fnArg2 = args.get(1);
				if (fnArg1 instanceof ConstantNode) {
					int i = (int) fnArg1.evaluate(null);
					return Optional.of(new FunctionNode(o, Arguments.createArguments(createConstant(-i), fnArg2)));
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

	private boolean isSubtract(Node arg2) { // TODO share
		return arg2 instanceof FunctionNode && ((FunctionNode) arg2).getOperator().getClass() == Subtract.class;
	}
}