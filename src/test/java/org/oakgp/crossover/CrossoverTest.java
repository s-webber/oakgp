package org.oakgp.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.Random;

public class CrossoverTest { // TODO rename SubtreeCrossoverTest
	@Test
	public void testFunctionNodes() {
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(2)).willReturn(1);
		given(mockRandom.nextInt(5)).willReturn(3);

		DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

		NodeEvolver c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("(org.oakgp.operator.math.Add 9 (org.oakgp.operator.math.Subtract 8 v5))", result.toString());
		assertTrue(dummySelector.isEmpty());
	}

	@Test
	public void testConstantNodes() {
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(1)).willReturn(0);

		DummyNodeSelector dummySelector = new DummyNodeSelector("1", "2");

		NodeEvolver c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("2", result.toString());
		assertTrue(dummySelector.isEmpty());
	}

	/**
	 * Checks that crossover does not happen if it would create a new tree structure with inconsistent types.
	 * <p>
	 * In the following example if crossover did happen then it would break the signature of the {@code Add} operator. i.e. the {@code Add} operator expects
	 * both of its arguments to be of type {@code integer} - but crossover would replace one of the existing arguments with a function node that has a
	 * {@code LessThan} operator - which has a {@code boolean} return type. This test checks we do <i>not</i> create: {@code (+ 9 (< 6 7))}
	 */
	@Test
	public void testMixingTypes() {
		// NOTE: this test checks that crossover does not happen if it would create a new tree structure with inconsistent types.
		// In the following example if crossover did happen then it would break the signature of the Add operator.
		// i.e. the Add operator expects both of its arguments to be of type integer - but crossover would replace one of the existing arguments with a
		// function node that has a LessThan operator - which has a boolean return type
		// e.g. (+ 9 (< 6 7))

		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(2)).willReturn(1);
		given(mockRandom.nextInt(6)).willReturn(2);

		DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(if (< 6 7) 8 9)");

		NodeEvolver c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("(org.oakgp.operator.math.Add 9 5)", result.toString());
		assertEquals(14, result.evaluate(null));
		assertTrue(dummySelector.isEmpty());
	}
}