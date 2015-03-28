package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

public class ArithmeticExpressionSimplifierTest {
	@Test
	public void test() {
		assertReplace("(* 3 (* -3 v1))", "(* -9 v1)");

		assertReplace("(+ 1 1)", "(+ 1 1)");

		assertAdditionSimplification("v0", "(+ 1 v0)", "(+ 1 (* 2 v0))");

		assertAdditionSimplification("v0", "(+ v1 (+ v1 (+ v0 9)))", "(+ v1 (+ v1 (+ (* 2 v0) 9)))");

		assertAdditionSimplification("v1", "(+ v1 (+ v1 (+ v0 9)))", "(+ (* 2 v1) (+ v1 (+ v0 9)))");

		assertAdditionSimplification("v0", "(* 1 v0)", "(* 2 v0)");

		assertReplace("(- 1 1)", "(- 1 1)");

		assertReplace("(+ v0 (- 1 v0))", "(- 1 0)");

		assertReplace("(- v0 (- v1 (- v0 9)))", "(- 0 (- v1 (- (* 2 v0) 9)))");
		assertReplace("(- v0 (- v1 (- v1 (- v0 9))))", "(- 0 (- v1 (- v1 (- 0 9))))");

		assertAdditionSimplification("9", "(+ v0 3)", "(+ v0 12)");

		assertAdditionSimplification("9", "(- v0 3)", "(- v0 -6)");

		assertReplace("(- 4 (- v1 (- v0 9)))", "(- 0 (- v1 (- v0 5)))");
		assertReplace("(- 4 (- v1 (+ v0 9)))", "(- 0 (- v1 (+ v0 13)))");

		assertReplace("(- (+ 4 v0) 3)", "(+ 1 v0)");
		assertReplace("(- (- v0 1) v1)", "(- (- v0 1) v1)");

		assertReplace("(- (- v0 1) (- v0 1))", "(- (- 0 0) (- 1 1))");
		assertReplace("(- (+ v0 1) (+ v0 1))", "(- (+ 0 0) (+ -1 1))");
		assertReplace("(+ (- v0 1) (- v0 1))", "(+ (- 0 0) (- (* 2 v0) 2))");
		assertReplace("(+ (+ v0 1) (+ v0 1))", "(+ (+ 0 0) (+ (* 2 v0) 2))");
		assertReplace("(- (+ v0 1) (- v0 1))", "(- (+ 0 0) (- -1 1))");
	}

	private void assertAdditionSimplification(String firstArg, String secondArg, String expectedOutput) {
		assertReplace("(+ " + firstArg + " " + secondArg + ")", expectedOutput);
	}

	private void assertReplace(String input, String expectedOutput) {
		Node in = readNode(input);
		Node simplifiedVersion = new ArithmeticExpressionSimplifier().simplify(in);
		String writeNode = new NodeWriter().writeNode(simplifiedVersion);
		assertEquals(expectedOutput, writeNode);
		if (!simplifiedVersion.equals(in)) {
			int[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };
			for (int[] assignedValue : assignedValues) {
				Assignments assignments = Assignments.createAssignments(assignedValue[0], assignedValue[1]);
				if (!in.evaluate(assignments).equals(simplifiedVersion.evaluate(assignments))) {
					throw new RuntimeException(writeNode);
				}
			}
		}
	}
}
