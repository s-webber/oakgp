package org.oakgp.operator;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;
import static org.junit.Assert.assertEquals;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.ConstantNode;
import org.junit.Test;

public class SubtractTest {
	@Test
	public void test() {
		Subtract f = new Subtract();
		Arguments arguments = createArguments(new ConstantNode(3), new ConstantNode(21));
		Assignments assignments = createAssignments();
		assertEquals(-18, f.evaluate(arguments, assignments));
	}
}
