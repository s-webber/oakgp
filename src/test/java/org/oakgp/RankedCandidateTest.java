package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class RankedCandidateTest {
	@Test
	public void testGetters() {
		Node n = createConstant(0);
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

	@Test
	public void testEquals() {
		double f = 7.5;
		RankedCandidate a = new RankedCandidate(createConstant(0), f);
		RankedCandidate b = new RankedCandidate(createConstant(0), f);
		RankedCandidate c = new RankedCandidate(createConstant(0), f * 2);
		RankedCandidate d = new RankedCandidate(createConstant(7), f);

		assertTrue(a.equals(a));
		assertEquals(a.hashCode(), a.hashCode());

		assertTrue(a.equals(b));
		assertEquals(a.hashCode(), b.hashCode());

		assertFalse(a.equals(c));

		assertFalse(a.equals(d));
	}

	@Test
	public void testToString() {
		RankedCandidate rankedCandidate = new RankedCandidate(readNode("(+ 2 v0)"), 85.75);
		assertEquals("[(org.oakgp.operator.math.Add 2 v0) 85.75]", rankedCandidate.toString());
	}
}
