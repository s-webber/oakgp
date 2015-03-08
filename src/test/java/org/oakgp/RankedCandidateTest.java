package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public class RankedCandidateTest {
	@Test
	public void testGetters() {
		Node n = new ConstantNode(0);
		double f = 7.5;
		RankedCandidate a = new RankedCandidate(n, f);
		assertSame(n, a.getNode());
		assertEquals(f, a.getFitness(), 0);
	}

	@Test
	public void testCompareTo() {
		RankedCandidate a = new RankedCandidate(null, 1);
		RankedCandidate b = new RankedCandidate(null, 2);
		RankedCandidate c = new RankedCandidate(null, 1);
		assertEquals(-1, a.compareTo(b));
		assertEquals(1, b.compareTo(a));
		assertEquals(0, a.compareTo(c));
	}
}
