package org.oakgp.tournament;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public class TwoPlayerGameCacheTest {
	@Test
	public void test() {
		// set-up expectations
		final Node n1 = new ConstantNode(1);
		final Node n2 = new ConstantNode(2);
		final Node n3 = new ConstantNode(3);
		final double fitness1 = 7;
		final double fitness2 = 4;
		final AtomicInteger evaluateCtr = new AtomicInteger(0);
		final TwoPlayerGame mockTwoPlayerGame = (player1, player2) -> {
			evaluateCtr.incrementAndGet();
			if (n1 == player1 && n2 == player2) {
				return fitness1;
			} else if (n1 == player1 && n3 == player2) {
				return fitness2;
			} else {
				throw new IllegalArgumentException();
			}
		};

		// create object to test
		final TwoPlayerGameCache cache = new TwoPlayerGameCache(3, mockTwoPlayerGame);

		// test evaluate is only called once per node-pair
		assertEquals(fitness1, cache.evaluate(n1, n2), 0);
		assertEquals(-fitness1, cache.evaluate(n2, n1), 0);
		assertEquals(-fitness1, cache.evaluate(n2, n1), 0);
		assertEquals(fitness1, cache.evaluate(n1, n2), 0);
		assertEquals(1, evaluateCtr.get());

		assertEquals(fitness2, cache.evaluate(n1, n3), 0);
		assertEquals(-fitness2, cache.evaluate(n3, n1), 0);
		assertEquals(fitness2, cache.evaluate(n1, n3), 0);
		assertEquals(-fitness2, cache.evaluate(n3, n1), 0);
		assertEquals(2, evaluateCtr.get());
	}
}
