package org.oakgp.operator;

import static org.oakgp.TestUtils.readNode;

import java.util.List;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

public class SubtractTest extends AbstractOperatorTest {
	@Override
	protected Operator getOperator() {
		return new Subtract();
	}

	@Override
	protected void getEvaluateTests(EvaluateTestCases testCases) {
		testCases.put("(- 3 21)", -18);

	}

	@Override
	protected void getCanSimplifyTests(SimplifyTestCases testCases) {
		Object[][] assignedValues = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

		testCases.put("(- v0 -7)", "(+ 7 v0)", assignedValues);

		// anything minus zero is itself
		testCases.put("(- v0 0)", "v0", assignedValues);
		// anything minus itself is zero
		testCases.put("(- v0 v0)", "0", assignedValues);

		testCases.put("(- 1 (+ 1 v0))", "(- 0 v0)", assignedValues);
		testCases.put("(- 1 (- 1 v0))", "v0", assignedValues);
		testCases.put("(- 6 (+ 4 v0))", "(- 2 v0)", assignedValues);
		testCases.put("(- 6 (- 4 v0))", "(+ 2 v0)", assignedValues);

		// (1 + x) - (2 + y) evaluates to -1+x-y
		testCases.put("(- (+ 1 v0) (+ 2 v1))", "(- (- v0 1) v1)", assignedValues);
		testCases.put("(- (+ 1 v0) (+ 2 v0))", "-1", assignedValues);

		// (1 + x) - (12 - y) evaluates to -11+x+y
		testCases.put("(- (+ 1 v0) (- 12 v1))", "(- (+ v0 v1) 11)", assignedValues);
		testCases.put("(- (+ 1 v0) (- 12 v0))", "(- (* 2 v0) 11)", assignedValues);

		testCases.put("(- (+ 1 v0) v0)", "1", assignedValues);

		testCases.put("(- (- (- (* 2 v0) 9) v1) v1)", "(+ (- (* 2 v0) 9) (* -2 v1))", assignedValues);
		testCases.put("(- (- (+ (* 2 v0) 9) v1) v1)", "(+ (* -2 v1) (+ 9 (* 2 v0)))", assignedValues);

		testCases.put("(- (- (- (* 2 v0) 9) v1) v1)", "(+ (- (* 2 v0) 9) (* -2 v1))", assignedValues);

		testCases.put("(- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v0 2)))", "(- 4 (+ v0 (* 2 v1)))", assignedValues);
		testCases.put("(- (* -2 v0) (- v1 v0))", "(- (* -3 v0) v1)", assignedValues);

		// TODO
		// (- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1))
		// (5-((((2*y)+(2*x))-1)-(x-2)))-(2 * x)
		// -3x+4-2y
		testCases.put("(- (- 5 (- (- (+ (* 2 v0) (* 2 v1)) 1) (- v1 2))) (* 2 v1))", "(- (- 4 (+ v1 (* 2 v0))) (* 2 v1))", assignedValues);

		testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- 8 v1))", "(- (- v0 v1) (- 8 v1))", assignedValues);
		testCases.put("(- (+ 9 (- v0 (+ 9 v1))) (- v1 v0))", "(- (- v0 v1) (- v1 v0))", assignedValues); // TODO can this be simplified further?
	}

	@Test
	public void testReplace() {
		assertReplace("(- v0 v1)", "v0");
		assertReplace("(- v0 v1)", "v1");
		assertReplace("(+ v0 v1)", "v0");
		assertReplace("(+ v0 v1)", "v1");
		assertReplace("(- 9 (+ v0 v1))", "v0");
		assertReplace("(- 9 (+ v0 v1))", "v1");
		assertReplace("(- 9 (- v0 v1))", "v0");
		assertReplace("(- 9 (- v0 v1))", "v1");
		assertReplace("(+ 9 (+ v0 v1))", "v0");
		assertReplace("(+ 9 (+ v0 v1))", "v1");
		assertReplace("(+ 9 (- v0 v1))", "v0");
		assertReplace("(+ 9 (- v0 v1))", "v1");
		assertReplace("(+ 9 (- (- 9 v0) v1))", "v0");
		assertReplace("(+ 9 (- (- 9 v0) v1))", "v1");
		assertReplace("(+ 9 (- v0 (- 9 v1)))", "v0");
		assertReplace("(+ 9 (- v0 (- 9 v1)))", "v1");
		assertReplace("(- 9 (- (- 9 v0) v1))", "v0");
		assertReplace("(- 9 (- (- 9 v0) v1))", "v1");
		assertReplace("(- 9 (- v0 (- 9 v1)))", "v0");
		assertReplace("(- 9 (- v0 (- 9 v1)))", "v1");
		assertReplace("(+ 9 (- (+ 9 v0) v1))", "v0");
		assertReplace("(+ 9 (- (+ 9 v0) v1))", "v1");
		assertReplace("(+ 9 (- v0 (+ 9 v1)))", "v0");
		assertReplace("(+ 9 (- v0 (+ 9 v1)))", "v1");
	}

	private void assertReplace(String arg1, String arg2) {
		FunctionNode fn = (FunctionNode) readNode(arg1);
		Node nodeToReplace = readNode(arg2);
		Node n = Subtract.replace(fn, nodeToReplace, true);
		if (!n.equals(fn)) {
			int[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };
			for (int[] assignedValue : assignedValues) {
				Assignments assignments = Assignments.createAssignments(assignedValue[0], assignedValue[1]);
				FunctionNode x = new FunctionNode(getOperator(), Arguments.createArguments(fn, nodeToReplace));
				if (!x.evaluate(assignments).equals(n.evaluate(assignments))) {
					throw new RuntimeException(new NodeWriter().writeNode(n));
				}
			}
		}
	}

	@Override
	protected void getCannotSimplifyTests(List<String> testCases) {
		testCases.add("(- v0 1)");
		testCases.add("(- 0 v0)");
	}
}
