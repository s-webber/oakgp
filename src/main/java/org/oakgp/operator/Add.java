package org.oakgp.operator;

import java.util.Optional;

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
	public Optional<Node> simplify(Arguments arguments) {
		Node arg1 = arguments.get(0);
		Node arg2 = arguments.get(1);
		if (ZERO.equals(arg1)) {
			return Optional.of(arg2);
		} else if (ZERO.equals(arg2)) {
			return Optional.of(arg1);
		} else if (arg1 instanceof FunctionNode && arg2 instanceof FunctionNode) {
			FunctionNode fn1 = (FunctionNode) arg1;
			FunctionNode fn2 = (FunctionNode) arg2;
			if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode
					&& (sameOperator(Add.class, fn1, fn2) || sameOperator(Subtract.class, fn1, fn2))) {
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(new ConstantNode(((int) fn1Value) + ((int) fn2Value)),
						new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(1))))));
			} else if (fn1.getArguments().get(1) instanceof ConstantNode && fn2.getArguments().get(1) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				Object fn1Value = fn1.getArguments().get(1).evaluate(null);
				Object fn2Value = fn2.getArguments().get(1).evaluate(null);
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(new ConstantNode(((int) fn1Value) + ((int) fn2Value)),
						new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(0), fn2.getArguments().get(0))))));
			} else if (fn1.getArguments().get(0) instanceof ConstantNode && fn2.getArguments().get(1) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				Object fn1Value = fn1.getArguments().get(0).evaluate(null);
				Object fn2Value = fn2.getArguments().get(1).evaluate(null);
				return Optional.of(new FunctionNode(fn1.getOperator(), Arguments.createArguments(new ConstantNode(((int) fn1Value) + ((int) fn2Value)),
						new FunctionNode(this, Arguments.createArguments(fn1.getArguments().get(1), fn2.getArguments().get(0))))));
			} else if (fn1.getArguments().get(1) instanceof ConstantNode && fn2.getArguments().get(0) instanceof ConstantNode
					&& sameOperator(Add.class, fn1, fn2)) {
				Object fn1Value = fn1.getArguments().get(1).evaluate(null);
				Object fn2Value = fn2.getArguments().get(0).evaluate(null);
				return Optional.of(new FunctionNode(this, Arguments.createArguments(new ConstantNode(((int) fn1Value) + ((int) fn2Value)), new FunctionNode(
						this, Arguments.createArguments(fn1.getArguments().get(0), fn2.getArguments().get(1))))));
			}
		} else if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) arg2;
			Node firstArg = fn.getArguments().get(0);
			if (firstArg instanceof ConstantNode) {
				Operator fnOperator = fn.getOperator();
				if (fnOperator.getClass() == Add.class) {
					return Optional.of(new FunctionNode(fnOperator, Arguments.createArguments(
							new ConstantNode(((int) arg1.evaluate(null)) + ((int) firstArg.evaluate(null))), fn.getArguments().get(1))));
				} else if (fnOperator.getClass() == Subtract.class) {
					return Optional.of(new FunctionNode(fnOperator, Arguments.createArguments(
							new ConstantNode(((int) arg1.evaluate(null)) + ((int) firstArg.evaluate(null))), fn.getArguments().get(1))));
				}
			}
		}
		return Optional.empty();
	}

	private boolean sameOperator(Class<? extends Operator> operatorClass, FunctionNode fn1, FunctionNode fn2) {
		return fn1.getOperator().getClass() == operatorClass && fn2.getOperator().getClass() == operatorClass;
	}
}
