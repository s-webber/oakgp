package org.oakgp.examples;

import static org.oakgp.Arguments.createArguments;

import java.util.Collection;
import java.util.Set;

import org.oakgp.FunctionSet;
import org.oakgp.NodeSimplifier;
import org.oakgp.RankedCandidate;
import org.oakgp.Signature;
import org.oakgp.TerminalSet;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Add;
import org.oakgp.operator.Equal;
import org.oakgp.operator.GreaterThan;
import org.oakgp.operator.GreaterThanOrEqual;
import org.oakgp.operator.If;
import org.oakgp.operator.LessThan;
import org.oakgp.operator.LessThanOrEqual;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.NotEqual;
import org.oakgp.operator.Operator;
import org.oakgp.operator.Subtract;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.serialize.NodeWriter;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

/** Utility classes for tests in sub-packages of {@code org.oakgp.examples}. */
public class SystemTestUtils {
	public static final Random RANDOM = new JavaUtilRandomAdapter();
	public static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
	public static final FunctionSet ARITHMETIC_FUNCTION_SET = new FunctionSet(RANDOM, new Operator[] { new Add(), new Subtract(), new Multiply() });
	public static final FunctionSet COMPARISON_FUNCTION_SET = new FunctionSet(RANDOM, new Operator[] { new Add(), new Subtract(), new Multiply(),
			new LessThan(), new LessThanOrEqual(), new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual(), new If() });
	public static final int GENERATION_SIZE = 50;
	public static final int ELITISM_SIZE = 3;
	public static final double RATIO_VARIABLES = .6;

	public static Collection<Node> createInitialGeneration(FunctionSet functionSet, TerminalSet terminalSet, int size) {
		Set<Node> initialGeneration = new NodeSet();
		for (int i = 0; i < size; i++) {
			initialGeneration.add(makeRandomTree(functionSet, terminalSet, 4));
		}
		return initialGeneration;
	}

	public static Node makeRandomTree(FunctionSet functionSet, TerminalSet terminalSet, int depth) {
		return makeRandomTree(Type.INTEGER, functionSet, terminalSet, depth);
	}

	private static Node makeRandomTree(Type type, FunctionSet functionSet, TerminalSet terminalSet, int depth) {
		if (depth > 0 && RANDOM.nextDouble() < .5) {
			Operator operator = functionSet.next(type);
			Signature signature = operator.getSignature();
			Node[] args = new Node[signature.getArgumentTypesLength()];
			for (int i = 0; i < args.length; i++) {
				Type argType = signature.getArgumentType(i);
				Node arg = makeRandomTree(argType, functionSet, terminalSet, depth - 1);
				args[i] = arg;
			}
			return new FunctionNode(operator, createArguments(args));
		} else {
			return terminalSet.next();
		}
	}

	public static ConstantNode[] createConstants(int numberOfConstants) {
		ConstantNode[] constants = new ConstantNode[numberOfConstants];
		for (int i = 0; i < numberOfConstants; i++) {
			constants[i] = new ConstantNode(i);
		}
		return constants;
	}

	public static void printRankedCandidate(RankedCandidate candidate) {
		System.out.println("Best: " + candidate);
		System.out.println(new NodeWriter().writeNode(new NodeSimplifier().simplify(candidate.getNode())));
	}
}
