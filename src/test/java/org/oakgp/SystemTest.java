package org.oakgp;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.mutate.SubtreeCrossover;
import org.oakgp.mutate.PointMutation;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;
import org.oakgp.operator.Subtract;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.Random;

/**
 * Performs full genetic programming runs without relying on any mock objects.
 * <p>
 * Would be better to have in a separate "system-test" directory under the "src" directory - or in a completely separate Git project (that has this project as a
 * dependency). Leaving here for the moment as it provides a convenient mechanism to perform a full test of the process. TODO decide long-term solution for this
 * </p>
 */
public class SystemTest {
	private static final Random RANDOM = new JavaUtilRandomAdapter();
	private static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
	private static final FunctionSet FUNCTION_SET = new FunctionSet(RANDOM, new Operator[] { new Add(), new Subtract(), new Multiply() });
	private static final int GENERATION_SIZE = 50;
	private static final double RATIO_VARIABLES = .6;

	@Test
	public void test() {
		int numVariables = 2;
		ConstantNode[] constants = new ConstantNode[11];
		for (int i = 0; i < constants.length; i++) {
			constants[i] = new ConstantNode(i);
		}
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, numVariables, constants);
		FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
			int x = a.get(0);
			int y = a.get(1);
			return (x * x) + 2 * y + 3 * x + 5;
		}));
		List<Node> initialGeneration = createInitialGeneration(FUNCTION_SET, terminalSet, GENERATION_SIZE);
		doIt(terminalSet, fitnessFunction, initialGeneration);
	}

	@Test
	public void test2() {
		int numVariables = 3;
		ConstantNode[] constants = new ConstantNode[11];
		for (int i = 0; i < constants.length; i++) {
			constants[i] = new ConstantNode(i);
		}
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, numVariables, constants);
		FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
			int x = a.get(0);
			int y = a.get(1);
			int z = a.get(2);
			return (x * -3) + (y * 5) - z;
		}));
		List<Node> initialGeneration = createInitialGeneration(FUNCTION_SET, terminalSet, GENERATION_SIZE);
		doIt(terminalSet, fitnessFunction, initialGeneration);
	}

	private static Map<Assignments, Integer> createTests(int numVariables, Function<Assignments, Integer> f) {
		Map<Assignments, Integer> tests = new HashMap<>();
		for (int i = 0; i < 200; i++) {
			int[] inputs = createInputs(numVariables);
			Assignments assignments = createAssignments(inputs);
			tests.put(assignments, f.apply(assignments));
		}
		return tests;
	}

	private static int[] createInputs(int numVariables) {
		int[] variables = new int[numVariables];
		for (int i = 0; i < numVariables; i++) {
			variables[i] = RANDOM.nextInt(40);
		}
		return variables;
	}

	private void doIt(TerminalSet terminalSet, FitnessFunction fitnessFunction, List<Node> initialGeneration) {
		Predicate<List<RankedCandidate>> terminator = createTerminator();
		Map<NodeEvolver, Long> nodeEvolvers = createNodeEvolvers(terminalSet);
		RankedCandidate best = Runner.process(new GenerationProcessor(fitnessFunction), new GenerationEvolver(SELECTOR_FACTORY, nodeEvolvers), terminator,
				initialGeneration);
		System.out.println("FIN " + best.getFitness() + " " + best.getNode());
	}

	private static List<Node> createInitialGeneration(FunctionSet functionSet, TerminalSet terminalSet, int size) {
		List<Node> initialGeneration = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			initialGeneration.add(makeRandomTree(functionSet, terminalSet, 4));
		}
		return initialGeneration;
	}

	private Map<NodeEvolver, Long> createNodeEvolvers(TerminalSet terminalSet) {
		Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
		nodeEvolvers.put(t -> makeRandomTree(FUNCTION_SET, terminalSet, 4), 5L);
		nodeEvolvers.put(new SubtreeCrossover(RANDOM), 22L);
		nodeEvolvers.put(new PointMutation(FUNCTION_SET, terminalSet), 22L);
		return nodeEvolvers;
	}

	private static Node makeRandomTree(FunctionSet functionSet, TerminalSet terminalSet, int depth) {
		if (depth > 0 && RANDOM.nextDouble() < .5) {
			Node arg1 = makeRandomTree(functionSet, terminalSet, depth - 1);
			Node arg2 = makeRandomTree(functionSet, terminalSet, depth - 1);
			Arguments args = createArguments(arg1, arg2);
			Operator f = FUNCTION_SET.next();
			return new FunctionNode(f, args);
		} else {
			return terminalSet.next();
		}
	}

	private Predicate<List<RankedCandidate>> createTerminator() {
		return new Predicate<List<RankedCandidate>>() {
			int ctr = 0;
			double previousBest = 0;

			@Override
			public boolean test(List<RankedCandidate> t) {
				ctr++;
				double best = t.get(0).getFitness();
				boolean finished = ctr > 1000 || best == 0;
				if (previousBest != best) {
					previousBest = best;
					System.out.println(ctr + " " + best);
				} else if (finished || ctr % 500 == 0) {
					System.out.println(ctr + " " + best);
				}
				return finished;
			}
		};
	}
}
