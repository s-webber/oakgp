package org.oakgp.examples.gridwar;

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
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.GenerationEvolver;
import org.oakgp.GenerationProcessor;
import org.oakgp.NodeEvolver;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.TerminalSet;
import org.oakgp.mutate.PointMutation;
import org.oakgp.mutate.SubtreeCrossover;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.tournament.RoundRobinTournament;

public class GridWarSystemTest {
	private static final int NUM_GENERATIONS = 250;
	private static final int NUM_VARIABLES = 5;
	private static final int NUM_CONSTANTS = 5;

	@Test
	public void test() {
		// set-up
		ConstantNode[] constants = createConstants(NUM_CONSTANTS);
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, NUM_VARIABLES, constants);
		List<Node> initialGeneration = createInitialGeneration(COMPARISON_FUNCTION_SET, terminalSet, GENERATION_SIZE);
		Map<NodeEvolver, Long> nodeEvolvers = createNodeEvolvers(terminalSet);
		Predicate<List<RankedCandidate>> terminator = createTerminator();

		// run process
		GenerationProcessor generationProcessor = new RoundRobinTournament(new GridWar(RANDOM));
		RankedCandidate best = Runner.process(generationProcessor, new GenerationEvolver(SELECTOR_FACTORY, nodeEvolvers), terminator, initialGeneration);

		// print best
		printRankedCandidate(best);
	}

	private Map<NodeEvolver, Long> createNodeEvolvers(TerminalSet terminalSet) {
		Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
		nodeEvolvers.put(t -> makeRandomTree(COMPARISON_FUNCTION_SET, terminalSet, 4), 5L);
		nodeEvolvers.put(new SubtreeCrossover(RANDOM), 22L);
		nodeEvolvers.put(new PointMutation(RANDOM, COMPARISON_FUNCTION_SET, terminalSet), 22L);
		return nodeEvolvers;
	}

	private Predicate<List<RankedCandidate>> createTerminator() {
		return new Predicate<List<RankedCandidate>>() {
			int ctr = 1;

			@Override
			public boolean test(List<RankedCandidate> t) {
				if (ctr % 50 == 0) {
					System.out.println(ctr);
				}
				return ctr++ > NUM_GENERATIONS;
			}
		};
	}
}
