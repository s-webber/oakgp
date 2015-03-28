package org.oakgp.operator;

import static org.oakgp.operator.ArithmeticOperator.createConstant;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.NodeComparator;

public final class ArithmeticExpressionSimplifier {
	Node simplify(Node in) {
		if (!(in instanceof FunctionNode)) {
			return in;
		}
		FunctionNode currentFunctionNode = (FunctionNode) in;
		Operator currentOperator = currentFunctionNode.getOperator();
		if (!(currentOperator instanceof ArithmeticOperator)) {
			return in;
		}
		boolean isAdd = currentOperator.getClass() == Add.class;
		boolean isSubtract = currentOperator.getClass() == Subtract.class;
		boolean isMultiply = currentOperator.getClass() == Multiply.class;
		Node firstArg = currentFunctionNode.getArguments().get(0);
		Node secondArg = currentFunctionNode.getArguments().get(1);

		// Ordering of arguments - TODO move to Add and Multiply
		if ((isAdd || isMultiply) && new NodeComparator().compare(firstArg, secondArg) > 0) {
			throw new IllegalArgumentException("arg1 " + firstArg + " arg2 " + secondArg);
		}

		// TODO move to Operator implementations
		if (firstArg instanceof ConstantNode && secondArg instanceof FunctionNode) {
			FunctionNode fn2 = (FunctionNode) secondArg;
			if (fn2.getArguments().get(0) instanceof ConstantNode && (currentOperator.getClass() == fn2.getOperator().getClass() || (isAdd && sameOperator(Subtract.class, fn2)))) {
				int i1 = (int) firstArg.evaluate(null);
				int i2 = (int) fn2.getArguments().get(0).evaluate(null);
				int result;
				Operator op = fn2.getOperator();
				if (isAdd) {
					if (sameOperator(Subtract.class, fn2)) {
						result = i1 + i2;
					} else {
						result = i1 + i2;
					}
				} else if (isSubtract) {
					if (i1 == 0) {
						// (- 0 (- 0 v0)) -> v0
						// (- 0 (- 7 v0)) -> (- v0 7)
						return new FunctionNode(fn2.getOperator(), Arguments.createArguments(fn2.getArguments().get(1), fn2.getArguments().get(0)));
					} else if (i2 == 0) {
						// (- 1 (- 0 v0)) -> (+ 1 v0)
						return new FunctionNode(new Add(), Arguments.createArguments(firstArg, fn2.getArguments().get(1)));
					} else {
						op = new Add();
						result = i1 - i2;
					}
				} else if (isMultiply) {
					result = i1 * i2;
				} else {
					throw new IllegalArgumentException();
				}
				return new FunctionNode(op, Arguments.createArguments(createConstant(result), fn2.getArguments().get(1)));
			}
		}

		if (isAdd || isSubtract) {
			if (firstArg instanceof FunctionNode && secondArg instanceof FunctionNode) {
				Optional<NodePair> o = recursiveReplace(firstArg, secondArg, isAdd);
				if (o.isPresent()) {
					NodePair p = o.get();
					return new FunctionNode(currentOperator, Arguments.createArguments(p.x, p.y));
				}
				o = recursiveReplace(secondArg, firstArg, isAdd);
				if (o.isPresent()) {
					NodePair p = o.get();
					return new FunctionNode(currentOperator, Arguments.createArguments(p.y, p.x));
				}
			} else if (firstArg instanceof FunctionNode && !(secondArg instanceof FunctionNode)) {
				Node tmp = simplify(firstArg, secondArg, isAdd);
				if (!tmp.equals(firstArg)) {
					return tmp;
				}
			} else if (secondArg instanceof FunctionNode && !(firstArg instanceof FunctionNode)) {
				Node tmp = simplify(secondArg, firstArg, isAdd);
				if (!tmp.equals(secondArg)) {
					return dealWithSubtract(currentOperator, tmp);
				}
			}
		}

		return in;
	}

	// TODO order of args nodeToSearch,nodeToUpdate or nodeToUpdate,nodeToSearch ? be consistent with simplify
	private Optional<NodePair> recursiveReplace(Node nodeToSearch, Node nodeToUpdate, boolean isPos) {
		if (nodeToSearch instanceof FunctionNode) {
			FunctionNode fn = (FunctionNode) nodeToSearch;
			Operator op = fn.getOperator();
			boolean isAdd = op.getClass() == Add.class;
			boolean isSubtract = op.getClass() == Subtract.class;
			if (isAdd || isSubtract) {// op instanceof ArithmeticOperator) {
				Node firstArg = fn.getArguments().get(0);
				Node secondArg = fn.getArguments().get(1);
				Optional<NodePair> o = recursiveReplace(firstArg, nodeToUpdate, isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return Optional.of(new NodePair(new FunctionNode(op, Arguments.createArguments(p.x, secondArg)), p.y));
				}
				o = recursiveReplace(secondArg, nodeToUpdate, isSubtract ? !isPos : isPos);
				if (o.isPresent()) {
					NodePair p = o.get();
					return Optional.of(new NodePair(new FunctionNode(op, Arguments.createArguments(firstArg, p.x)), p.y));
				}
			}
		} else {
			Node tmp = simplify(nodeToUpdate, nodeToSearch, isPos);
			if (!tmp.equals(nodeToUpdate)) {
				return Optional.of(new NodePair(createConstant(0), tmp));
			}
		}
		return Optional.empty();
	}

	static boolean sameOperator(Class<? extends Operator> operatorClass, FunctionNode fn1, FunctionNode fn2) {
		return sameOperator(operatorClass, fn1) && sameOperator(operatorClass, fn2);
	}

	static boolean sameOperator(Class<? extends Operator> operatorClass, FunctionNode fn1) {
		return fn1.getOperator().getClass() == operatorClass;
	}

	private Node dealWithSubtract(Operator currentOperator, Node tmp) {
		if (currentOperator.getClass() == Subtract.class) {
			tmp = new FunctionNode(currentOperator, Arguments.createArguments(createConstant(0), tmp));
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
		boolean isAdd = currentOperator.getClass() == Add.class;
		boolean isSubtract = currentOperator.getClass() == Subtract.class;
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
		} else if (currentOperator.getClass() == Multiply.class && firstArg instanceof ConstantNode && secondArg.equals(nodeToReplace)) {
			int inc = isPos ? 1 : -1;
			return new FunctionNode(currentOperator, Arguments.createArguments(createConstant((int) ((ConstantNode) firstArg).evaluate(null) + inc),
					nodeToReplace));
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
				return new FunctionNode(new Multiply(), Arguments.createArguments(createConstant(2), nodeToReplace));
			} else {
				return createConstant(0);
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
