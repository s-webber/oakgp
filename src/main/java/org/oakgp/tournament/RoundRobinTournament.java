package org.oakgp.tournament;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.GenerationProcessor;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;

/** Ranks and sorts the fitness of {@code Node} instances using a {@code TwoPlayerGame} in a round-robin tournament. */
public final class RoundRobinTournament implements GenerationProcessor {
	private final TwoPlayerGame game;

	public RoundRobinTournament(TwoPlayerGame game) {
		this.game = game;
	}

	@Override
	public List<RankedCandidate> process(Collection<Node> input) {
		Node[] inputAsArray = input.toArray(new Node[input.size()]);
		double[] fitness = evaluateFitness(inputAsArray);
		return toRankedCandidates(inputAsArray, fitness);
	}

	private double[] evaluateFitness(Node[] input) {
		int size = input.length;
		double[] fitness = new double[size];
		for (int i1 = 0; i1 < size - 1; i1++) {
			Node player1 = input[i1];
			for (int i2 = i1 + 1; i2 < size; i2++) {
				Node player2 = input[i2];
				double result = game.evaluate(player1, player2);
				fitness[i1] += result;
				fitness[i2] += -result;
			}
		}
		return fitness;
	}

	private List<RankedCandidate> toRankedCandidates(Node[] input, double[] fitness) {
		int size = fitness.length;
		List<RankedCandidate> output = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			output.add(new RankedCandidate(input[i], fitness[i]));
		}
		sort(output, reverseOrder());
		return output;
	}
}
