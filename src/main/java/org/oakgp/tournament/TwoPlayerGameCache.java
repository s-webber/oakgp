package org.oakgp.tournament;

import static org.oakgp.CacheMap.createCache;

import java.util.Map;

import org.oakgp.node.Node;

public final class TwoPlayerGameCache implements TwoPlayerGame {
	private final TwoPlayerGame twoPlayerGame;
	private final Map<NodePair, Double> cache;

	public TwoPlayerGameCache(int maxSize, TwoPlayerGame twoPlayerGame) {
		this.twoPlayerGame = twoPlayerGame;
		this.cache = createCache(maxSize);
	}

	@Override
	public double evaluate(Node player1, Node player2) {
		NodePair pair = new NodePair(player1, player2);
		Double result = cache.get(pair);
		if (result == null) {
			result = twoPlayerGame.evaluate(player1, player2);
			cache.put(new NodePair(player2, player1), -result);
			cache.put(pair, result);
		}
		return result;
	}

	private static class NodePair {
		private final Node n1;
		private final Node n2;

		public NodePair(Node n1, Node n2) {
			this.n1 = n1;
			this.n2 = n2;
		}

		@Override
		public boolean equals(Object o) {
			NodePair p = (NodePair) o;
			return n1.equals(p.n1) && n2.equals(p.n2);
		}

		@Override
		public int hashCode() {
			return n1.hashCode() + n2.hashCode();
		}
	}
}
