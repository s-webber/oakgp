package org.oakgp.node;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.operator.Operator;

/** Contains a function (operator) and the arguments (operands) to apply to it. */
public final class FunctionNode implements Node {
	private final Operator f;
	private final Arguments arguments;

	/**
	 * Constructs a new {@code FunctionNode} with the specified operator function and arguments.
	 *
	 * @param operator
	 *            the operator to associate with this {@code FunctionNode}
	 * @param arguments
	 *            the arguments (i.e. operands) to apply to {@code operator} when evaluating this {@code FunctionNode}
	 */
	public FunctionNode(Operator operator, Arguments arguments) {
		this.f = operator;
		this.arguments = arguments;
	}

	public Operator getOperator() {
		return f;
	}

	public Arguments getArguments() {
		return arguments;
	}

	@Override
	public int evaluate(Assignments assignments) {
		return f.evaluate(arguments, assignments);
	}

	@Override
	public Node replaceAt(int index, java.util.function.Function<Node, Node> replacement) {
		int total = 0;
		for (int i = 0; i < arguments.length(); i++) {
			Node node = arguments.get(i);
			int c = node.getNodeCount();
			if (total + c > index) {
				return new FunctionNode(f, arguments.replaceAt(i, node.replaceAt(index - total, replacement)));
			} else {
				total += c;
			}
		}
		return replacement.apply(this);
	}

	@Override
	public Node getAt(int index) {
		int total = 0;
		for (int i = 0; i < arguments.length(); i++) {
			Node node = arguments.get(i);
			int c = node.getNodeCount();
			if (total + c > index) {
				return arguments.get(i).getAt(index - total);
			} else {
				total += c;
			}
		}
		return this;
	}

	@Override
	public int getNodeCount() {
		int total = 1;
		for (int i = 0; i < arguments.length(); i++) {
			total += arguments.get(i).getNodeCount();
		}
		return total;
	}

	@Override
	public int hashCode() {
		// TODO cache hashCode?
		return f.getClass().hashCode() * arguments.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) o;
			return this.f.getClass().equals(fn.f.getClass()) && this.arguments.equals(fn.arguments);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(f.getClass().getName());
		for (int i = 0; i < arguments.length(); i++) {
			sb.append(' ');
			sb.append(arguments.get(i));
		}
		return sb + ")";
	}
}
