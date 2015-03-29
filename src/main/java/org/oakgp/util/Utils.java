package org.oakgp.util;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

public final class Utils {
	private static final Object[][] TEST_DATA = { { 2, 14, 4, 9, 7 } };

	public static void assertEvaluateToSameResult(Node input, Node output) {
		for (Object[] assignedValues : TEST_DATA) {
			assertEvaluateToSameResult(input, output, assignedValues);
		}
	}

	private static void assertEvaluateToSameResult(Node input, Node output, Object[] assignedValues) {
		Assignments assignments = Assignments.createAssignments(assignedValues);
		if (!input.evaluate(assignments).equals(output.evaluate(assignments))) {
			throw new IllegalArgumentException(new NodeWriter().writeNode(input) + " = " + input.evaluate(assignments) + " "
					+ new NodeWriter().writeNode(output) + " = " + output.evaluate(assignments));
		}
	}
}
