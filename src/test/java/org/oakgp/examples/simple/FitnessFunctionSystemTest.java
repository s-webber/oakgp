package org.oakgp.examples.simple;

import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.examples.SystemTestUtils.ARITHMETIC_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.COMPARISON_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.GENERATION_SIZE;
import static org.oakgp.examples.SystemTestUtils.RANDOM;
import static org.oakgp.examples.SystemTestUtils.RATIO_VARIABLES;
import static org.oakgp.examples.SystemTestUtils.SELECTOR_FACTORY;
import static org.oakgp.examples.SystemTestUtils.createConstants;
import static org.oakgp.examples.SystemTestUtils.createInitialGeneration;
import static org.oakgp.examples.SystemTestUtils.makeRandomTree;
import static org.oakgp.examples.SystemTestUtils.printRankedCandidate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.FitnessFunction;
import org.oakgp.FitnessFunctionCache;
import org.oakgp.FitnessFunctionGenerationProcessor;
import org.oakgp.FunctionSet;
import org.oakgp.GenerationEvolver;
import org.oakgp.GenerationProcessor;
import org.oakgp.NodeEvolver;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.TerminalSet;
import org.oakgp.TestDataFitnessFunction;
import org.oakgp.mutate.PointMutation;
import org.oakgp.mutate.SubtreeCrossover;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/**
 * Performs full genetic programming runs without relying on any mock objects.
 * <p>
 * Would be better to have in a separate "system-test" directory under the "src" directory - or in a completely separate Git project (that has this project as a
 * dependency). Leaving here for the moment as it provides a convenient mechanism to perform a full test of the process. TODO decide long-term solution for this
 * </p>
 */
public class FitnessFunctionSystemTest {
	@Test
	public void testTwoVariableArithmeticExpression() {
		ConstantNode[] constants = createConstants(11);
		int numVariables = 2;
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, numVariables, constants);
		FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
			int x = (int) a.get(0);
			int y = (int) a.get(1);
			return (x * x) + 2 * y + 3 * x + 5;
		}));
		List<Node> initialGeneration = createInitialGeneration(ARITHMETIC_FUNCTION_SET, terminalSet, GENERATION_SIZE);
		doIt(ARITHMETIC_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
	}

	@Test
	public void testThreeVariableArithmeticExpression() {
		ConstantNode[] constants = createConstants(11);
		int numVariables = 3;
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, numVariables, constants);
		FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
			int x = (int) a.get(0);
			int y = (int) a.get(1);
			int z = (int) a.get(2);
			return (x * -3) + (y * 5) - z;
		}));
		List<Node> initialGeneration = createInitialGeneration(ARITHMETIC_FUNCTION_SET, terminalSet, GENERATION_SIZE);
		doIt(ARITHMETIC_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
	}

	@Test
	public void testTwoVariableBooleanLogicExpression() {
		ConstantNode[] constants = createConstants(5);
		int numVariables = 2;
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, numVariables, constants);
		FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
			int x = (int) a.get(0);
			int y = (int) a.get(1);
			return x > 20 ? x : y;
		}));
		List<Node> initialGeneration = createInitialGeneration(COMPARISON_FUNCTION_SET, terminalSet, GENERATION_SIZE);
		doIt(COMPARISON_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
	}

	private static Map<Assignments, Integer> createTests(int numVariables, Function<Assignments, Integer> f) {
		Map<Assignments, Integer> tests = new HashMap<>();
		for (int i = 0; i < 200; i++) {
			Object[] inputs = createInputs(numVariables);
			Assignments assignments = createAssignments(inputs);
			tests.put(assignments, f.apply(assignments));
		}
		return tests;
	}

	private static Object[] createInputs(int numVariables) {
		Object[] variables = new Object[numVariables];
		for (int i = 0; i < numVariables; i++) {
			variables[i] = RANDOM.nextInt(40);
		}
		return variables;
	}

	private void doIt(FunctionSet functionSet, TerminalSet terminalSet, FitnessFunction fitnessFunction, List<Node> initialGeneration) {
		Predicate<List<RankedCandidate>> terminator = createTerminator();
		Map<NodeEvolver, Long> nodeEvolvers = createNodeEvolvers(functionSet, terminalSet);
		FitnessFunction fitnessFunctionCache = new FitnessFunctionCache(GENERATION_SIZE, fitnessFunction);
		GenerationProcessor generationProcessor = new FitnessFunctionGenerationProcessor(fitnessFunctionCache);
		RankedCandidate best = Runner.process(generationProcessor, new GenerationEvolver(SELECTOR_FACTORY, nodeEvolvers), terminator, initialGeneration);
		printRankedCandidate(best);
	}

	private Map<NodeEvolver, Long> createNodeEvolvers(FunctionSet functionSet, TerminalSet terminalSet) {
		Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
		nodeEvolvers.put(t -> makeRandomTree(functionSet, terminalSet, 4), 5L);
		nodeEvolvers.put(new SubtreeCrossover(RANDOM), 22L);
		nodeEvolvers.put(new PointMutation(RANDOM, functionSet, terminalSet), 22L);
		return nodeEvolvers;
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
