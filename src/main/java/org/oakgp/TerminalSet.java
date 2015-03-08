package org.oakgp;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.util.Random;

/** Represents the range of possible terminal nodes to use during a genetic programming run. */
public final class TerminalSet {
	private final Random random;
	private final double ratioVariables;
	private final int numVariables;
	private final ConstantNode[] constants;

	// TODO defensive copy of constants?
	public TerminalSet(Random random, double ratioVariables, int numVariables, ConstantNode[] constants) {
		this.random = random;
		this.ratioVariables = ratioVariables;
		this.numVariables = numVariables;
		this.constants = constants;
	}

	/**
	 * Returns a randomly selected terminal node.
	 *
	 * @return a randomly selected terminal node
	 */
	public Node next() {
		if (doCreateVariable()) {
			return nextVariable();
		} else {
			return nextConstant();
		}
	}

	/**
	 * Returns a randomly selected terminal node that is not the same as the specified {@code Node}.
	 *
	 * @param current
	 *            the current {@code Node} that the returned result should be an alternative to (i.e. not the same as)
	 * @return a randomly selected terminal node that is not the same as the specified {@code Node}
	 */
	public Node nextAlternative(Node current) {
		if (doCreateVariable()) {
			return nextAlternativeVariable(current);
		} else {
			return nextAlternativeConstant(current);
		}
	}

	private Node nextAlternativeVariable(Node current) {
		if (current instanceof VariableNode) {
			if (numVariables == 1) {
				return nextConstant();
			} else {
				int randomId = random.nextInt(numVariables);
				if (randomId == ((VariableNode) current).getId()) {
					int secondRandomId = random.nextInt(numVariables - 1);
					if (secondRandomId >= randomId) {
						return new VariableNode(secondRandomId + 1);
					} else {
						return new VariableNode(secondRandomId);
					}
				} else {
					return new VariableNode(randomId);
				}
			}
		} else {
			return nextVariable();
		}
	}

	private Node nextConstant() {
		return constants[random.nextInt(constants.length)];
	}

	// TODO create cache of VariableNodes rather than creating new instances each time?
	private Node nextVariable() {
		return new VariableNode(random.nextInt(numVariables));
	}

	private Node nextAlternativeConstant(Node current) {
		int randomIndex = random.nextInt(constants.length);
		ConstantNode next = constants[randomIndex];
		if (next == current) {
			int secondRandomIndex = random.nextInt(constants.length - 1);
			if (secondRandomIndex >= randomIndex) {
				return constants[secondRandomIndex + 1];
			} else {
				return constants[secondRandomIndex];
			}
		} else {
			return next;
		}
	}

	private boolean doCreateVariable() {
		return random.nextDouble() < ratioVariables;
	}
}
