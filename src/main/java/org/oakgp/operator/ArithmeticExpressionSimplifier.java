package org.oakgp.operator;

import static org.oakgp.Type.INTEGER;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;
import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

final class ArithmeticExpressionSimplifier {
	static final ConstantNode ZERO = createConstant(0);
	static final ConstantNode ONE = createConstant(1);

	/** Private constructor as all methods are static. */
	private ArithmeticExpressionSimplifier() {
		// do nothing
	}

	static Optional<Node> simplify(Operator operator, Node firstArg, Node secondArg) {
		if (!(operator instanceof ArithmeticOperator)) {
			return Optional.empty();
		}

		Node simplifiedVersion = getSimplifiedVersion(operator, firstArg, secondArg);
		if (simplifiedVersion != null) {// TODO remove this block - only used to sanity check results
			FunctionNode in = new FunctionNode(operator, firstArg, secondArg);
			assertEvaluateToSameResult(in, simplifiedVersion);
		}
		return Optional.ofNullable(simplifiedVersion);
	}

	private static Node getSimplifiedVersion(Operator operator, Node firstArg, Node secondArg) {
		boolean isAdd = isAdd(operator);
		boolean isSubtract = isSubtract(operator);
		boolean isMultiply = isMultiply(operator);

		// Ordering of arguments - TODO move to Add and Multiply
		if ((isAdd || isMultiply) && NODE_COMPARATOR.compare(firstArg, secondArg) > 0) {
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
					return new FunctionNode(operator, p.x, p.y);
				}
				o = recursiveReplace(secondArg, firstArg, isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return new FunctionNode(operator, p.y, p.x);
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
	private static Optional<NodePair> recursiveReplace(Node nodeToSearch, Node nodeToUpdate, boolean isPos) {
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
					Node tmp = new FunctionNode(op, createConstant(result), secondArg);
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
						return Optional.of(new NodePair(new FunctionNode(op, p.x, o2.get().x), o2.get().y));
					} else {
						return Optional.of(new NodePair(new FunctionNode(op, p.x, secondArg), p.y));
					}
				}
				o = recursiveReplace(secondArg, nodeToUpdate, isSubtract ? !isPos : isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return Optional.of(new NodePair(new FunctionNode(op, firstArg, p.x), p.y));
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

	// TODO return Optional<Node>
	private static Node simplify(final Node current, final Node nodeToReplace, boolean isPos) {
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
				return new FunctionNode(currentOperator, replace(firstArg, nodeToReplace, isPos), secondArg);
			} else if (isSuitableForReplacement(secondArg, nodeToReplace)) {
				return new FunctionNode(currentOperator, firstArg, replace(secondArg, nodeToReplace, recursiveIsPos));
			}
			Node tmp = simplify(firstArg, nodeToReplace, isPos);
			if (!tmp.equals(firstArg)) {
				return new FunctionNode(currentOperator, tmp, secondArg);
			}
			tmp = simplify(secondArg, nodeToReplace, recursiveIsPos);
			if (!tmp.equals(secondArg)) {
				return new FunctionNode(currentOperator, firstArg, tmp);
			}
		} else if (isMultiply(currentOperator) && firstArg instanceof ConstantNode && secondArg.equals(nodeToReplace)) {
			int inc = isPos ? 1 : -1;
			return new FunctionNode(currentOperator, createConstant((int) ((ConstantNode) firstArg).evaluate(null) + inc), nodeToReplace);
		} else if (sameMultiplyVariable(current, nodeToReplace)) {
			return addMulitplyVariables(current, nodeToReplace, isPos);
		}

		return current;
	}

	private static boolean isSuitableForReplacement(Node currentNode, Node nodeToReplace) {
		if (nodeToReplace instanceof ConstantNode) {
			return currentNode instanceof ConstantNode;
		} else {
			return nodeToReplace.equals(currentNode);
		}
	}

	private static boolean sameMultiplyVariable(Node n1, Node n2) {
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

	private static Node addMulitplyVariables(Node n1, Node n2, boolean isPos) {
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
		return new FunctionNode(f1.getOperator(), createConstant(result), f1.getArguments().get(1));
	}

	private static Node replace(Node currentNode, Node nodeToReplace, boolean isPos) {
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
				return multiplyByTwo(nodeToReplace);
			} else {
				return ZERO;
			}
		}
	}

	private static Node dealWithSubtract(Operator currentOperator, Node tmp) {
		if (isSubtract(currentOperator)) {
			return new FunctionNode(currentOperator, ZERO, tmp);
		} else {
			return tmp;
		}
	}

	static ConstantNode createConstant(int i) {
		return new ConstantNode(i, INTEGER);
	}

	static FunctionNode multiplyByTwo(Node arg) {
		return new FunctionNode(new Multiply(), createConstant(2), arg);
	}

	static Node negate(Node arg) {
		if (arg instanceof ConstantNode) {
			return createConstant(-(int) arg.evaluate(null));
		} else {
			return new FunctionNode(new Subtract(), ZERO, arg);
		}
	}

	static boolean isAddOrSubtract(Operator o) {
		return isAdd(o) || isSubtract(o);
	}

	static boolean isAdd(FunctionNode n) {
		return isAdd(n.getOperator());
	}

	static boolean isAdd(Operator o) {
		return isOperatorOfType(o, Add.class);
	}

	static boolean isSubtract(Node n) {
		return n instanceof FunctionNode && isSubtract((FunctionNode) n);
	}

	static boolean isSubtract(FunctionNode n) {
		return isSubtract(n.getOperator());
	}

	static boolean isSubtract(Operator o) {
		return isOperatorOfType(o, Subtract.class);
	}

	static boolean isMultiply(FunctionNode n) {
		return isMultiply(n.getOperator());
	}

	static boolean isMultiply(Operator o) {
		return isOperatorOfType(o, Multiply.class);
	}

	private static boolean isOperatorOfType(Operator o, Class<? extends Operator> operatorClass) {
		return o.getClass() == operatorClass;
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
