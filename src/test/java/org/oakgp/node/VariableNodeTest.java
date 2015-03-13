package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Assignments;

public class VariableNodeTest {
	@Test
	public void testGetters() {
		final int id = 7;
		final VariableNode v = new VariableNode(id);
		assertEquals(id, v.getId());
		assertEquals(1, v.getNodeCount());
		assertSame(INTEGER, v.getType());
	}

	@Test
	public void testToString() {
		assertEquals("p5", new VariableNode(5).toString());
	}

	@Test
	public void testEvaluate() {
		final int expected = 9;
		final VariableNode v = new VariableNode(0);
		final Assignments assignments = createAssignments(expected);
		final int actual = v.evaluate(assignments);
		assertEquals(expected, actual);
	}

	@Test
	public void testReplaceAt() {
		final VariableNode v = new VariableNode(0);
		final ConstantNode c = new ConstantNode(Integer.MAX_VALUE);
		assertSame(v, v.replaceAt(0, t -> t));
		assertSame(c, v.replaceAt(0, t -> c));
	}

	@Test
	public void testEqualsAndHashCode() {
		final VariableNode n1 = new VariableNode(1);
		final VariableNode n2 = new VariableNode(1);
		assertNotSame(n1, n2);
		assertEquals(n1, n1);
		assertEquals(n1.hashCode(), n2.hashCode());
		assertEquals(n1, n2);
	}

	@Test
	public void testNotEquals() {
		final VariableNode n = new VariableNode(1);
		assertNotEquals(n, new VariableNode(0));
		assertNotEquals(n, new VariableNode(2));
		assertNotEquals(n, new ConstantNode(1));
		assertNotEquals(n, new Integer(1));
	}
}
