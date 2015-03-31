package org.oakgp;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

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
 * <b>Note:</b> relies on {@code Operator} implementations being referentially transparent.
 */
public final class NodeSimplifier {
	private static final int MAX_RETRIES = 100;

	public Node simplify(Node input) {
		int ctr = 0;
		Set<String> s = new HashSet<>();
		Node previous;
		Node output = input;
		do {
			previous = output;
			output = _simplify(output);
			if (!output.equals(previous) && !s.add(new NodeWriter().writeNode(output))) { // TODO
				// throw new IllegalArgumentException(new NodeWriter().writeNode(output) + " " + new NodeWriter().writeNode(previous));
				return output;
			}
			if (ctr++ > MAX_RETRIES) { // TODO
				throw new IllegalArgumentException(new NodeWriter().writeNode(input));
			}
		} while (output instanceof FunctionNode && !output.equals(previous));
		return output;
	}

	private Node _simplify(Node input) {
		if (input instanceof FunctionNode) {
			Node output = simplifyFunctionNode((FunctionNode) input);
			assertEvaluateToSameResult(input, output);
			return output;
		} else {
			return input;
		}
	}

	private Node simplifyFunctionNode(final FunctionNode input) {
		// TODO have a isSimplified method on FunctionNode to indicate that if it has already been simplified? (so no need to try again here)
		// try to simplify each of the arguments
		Arguments inputArgs = input.getArguments();
		Node[] simplifiedArgs = new Node[inputArgs.length()];
		boolean modified = false;
		boolean constants = true;
		for (int i = 0; i < simplifiedArgs.length; i++) {
			Node originalArg = inputArgs.get(i);
			simplifiedArgs[i] = _simplify(originalArg);
			if (originalArg != simplifiedArgs[i]) {
				assertEvaluateToSameResult(originalArg, simplifiedArgs[i]);
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
			assertEvaluateToSameResult(input, output);
		} else {
			arguments = inputArgs;
			output = input;
		}

		// if all arguments are constants then return result of evaluating them
		if (constants) {
			return new ConstantNode(output.evaluate(null), output.getType());
		}

		// try to simplify using operator specific logic
		Node simplifiedByOperatorVersion = input.getOperator().simplify(arguments);
		if (simplifiedByOperatorVersion == null) {
			return output;
		} else {
			assertEvaluateToSameResult(input, simplifiedByOperatorVersion);
			return simplifiedByOperatorVersion;
		}
	}
}
