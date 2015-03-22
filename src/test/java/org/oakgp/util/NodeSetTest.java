package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.readNode;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.oakgp.node.Node;

public class NodeSetTest {
	@Test
	public void testAdd() {
		Set<Node> s = new NodeSet();

		Node n1 = readNode("(+ v0 (+ 3 1))");
		Node n2 = readNode("(+ v0 (- 7 3))");

		assertTrue(s.add(n1));
		assertFalse(s.add(n2));
		assertEquals(1, s.size());
		assertFalse(s.contains(n1));
		assertFalse(s.contains(n2));

		Node simplifiedVersion = readNode("(+ v0 4)");
		assertTrue(s.contains(simplifiedVersion));
		assertFalse(s.add(simplifiedVersion));
		assertEquals(1, s.size());

		Node n3 = readNode("(* v0 4)");
		Node n4 = readNode("(+ v0 5)");
		assertTrue(s.add(n3));
		assertTrue(s.add(n4));

		assertTrue(s.contains(simplifiedVersion));
		assertTrue(s.contains(n3));
		assertTrue(s.contains(n4));
		assertEquals(3, s.size());
	}

	@Test
	public void testAddAll() {
		Node n1 = readNode("(+ v0 (+ 3 1))");
		Node n2 = readNode("(+ v0 (- 7 3))");
		Node n3 = readNode("(* v0 4)");
		Node n4 = readNode("(+ v0 5)");

		Set<Node> s = new NodeSet();
		s.addAll(Arrays.asList(n1, n2, n3, n4));

		Node simplifiedVersion = readNode("(+ v0 4)");
		assertEquals(3, s.size());
		assertTrue(s.contains(simplifiedVersion));
		assertTrue(s.contains(n3));
		assertTrue(s.contains(n4));
	}
}
