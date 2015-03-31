package org.oakgp.fitness;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.createConstant;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.oakgp.node.Node;

public class FitnessFunctionCacheTest {
	@Test
	public void test() {
		// set-up expectations
		final Node n1 = createConstant(1);
		final Node n2 = createConstant(2);
		final double fitness1 = 9;
		final double fitness2 = -3;
		final AtomicInteger evaluateCtr = new AtomicInteger(0);
		final FitnessFunction mockFitnessFunction = (n) -> {
			evaluateCtr.incrementAndGet();
			if (n == n1) {
				return fitness1;
			} else if (n == n2) {
				return fitness2;
			} else {
				throw new IllegalArgumentException();
			}
		};

		// create object to test
		final FitnessFunctionCache cache = new FitnessFunctionCache(3, mockFitnessFunction);

		// test evaluate is only called once per node
		assertEquals(fitness1, cache.evaluate(n1), 0);
		assertEquals(1, evaluateCtr.get());
		assertEquals(fitness2, cache.evaluate(n2), 0);
		assertEquals(2, evaluateCtr.get());
		assertEquals(fitness1, cache.evaluate(n1), 0);
		assertEquals(fitness2, cache.evaluate(n2), 0);
		assertEquals(2, evaluateCtr.get());
	}
}
