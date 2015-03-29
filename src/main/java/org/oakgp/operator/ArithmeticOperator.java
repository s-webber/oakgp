package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

abstract class ArithmeticOperator implements Operator {
	static final ConstantNode ZERO = createConstant(0); // TODO move or make private
	static final ConstantNode ONE = createConstant(1); // TODO move or make private
	private static final Signature SIGNATURE = Signature.createSignature(INTEGER, INTEGER, INTEGER);

	@Override
	public final Object evaluate(Arguments arguments, Assignments assignments) {
		int i1 = (int) arguments.get(0).evaluate(assignments);
		int i2 = (int) arguments.get(1).evaluate(assignments);
		return evaluate(i1, i2);
	}

	protected abstract int evaluate(int arg1, int arg2);

	protected static ConstantNode createConstant(int i) {
		return new ConstantNode(i, INTEGER);
	}

	static FunctionNode times2(Node arg) { // TODO move to somewhere more suitable
		return new FunctionNode(new Multiply(), Arguments.createArguments(createConstant(2), arg));
	}

	static Node negate(Node arg) { // TODO move to somewhere more suitable
		if (arg instanceof ConstantNode) {
			return createConstant(-(int) arg.evaluate(null));
		} else {
			return new FunctionNode(new Subtract(), Arguments.createArguments(ZERO, arg));
		}
	}

	static boolean isAddOrSubtract(Operator o) { // TODO move to somewhere more suitable
		return isAdd(o) || isSubtract(o);
	}

	static boolean isAdd(Node n) { // TODO move to somewhere more suitable
		return n instanceof FunctionNode && isSubtract((FunctionNode) n);
	}

	static boolean isAdd(FunctionNode n) { // TODO move to somewhere more suitable
		return isSubtract(n.getOperator());
	}

	static boolean isAdd(Operator o) { // TODO move to somewhere more suitable
		return isOperatorOfType(o, Add.class);
	}

	static boolean isSubtract(Node n) { // TODO move to somewhere more suitable
		return n instanceof FunctionNode && isSubtract((FunctionNode) n);
	}

	static boolean isSubtract(FunctionNode n) { // TODO move to somewhere more suitable
		return isSubtract(n.getOperator());
	}

	static boolean isSubtract(Operator o) { // TODO move to somewhere more suitable
		return isOperatorOfType(o, Subtract.class);
	}

	static boolean isMultiply(Node n) { // TODO move to somewhere more suitable
		return n instanceof FunctionNode && isMultiply((FunctionNode) n);
	}

	static boolean isMultiply(FunctionNode n) { // TODO move to somewhere more suitable
		return isMultiply(n.getOperator());
	}

	static boolean isMultiply(Operator o) { // TODO move to somewhere more suitable
		return isOperatorOfType(o, Multiply.class);
	}

	private static boolean isOperatorOfType(Operator o, Class<? extends Operator> operatorClass) { // TODO move to somewhere more suitable
		return o.getClass() == operatorClass;
	}

	@Override
	public final Signature getSignature() {
		return SIGNATURE;
	}
}
