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

		DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 p5))");

		SubtreeCrossover c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(dummySelector);
		assertEquals("(org.oakgp.operator.Add 9 (org.oakgp.operator.Subtract 8 p5))", result.toString());
		assertTrue(dummySelector.isEmpty());
	}
}