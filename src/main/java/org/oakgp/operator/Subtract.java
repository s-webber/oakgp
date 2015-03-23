package org.oakgp.operator;

import static org.oakgp.operator.Add.createAddArguments;
import static org.oakgp.operator.Add.getAddArguments;
import static org.oakgp.operator.Add.sameOperator;

import java.util.List;
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
		if (ZERO.equals(arg2)) {
			return Optional.of(arg1);
		} else if (arg1.equals(arg2)) {
			return Optional.of(ZERO);
		} else if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) arg2;
			Node firstArg = fn.getArguments().get(0);
			if (firstArg instanceof ConstantNode) {
				Operator fnOperator = fn.getOperator();
				if (fnOperator.getClass() == Add.class) {
					return Optional.of(new FunctionNode(this, Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) - ((int) firstArg.evaluate(null))), fn.getArguments().get(1))));
				} else if (fnOperator.getClass() == Subtract.class) {
					return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) - ((int) firstArg.evaluate(null))), fn.getArguments().get(1))));
				}
			}
		} else if (arg1 instanceof FunctionNode && arg2 instanceof FunctionNode) {
			FunctionNode fn1 = (FunctionNode) arg1;
			FunctionNode fn2 = (FunctionNode) arg2;
			if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode && sameOperator(Add.class, fn1, fn2)) {
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				FunctionNode newFunctionNode = new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(1)));
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(createConstant(((int) fn1Value) - ((int) fn2Value)),
						newFunctionNode)));
			} else if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode
					&& fn1.getOperator().getClass() == Add.class && fn2.getOperator().getClass() == Subtract.class) {
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				FunctionNode newFunctionNode = new FunctionNode(fn1.getOperator(), Arguments.createArguments(fn1.getArguments().get(1),
						fn2.getArguments().get(1)));
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(createConstant(((int) fn1Value) - ((int) fn2Value)),
						newFunctionNode)));
			}
		}

		if (arg1 instanceof FunctionNode && ((FunctionNode) arg1).getOperator().getClass() == Add.class) {
			List<Node> result = getAddArguments(arg1);
			if (result.contains(arg2)) {
				result.remove(arg2);
				return Optional.of(createAddArguments(result));
			}
		}

		if (arg1 instanceof FunctionNode && ((FunctionNode) arg1).getOperator().getClass() == Subtract.class) {
			FunctionNode fn = (FunctionNode) arg1;
			if (arg2.equals(fn.getArguments().get(1))) {
				return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(
						new FunctionNode(new Multiply(), Arguments.createArguments(createConstant(-2), arg2)), fn.getArguments().get(0))));
			}
		}

		if (arg1 instanceof FunctionNode) {
			if (!(arg2 instanceof FunctionNode)) {
				Node n = replace((FunctionNode) arg1, arg2, true);
				if (!n.equals(arg1)) {
					return Optional.of(n);
				}
			} else if (sameOperator(Subtract.class, (FunctionNode) arg2)) {
				FunctionNode fn2 = (FunctionNode) arg2;
				if (!(fn2.getArguments().get(1) instanceof FunctionNode)) {
					Node n = replace((FunctionNode) arg1, fn2.getArguments().get(1), true);
					if (!n.equals(arg1)) {
						return Optional.of(new FunctionNode(this, Arguments.createArguments(n, fn2.getArguments().get(0))));
					}
				}
				if (!(fn2.getArguments().get(0) instanceof FunctionNode)) {
					Node n = replace((FunctionNode) arg1, fn2.getArguments().get(0), true);
					if (!n.equals(arg1)) {
						return Optional.of(new FunctionNode(this, Arguments.createArguments(n,
								new FunctionNode(this, Arguments.createArguments(createConstant(0), fn2.getArguments().get(1))))));
					}
				}
			}
		}

		if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			return Optional.of(new FunctionNode(new Add(), Arguments.createArguments(arg1, createConstant(-((int) arg2.evaluate(null))))));
		}

		return Optional.empty();
	}

	public static Node replace(FunctionNode fn, Node nodeToReplace, boolean doReplace) {
		if (!(fn.getOperator() instanceof ArithmeticOperator)) {
			return fn;
		}

		if (sameOperator(Add.class, fn)) {
			Node newArg;
			if (doReplace) {
				newArg = createConstant(0);
			} else {
				newArg = new FunctionNode(new Multiply(), Arguments.createArguments(createConstant(2), nodeToReplace));
			}
			if (fn.getArguments().get(0).equals(nodeToReplace)) {
				return new FunctionNode(fn.getOperator(), Arguments.createArguments(newArg, fn.getArguments().get(1)));
			} else if (fn.getArguments().get(1).equals(nodeToReplace)) {
				return new FunctionNode(fn.getOperator(), Arguments.createArguments(fn.getArguments().get(0), newArg));
			}
		}
		if (sameOperator(Multiply.class, fn) && fn.getArguments().get(0) instanceof ConstantNode && fn.getArguments().get(1).equals(nodeToReplace)) {
			int inc = doReplace ? -1 : 1;
			return new FunctionNode(fn.getOperator(), Arguments.createArguments(createConstant((int) ((ConstantNode) fn.getArguments().get(0)).evaluate(null)
					+ inc), nodeToReplace));
		}
		if (sameOperator(Add.class, fn) || sameOperator(Subtract.class, fn)) {
			// doReplace = sameOperator(Add.class, fn);
			if (fn.getArguments().get(0) instanceof FunctionNode) {
				Node n = replace((FunctionNode) fn.getArguments().get(0), nodeToReplace, doReplace);
				if (!n.equals(fn.getArguments().get(0))) {
					return new FunctionNode(fn.getOperator(), Arguments.createArguments(n, fn.getArguments().get(1)));
				}
			}
			if (fn.getArguments().get(1) instanceof FunctionNode) {
				if (sameOperator(Subtract.class, fn)) {
					doReplace = !doReplace;
				}
				Node n = replace((FunctionNode) fn.getArguments().get(1), nodeToReplace, doReplace);
				if (!n.equals(fn.getArguments().get(1))) {
					return new FunctionNode(fn.getOperator(), Arguments.createArguments(fn.getArguments().get(0), n));
				}
			}
		}

		return fn;
	}
}