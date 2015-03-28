package org.oakgp.util;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

public final class Utils {
	public static void assertEvaluateToSameResult(Node input, Node output) {
		Assignments assignments = Assignments.createAssignments(0, 1, 2, 3, 1);
		if (!input.evaluate(assignments).equals(output.evaluate(assignments))) {
			throw new IllegalArgumentException(new NodeWriter().writeNode(input) + " = " + input.evaluate(assignments) + " "
					+ new NodeWriter().writeNode(output) + " = " + output.evaluate(assignments));
		}
	}
}
