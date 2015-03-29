package org.oakgp.operator;

import static org.oakgp.operator.ArithmeticOperator.ZERO;
import static org.oakgp.operator.ArithmeticOperator.createConstant;
import static org.oakgp.operator.ArithmeticOperator.isAdd;
import static org.oakgp.operator.ArithmeticOperator.isMultiply;
import static org.oakgp.operator.ArithmeticOperator.isSubtract;
import static org.oakgp.operator.ArithmeticOperator.times2;
import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

final class ArithmeticExpressionSimplifier {
	Optional<Node> simplify(Operator operator, Node firstArg, Node secondArg) {
		if (!(operator instanceof ArithmeticOperator)) {
			return Optional.empty();
		}

		Node simplifiedVersion = getSimplifiedVerion(operator, firstArg, secondArg);
		if (simplifiedVersion != null) {// TODO remove this block - only used to sanity check results
			FunctionNode in = new FunctionNode(operator, Arguments.createArguments(firstArg, secondArg));
			assertEvaluateToSameResult(in, simplifiedVersion);
		}
		return Optional.ofNullable(simplifiedVersion);
	}

	private Node getSimplifiedVerion(Operator operator, Node firstArg, Node secondArg) {
		boolean isAdd = isAdd(operator);
		boolean isSubtract = isSubtract(operator);
		boolean isMultiply = isMultiply(operator);

		// Ordering of arguments - TODO move to Add and Multiply
		if ((isAdd || isMultiply) && new NodeComparator().compare(firstArg, secondArg) > 0) {
			throw new IllegalArgumentException("arg1 " + firstArg + " arg2 " + secondArg);
		}

		// TODO move to Operator implementations
		if (firstArg instanceof ConstantNode && secondArg instanceof FunctionNode) {
			FunctionNode fn2 = (FunctionNode) secondArg;
			if (fn2.getArguments().get(0) instanceof ConstantNode && (operator.getClass() == fn2.getOperator().getClass() || (isAdd && isSubtract(fn2)))) {
				throw new IllegalArgumentException();
			}
		}

		if (isAdd || isSubtract) {
			boolean isPos = !isSubtract;
			if (firstArg instanceof FunctionNode && secondArg instanceof FunctionNode) {
				Optional<NodePair> o = recursiveReplace(firstArg, secondArg, isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return new FunctionNode(operator, Arguments.createArguments(p.x, p.y));
				}
				o = recursiveReplace(secondArg, firstArg, isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return new FunctionNode(operator, Arguments.createArguments(p.y, p.x));
				}
			} else if (firstArg instanceof FunctionNode) {
				Node tmp = simplify(firstArg, secondArg, isPos);
				if (!tmp.equals(firstArg)) {
					return tmp;
				}
			} else if (secondArg instanceof FunctionNode) {
				Node tmp = simplify(secondArg, firstArg, isPos);
				if (!tmp.equals(secondArg)) {
					return dealWithSubtract(operator, tmp);
				}
			}
		}

		return null;
	}

	// TODO order of args nodeToSearch,nodeToUpdate or nodeToUpdate,nodeToSearch ? be consistent with simplify
	// nodeToSearch tree structure being walked
	// nodeToUpdate item to add to nodeToSearch
	private Optional<NodePair> recursiveReplace(Node nodeToSearch, Node nodeToUpdate, boolean isPos) {
		if (nodeToSearch instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) nodeToSearch;
			Operator op = fn.getOperator();
			boolean isAdd = isAdd(op);
			boolean isSubtract = isSubtract(op);
			boolean isMultiply = isMultiply(op);
			Node firstArg = fn.getArguments().get(0);
			Node secondArg = fn.getArguments().get(1);
			if (isMultiply && nodeToUpdate instanceof FunctionNode) {
				FunctionNode x = (FunctionNode) nodeToUpdate;
				Arguments a = x.getArguments();
				if (isMultiply(x) && firstArg instanceof ConstantNode && a.get(0) instanceof ConstantNode && secondArg.equals(a.get(1))) {
					int i1 = (int) firstArg.evaluate(null);
					int i2 = (int) a.get(0).evaluate(null);
					int result;
					if (isPos) {
						result = i2 + i1;
					} else {
						result = i2 - i1;
					}
					Node tmp = new FunctionNode(op, Arguments.createArguments(createConstant(result), secondArg));
					return Optional.of(new NodePair(ZERO, tmp));
				}

				Node tmp = simplify(nodeToUpdate, nodeToSearch, isPos);
				if (!tmp.equals(nodeToUpdate)) {
					return Optional.of(new NodePair(ZERO, tmp));
				}
			}
			if (isAdd || isSubtract) {
				Optional<NodePair> o = recursiveReplace(firstArg, nodeToUpdate, isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					// TODO is performance better if we don't call recursiveReplace again here with the second arg?
					Optional<NodePair> o2 = recursiveReplace(secondArg, p.y, isSubtract ? !isPos : isPos);
					if (o2.isPresent()) {
						return Optional.of(new NodePair(new FunctionNode(op, Arguments.createArguments(p.x, o2.get().x)), o2.get().y));
					} else {
						return Optional.of(new NodePair(new FunctionNode(op, Arguments.createArguments(p.x, secondArg)), p.y));
					}
				}
				o = recursiveReplace(secondArg, nodeToUpdate, isSubtract ? !isPos : isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return Optional.of(new NodePair(new FunctionNode(op, Arguments.createArguments(firstArg, p.x)), p.y));
				}
			}
		} else if (!ZERO.equals(nodeToSearch)) {
			// TODO confirm this is not worth doing when nodeToSearch is 0
			Node tmp = simplify(nodeToUpdate, nodeToSearch, isPos);
			if (!tmp.equals(nodeToUpdate)) {
				return Optional.of(new NodePair(ZERO, tmp));
			}
		}
		return Optional.empty();
	}

	private Node addMulitplyVariables(Node n1, Node n2, boolean isPos) {
		FunctionNode f1 = (FunctionNode) n1;
		FunctionNode f2 = (FunctionNode) n2;
		int i1 = (int) f1.getArguments().get(0).evaluate(null);
		int i2 = (int) f2.getArguments().get(0).evaluate(null);
		int result;
		if (isPos) {
			result = i1 + i2;
		} else {
			result = i1 - i2;
		}
		return new FunctionNode(f1.getOperator(), Arguments.createArguments(createConstant(result), f1.getArguments().get(1)));
	}

	private boolean sameMultiplyVariable(Node n1, Node n2) {
		if (n1 instanceof FunctionNode && n2 instanceof FunctionNode) {
			FunctionNode f1 = (FunctionNode) n1;
			FunctionNode f2 = (FunctionNode) n2;
			if (isMultiply(f1) && isMultiply(f2) && f1.getArguments().get(0) instanceof ConstantNode && f2.getArguments().get(0) instanceof ConstantNode
					&& f1.getArguments().get(1).equals(f2.getArguments().get(1))) {
				return true;
			}
		}
		return false;
	}

	private Node dealWithSubtract(Operator currentOperator, Node tmp) {
		if (isSubtract(currentOperator)) {
			tmp = new FunctionNode(currentOperator, Arguments.createArguments(ZERO, tmp));
		}
		return tmp;
	}

	// TODO return Optional<Node>
	private Node simplify(final Node current, final Node nodeToReplace, boolean isPos) {
		if (current.equals(nodeToReplace)) {
			return replace(current, nodeToReplace, isPos);
		}
		if (!(current instanceof FunctionNode)) {
			return current;
		}

		FunctionNode currentFunctionNode = (FunctionNode) current;
		Node firstArg = currentFunctionNode.getArguments().get(0);
		Node secondArg = currentFunctionNode.getArguments().get(1);
		Operator currentOperator = currentFunctionNode.getOperator();
		boolean isAdd = isAdd(currentOperator);
		boolean isSubtract = isSubtract(currentOperator);
		if (isAdd || isSubtract) {
			boolean recursiveIsPos = isPos;
			if (isSubtract) {
				recursiveIsPos = !isPos;
			}
			if (isSuitableForReplacement(firstArg, nodeToReplace)) {
				return new FunctionNode(currentOperator, Arguments.createArguments(replace(firstArg, nodeToReplace, isPos), secondArg));
			} else if (isSuitableForReplacement(secondArg, nodeToReplace)) {
				return new FunctionNode(currentOperator, Arguments.createArguments(firstArg, replace(secondArg, nodeToReplace, recursiveIsPos)));
			}
			Node tmp = simplify(firstArg, nodeToReplace, isPos);
			if (!tmp.equals(firstArg)) {
				return returnWithSimplifiedArgument(currentOperator, tmp, secondArg);
			}
			tmp = simplify(secondArg, nodeToReplace, recursiveIsPos);
			if (!tmp.equals(secondArg)) {
				return returnWithSimplifiedArgument(currentOperator, firstArg, tmp);
			}
		} else if (isMultiply(currentOperator) && firstArg instanceof ConstantNode && secondArg.equals(nodeToReplace)) {
			int inc = isPos ? 1 : -1;
			return new FunctionNode(currentOperator, Arguments.createArguments(createConstant((int) ((ConstantNode) firstArg).evaluate(null) + inc),
					nodeToReplace));
		} else if (sameMultiplyVariable(current, nodeToReplace)) {
			return addMulitplyVariables(current, nodeToReplace, isPos);
		}

		return current;
	}

	private FunctionNode returnWithSimplifiedArgument(Operator currentOperator, Node firstArg, Node secondArg) {
		return new FunctionNode(currentOperator, Arguments.createArguments(firstArg, secondArg));
	}

	private boolean isSuitableForReplacement(Node currentNode, Node nodeToReplace) {
		if (nodeToReplace instanceof ConstantNode) {
			return currentNode instanceof ConstantNode;
		} else {
			return nodeToReplace.equals(currentNode);
		}
	}

	private Node replace(Node currentNode, Node nodeToReplace, boolean isPos) {
		if (nodeToReplace.getClass() != currentNode.getClass()) {
			throw new IllegalArgumentException(nodeToReplace.getClass().getName() + " " + currentNode.getClass().getName());
		}

		if (nodeToReplace instanceof ConstantNode) {
			int currentNodeValue = (int) currentNode.evaluate(null);
			int nodeToReplaceValue = (int) nodeToReplace.evaluate(null);
			if (isPos) {
				return createConstant(currentNodeValue + nodeToReplaceValue);
			} else {
				return createConstant(currentNodeValue - nodeToReplaceValue);
			}
		} else {
			if (isPos) {
				return times2(nodeToReplace);
			} else {
				return ZERO;
			}
		}
	}

	private static class NodePair {
		final Node x;
		final Node y;

		NodePair(Node x, Node y) {
			this.x = x;
			this.y = y;
		}
	}
}
