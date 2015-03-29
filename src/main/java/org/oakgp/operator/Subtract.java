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
			// anything minus itself is zero
			// e.g. (- x x) -> 0
			return Optional.of(ZERO);
		} else if (ZERO.equals(arg2)) {
			// anything minus zero is itself
			// e.g. (- x 0) -> x
			return Optional.of(arg1);
		} else if (ZERO.equals(arg1) && isSubtract(arg2)) {
			// simplify "zero minus ?" expressions
			// e.g. (- 0 (- x y) -> (- y x)
			FunctionNode fn2 = (FunctionNode) arg2;
			Arguments fn2Arguments = fn2.getArguments();
			return Optional.of(new FunctionNode(this, Arguments.createArguments(fn2Arguments.get(1), fn2Arguments.get(0))));
		} else if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			// convert double negatives to addition
			// e.g. (- x -1) -> (+ 1 x)
			return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(negate(arg2), arg1)));
		} else {
			if (arg2 instanceof FunctionNode) {
				FunctionNode fn = (FunctionNode) arg2;
				Operator o = fn.getOperator();
				Arguments args = fn.getArguments();
				Node fnArg1 = args.get(0);
				Node fnArg2 = args.get(1);
				if (fnArg1 instanceof ConstantNode && isMultiply(o)) {
					if (ZERO.equals(arg1)) {
						int i = (int) fnArg1.evaluate(null);
						FunctionNode value = new FunctionNode(o, Arguments.createArguments(createConstant(-i), fnArg2));
						return Optional.of(value);
					} else if ((int) fnArg1.evaluate(null) < 0) {
						FunctionNode value = new FunctionNode(new Add(), Arguments.createArguments(arg1,
								new FunctionNode(o, Arguments.createArguments(createConstant(-(int) fnArg1.evaluate(null)), fnArg2))));
						return Optional.of(value);
					}
				} else if (ZERO.equals(arg1) && isAdd(o)) {
					// (- 0 (+ v0 v1) -> (+ (0 - v0) (0 - v1))
					FunctionNode value = new FunctionNode(o, Arguments.createArguments(negate(fnArg1), negate(fnArg2)));
					return Optional.of(value);
				} else if (arg1 instanceof ConstantNode && fnArg1 instanceof ConstantNode && isSubtract(fn)) {
					int i1 = (int) arg1.evaluate(null);
					int i2 = (int) fnArg1.evaluate(null);
					int result;
					Operator op = fn.getOperator();
					if (i1 == 0) {
						// (- 0 (- 0 v0)) -> v0
						// (- 0 (- 7 v0)) -> (- v0 7)
						return Optional.of(new FunctionNode(op, Arguments.createArguments(fnArg2, fnArg1)));
					} else if (i2 == 0) {
						// (- 1 (- 0 v0)) -> (+ 1 v0)
						return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(arg1, fnArg2)));
					} else {
						result = i1 - i2;
						return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(createConstant(result), fnArg2)));
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