package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class ConstantNodeTest {
	@Test
	public void test() {
		final ConstantNode n = new ConstantNode(7);
		assertEquals(1, n.getNodeCount());
		assertEquals("7", n.toString());
	}

	@Test
	public void testEvaluate() {
		int expected = 9;
		ConstantNode n = new ConstantNode(expected);
		int actual = n.evaluate(null);
		assertEquals(expected, actual);
	}

	@Test
	public void testReplaceAt() {
		ConstantNode n1 = new ConstantNode(9);
		ConstantNode n2 = new ConstantNode(5);
		assertEquals(n1, n1.replaceAt(0, t -> t));
		assertEquals(n2, n1.replaceAt(0, t -> n2));
	}

	@Test
	public void testEqualsAndHashCode() {
		final ConstantNode n1 = new ConstantNode(7);
		final ConstantNode n2 = new ConstantNode(7);
		assertNotSame(n1, n2);
		assertEquals(n1, n1);
		assertEquals(n1.hashCode(), n2.hashCode());
		assertEquals(n1, n2);
	}

	@Test
	public void testNotEquals() {
		final ConstantNode n = new ConstantNode(7);
		assertNotEquals(n, new ConstantNode(8));
		assertNotEquals(n, new ConstantNode(-7));
		assertNotEquals(n, new VariableNode(7));
		assertNotEquals(n, new Integer(7));
	}
}
