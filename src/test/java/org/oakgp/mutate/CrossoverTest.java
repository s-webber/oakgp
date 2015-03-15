package org.oakgp.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.Random;

public class CrossoverTest {
	@Test
	public void test() {
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(3)).willReturn(1);
		given(mockRandom.nextInt(5)).willReturn(3);

		DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

		SubtreeCrossover c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("(org.oakgp.operator.Add 9 (org.oakgp.operator.Subtract 8 v5))", result.toString());
		assertTrue(dummySelector.isEmpty());
	}

	@Test
	public void testMixingTypes() {
		// TODO get code working as it should, then update this test to reflect the new correct behaviour
		// NOTE: this test demonstrates the current incorrect behaviour of the code.
		// The result breaks the signature of the Add operator.
		// i.e. the Add operator expects both of its arguments to be of type integer - but crossover has replaced one of the existing arguments with a
		// function node that has a LessThan operator - which has a boolean return type
		// (this will be a problem when we stop using int primitives to represent both integer and boolean values)

		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(3)).willReturn(1);
		given(mockRandom.nextInt(6)).willReturn(2);

		DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(if (< 6 7) 8 9)");

		SubtreeCrossover c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("(org.oakgp.operator.Add 9 (org.oakgp.operator.LessThan 6 7))", result.toString());
		assertTrue(dummySelector.isEmpty());
	}
}