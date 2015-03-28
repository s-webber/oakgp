package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Type;

public class ConstantNodeTest {
	@Test
	public void testGetters() {
		final ConstantNode n = createConstant(7);
		assertEquals(1, n.getNodeCount());
		assertSame(INTEGER, n.getType());
	}

	@Test
	public void testToString() {
		assertEquals("5", createConstant(5).toString());
	}

	@Test
	public void testEvaluate() {
		Integer expected = 9;
		ConstantNode n = createConstant(expected);
		Object actual = n.evaluate(null);
		assertSame(expected, actual);
	}

	@Test
	public void testReplaceAt() {
		ConstantNode n1 = createConstant(9);
		ConstantNode n2 = createConstant(5);
		assertEquals(n1, n1.replaceAt(0, t -> t));
		assertEquals(n2, n1.replaceAt(0, t -> n2));
	}

	@Test
	public void testEqualsAndHashCodeIntegers() {
		final ConstantNode n1 = createConstant(7);
		final ConstantNode n2 = createConstant(7);
		assertNotSame(n1, n2);
		assertEquals(n1, n1);
		assertEquals(n1.hashCode(), n2.hashCode());
		assertEquals(n1, n2);
	}

	@Test
	public void testEqualsAndHashCodeStrings() {
		String a = new String("hello");
		String b = new String("hello");
		assertNotSame(a, b);

		final ConstantNode n1 = new ConstantNode(a, Type.STRING);
		final ConstantNode n2 = new ConstantNode(b, Type.STRING);
		assertNotSame(n1, n2);
		assertEquals(n1, n1);
		assertEquals(n1.hashCode(), n2.hashCode());
		assertEquals(n1, n2);
	}

	@Test
	public void testNotEquals() {
		final ConstantNode n = createConstant(7);
		assertNotEquals(n, createConstant(8));
		assertNotEquals(n, createConstant(-7));
		assertNotEquals(n, createVariable(7));
		assertNotEquals(n, new ConstantNode("7", Type.STRING));
		assertNotEquals(n, new Integer(7));
	}
}
