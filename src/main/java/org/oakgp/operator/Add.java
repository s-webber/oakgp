package org.oakgp.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;
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
		boolean shuffled = false;

		if (new NodeComparator().compare(arg1, arg2) > 0) {
			Node tmp = arg1;
			arg1 = arg2;
			arg2 = tmp;
			shuffled = true;
		}

		if (ZERO.equals(arg1)) {
			return Optional.of(arg2);
		} else if (ZERO.equals(arg2)) {
			throw new IllegalArgumentException();
			// return Optional.of(arg1);
		} else if (arg1.equals(arg2)) {
			// x + x = 2 * x
			return Optional.of(times2(arg1));
		} else if (arg1 instanceof FunctionNode && arg2 instanceof FunctionNode) {
			FunctionNode fn1 = (FunctionNode) arg1;
			FunctionNode fn2 = (FunctionNode) arg2;
			if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode
					&& (sameOperator(Add.class, fn1) || sameOperator(Subtract.class, fn1))
					&& (sameOperator(Add.class, fn2) || sameOperator(Subtract.class, fn2))) {
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				Operator operator1 = fn1.getOperator();// sameOperator(Subtract.class, fn1, fn2) ? fn1.getOperator() : this;
				Operator operator2;
				if (sameOperator(Add.class, fn1) && sameOperator(Add.class, fn2)) {
					operator2 = this;
				} else if (sameOperator(Subtract.class, fn1) && sameOperator(Add.class, fn2)) {
					operator2 = new Subtract();
				} else if (sameOperator(Add.class, fn1) && sameOperator(Subtract.class, fn2)) {
					operator2 = new Subtract();
				} else {
					operator2 = this;
				}
				return Optional.of(new FunctionNode(operator1, Arguments.createArguments(createConstant(((int) fn1Value) + ((int) fn2Value)), new FunctionNode(
						operator2, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(1))))));
			} else if (fn1.getArguments().get(1) instanceof ConstantNode && fn2.getArguments().get(1) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				throw new IllegalArgumentException();
				// Object fn1Value = fn1.getArguments().get(1).evaluate(null);
				// Object fn2Value = fn2.getArguments().get(1).evaluate(null);
				// return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(createConstant(((int) fn1Value) + ((int) fn2Value)),
				// new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(0), fn2.getArguments().get(0))))));
			} else if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(1) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				// throw new IllegalArgumentException();
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(1).evaluate(null);
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(createConstant(((int) fn1Value) + ((int) fn2Value)),
						new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(0))))));
			} else if (fn1.getArguments().get(1) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				// throw new IllegalArgumentException();
				Object fn1Value = fn1.getArguments().get(1).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				return Optional.of(new FunctionNode(this, Arguments.createArguments(createConstant(((int) fn1Value) + ((int) fn2Value)), new FunctionNode(this,
						Arguments.createArguments(fn1.getArguments().get(0), fn2.getArguments().get(1))))));
			} else if (fn1.getArguments().get(0).equals(fn2.getArguments().get(1)) && sameOperator(Add.class, fn1, fn2)) {
				return Optional.of(new FunctionNode(this, Arguments.createArguments(
						fn1.getArguments().get(1),
						new FunctionNode(this, Arguments.createArguments(fn2.getArguments().get(0),
								new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(0), fn2.getArguments().get(1))))))));
			} else if (fn1.getArguments().get(1).equals(fn2.getArguments().get(0)) && sameOperator(Add.class, fn1, fn2)) {
				return Optional.of(new FunctionNode(this, Arguments.createArguments(
						fn1.getArguments().get(0),
						new FunctionNode(this, Arguments.createArguments(fn2.getArguments().get(1),
								new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(0))))))));
			}
		} else if (arg2 instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) arg2;
			Operator fnOperator = fn.getOperator();
			Node firstArg = fn.getArguments().get(0);
			Node secondArg = fn.getArguments().get(1);
			if (fnOperator.getClass() == Multiply.class && firstArg instanceof ConstantNode && arg1.equals(secondArg)) {
				return Optional.of(new FunctionNode(fnOperator, Arguments.createArguments(createConstant(((int) firstArg.evaluate(null)) + 1), secondArg)));
			} else if (arg1 instanceof ConstantNode && firstArg instanceof ConstantNode) {
				if (fnOperator.getClass() == Add.class) {
					return Optional.of(new FunctionNode(fnOperator, Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) + ((int) firstArg.evaluate(null))), secondArg)));
				} else if (fnOperator.getClass() == Subtract.class) {
					return Optional.of(new FunctionNode(fnOperator, Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) + ((int) firstArg.evaluate(null))), secondArg)));
				}
			} else if (arg1 instanceof ConstantNode && secondArg instanceof ConstantNode) {
				if (fnOperator.getClass() == Add.class) {
					return Optional.of(new FunctionNode(this, Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) + ((int) secondArg.evaluate(null))), firstArg)));
				} else if (fnOperator.getClass() == Subtract.class) {
					return Optional.of(new FunctionNode(this, Arguments.createArguments(
							createConstant(((int) arg1.evaluate(null)) - ((int) secondArg.evaluate(null))), firstArg)));
				}
			}
		}

		if (arg2 instanceof FunctionNode && ((FunctionNode) arg2).getOperator().getClass() == Add.class) {
			List<Node> result = getAddArguments(arg2);
			if (result.size() < 2) {
				throw new RuntimeException((arg2 instanceof FunctionNode) + " " + new NodeWriter().writeNode(arg2) + "<");
			}
			if (result.contains(arg1)) {
				result.remove(arg1);
				return Optional.of(new FunctionNode(this, Arguments.createArguments(times2(arg1), createAddArguments(result))));
			}
		}

		if (arg2 instanceof FunctionNode && ((FunctionNode) arg2).getOperator().getClass() == Subtract.class) {
			FunctionNode fn = (FunctionNode) arg2;
			Node newFirstArg = new FunctionNode(this, Arguments.createArguments(arg1, fn.getArguments().get(0)));
			return Optional.of(new FunctionNode(fn.getOperator(), Arguments.createArguments(newFirstArg, fn.getArguments().get(1))));
		}

		if (arg1 instanceof ConstantNode && ((int) arg1.evaluate(null)) < 0) {
			return Optional.of(new FunctionNode(new Subtract(), Arguments.createArguments(arg2, createConstant(-((int) arg1.evaluate(null))))));
		}
		if (arg2 instanceof ConstantNode && ((int) arg2.evaluate(null)) < 0) {
			throw new IllegalArgumentException();
			// return Optional.of(new FunctionNode(new Subtract(), Arguments.createArguments(arg1, createConstant(-((int) arg2.evaluate(null))))));
		}

		return shuffled ? Optional.of(new FunctionNode(this, Arguments.createArguments(arg1, arg2))) : Optional.empty();
	}

	private FunctionNode times2(Node arg) {
		return new FunctionNode(new Multiply(), Arguments.createArguments(createConstant(2), arg));
	}

	static boolean sameOperator(Class<? extends Operator> operatorClass, FunctionNode fn1, FunctionNode fn2) {
		return sameOperator(operatorClass, fn1) && sameOperator(operatorClass, fn2);
	}

	static boolean sameOperator(Class<? extends Operator> operatorClass, FunctionNode fn1) {
		return fn1.getOperator().getClass() == operatorClass;
	}

	static Node createAddArguments(List<Node> result) {
		if (result.size() == 1) {
			return result.get(0);
		} else {
			return new FunctionNode(new Add(), Arguments.createArguments(result.remove(0), createAddArguments(result)));
		}
	}

	static List<Node> getAddArguments(Node n) {
		List<Node> result = new ArrayList<>();
		if (n instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) n;
			if (fn.getOperator().getClass() == Add.class) {
				result.addAll(getAddArguments(fn.getArguments().get(0)));
				result.addAll(getAddArguments(fn.getArguments().get(1)));
			} else {
				result.add(n);
			}
		} else {
			result.add(n);
		}
		return result;
	}
}
