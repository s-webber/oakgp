package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.VariableNode;

public class VariableNodeTest {
	@Test
	public void test() {
		final int id = 7;
		final VariableNode v = new VariableNode(id);
		assertEquals(id, v.getId());
		assertEquals(1, v.getNodeCount());
		assertEquals("p" + id, v.toString());
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
}
