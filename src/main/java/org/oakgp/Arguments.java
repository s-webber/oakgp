package org.oakgp;

import org.oakgp.node.Node;

/**
 * Represents the arguments of a function.
 * <p>
 * Immutable.
 */
public final class Arguments {
	private final Node[] args;

	/**
	 * Returns a new {@code Arguments} which contains the specified values.
	 * <p>
	 * Note: {@code Arguments} is immutable - so subsequent changes to {@code args} will not be reflected in the returned {@code Arguments}.
	 *
	 * @param args
	 *            the values to be stored in the {@code Arguments}
	 * @return a new {@code Arguments} which contains the values specified by {@code args}
	 */
	public static Arguments createArguments(Node... args) {
		return new Arguments(arrayCopy(args));
	}

	/** @see #createArguments(Node...) */
	private Arguments(Node[] args) {
		this.args = args;
	}

	/**
	 * Returns the {@code Node} at the specified position in this {@code Arguments}.
	 *
	 * @param index
	 *            index of the element to return
	 * @return the {@code Node} at the specified position in this {@code Arguments}
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (<tt>index &lt; 0 || index &gt;= length()</tt>)
	 */
	public Node get(int index) {
		return args[index];
	}

	/**
	 * Returns the number of elements in this {@code Arguments}.
	 *
	 * @return the number of elements in this {@code Arguments}
	 */
	public int length() {
		return args.length;
	}

	/**
	 * Returns a new {@code Arguments} resulting from replacing the existing {@code Node} at position {@code index} with {@code replacement}.
	 *
	 * @param index
	 *            the index of the {@code Node} that needs to be replaced.
	 * @param replacement
	 *            the new {@code Node} that needs to be store.
	 * @return A new {@code Arguments} derived from this {@code Arguments} by replacing the element at position {@code index} with {@code replacement}.
	 */
	public Arguments replaceAt(int index, Node replacement) {
		Node[] clone = arrayCopy(args);
		clone[index] = replacement;
		return new Arguments(clone);
	}

	private static Node[] arrayCopy(Node[] original) {
		Node[] copy = new Node[original.length];
		System.arraycopy(original, 0, copy, 0, original.length);
		return copy;
	}
}
