package org.oakgp;

import static org.oakgp.Arguments.createArguments;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * Simplifies tree structures by replacing expressions with their values.
 * <p>
 * e.g. The expression:
 *
 * <pre>
 * (+ 7 (* 3 6))
 * </pre>
 *
 * can be simplified to the value:
 *
 * <pre>
 * 25
 * </pre>
 *
 * </p>
 * <b>Note:</b> relies on {@code Operator} implementations being referentially transparent.
 */
public final class NodeSimplifier {
	public Node simplify(Node input) {
		if (input instanceof FunctionNode) {
			return simplifyFunctionNode((FunctionNode) input);
		} else {
			return input;
		}
	}

	private Node simplifyFunctionNode(FunctionNode input) {
		// try to simplify each of the arguments
		Arguments inputArgs = input.getArguments();
		Node[] simplifiedArgs = new Node[inputArgs.length()];
		boolean modified = false;
		boolean constants = true;
		for (int i = 0; i < simplifiedArgs.length; i++) {
			Node originalArg = inputArgs.get(i);
			simplifiedArgs[i] = simplify(originalArg);
			if (originalArg != simplifiedArgs[i]) {
				modified = true;
			}
			if (!(simplifiedArgs[i] instanceof ConstantNode)) {
				constants = false;
			}
		}

		// if could simplify arguments then use simplified version to create new FunctionNode
		Arguments arguments;
		FunctionNode output;
		if (modified) {
			arguments = createArguments(simplifiedArgs);
			output = new FunctionNode(input.getOperator(), arguments);
		} else {
			arguments = inputArgs;
			output = input;
		}

		// if all arguments are constants then return result of evaluating them
		if (constants) {
			return new ConstantNode(output.evaluate(null));
		}

		// try to simplify using operator
		return input.getOperator().simplify(arguments).orElse(output);
	}
}
