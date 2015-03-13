package org.oakgp.node;

import java.util.function.Function;

import org.oakgp.Assignments;
import org.oakgp.Type;

/**
 * Represents a constant value.
 * <p>
 * Return the same value each time is it evaluated.
 */
public final class ConstantNode implements Node {
	private final int value;

	/**
	 * Constructs a new {@code ConstantNode} that represents the specified value.
	 *
	 * @param value
	 *            the value to be represented by the {@code ConstantNode}
	 */
	public ConstantNode(int value) {
		this.value = value;
	}

	/**
	 * Returns the value specified when this {@code ConstantNode} was constructed.
	 */
	@Override
	public int evaluate(Assignments assignments) {
		return value;
	}

	@Override
	public int getNodeCount() {
		return 1;
	}

	@Override
	public Node replaceAt(int index, Function<Node, Node> replacement) {
		return replacement.apply(this);
	}

	@Override
	public Node getAt(int index) {
		return this;
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ConstantNode && this.value == ((ConstantNode) o).value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
