package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public abstract class AbstractOperatorTest {
	@Test
	public final void testEvaluate() {
		EvaluateTestCases testCases = new EvaluateTestCases();
		getEvaluateTests(testCases);
		assertFalse(testCases.tests.isEmpty());
		for (EvaluateTest test : testCases.tests) {
			FunctionNode input = readInput(test.input);
			// TODO pass Assignments into evaluate()
			Object actualResult = input.evaluate(null);
			assertEquals(test.expectedOutput, actualResult);
		}
	}

	@Test
	public final void testCanSimplify() {
		SimplifyTestCases testCases = new SimplifyTestCases();
		getCanSimplifyTests(testCases);
		assertFalse(testCases.tests.isEmpty());
		for (SimplifyTest test : testCases.tests) {
			FunctionNode input = readInput(test.input);
			Node expectedResult = readNode(test.expectedOutput);
			Node actualResult = new NodeSimplifier().simplify(input);
			assertEquals(writeNode(expectedResult), writeNode(actualResult));
			assertEquals(writeNode(input), expectedResult, actualResult);
			assertSame(actualResult, new NodeSimplifier().simplify(actualResult));
			assertEquals(actualResult, new NodeSimplifier().simplify(input));

			assertNodesEvaluateSameOutcome(test, input, actualResult);
		}
	}

	private void assertNodesEvaluateSameOutcome(SimplifyTest test, Node input, Node actualResult) {
		for (Object[] a : test.assignedValues) {
			Assignments assignments = Assignments.createAssignments(a);
			Object expectedOutcome = input.evaluate(assignments);
			Object actualOutcome = actualResult.evaluate(assignments);
			assertEquals(writeNode(actualResult), expectedOutcome, actualOutcome);
		}
	}

	@Test
	public final void testCannotSimplify() {
		List<String> l = new ArrayList<>();
		getCannotSimplifyTests(l);
		for (String s : l) {
			FunctionNode input = readInput(s);
			Node actualResult = new NodeSimplifier().simplify(input);
			assertSame(input, actualResult);
		}
	}

	private FunctionNode readInput(String input) {
		Node node = readNode(input);
		assertSame(node.toString(), FunctionNode.class, node.getClass());
		FunctionNode functionNode = (FunctionNode) node;
		assertSame(getOperator().getClass(), functionNode.getOperator().getClass());
		return functionNode;
	}

	protected abstract Operator getOperator();

	protected abstract void getEvaluateTests(EvaluateTestCases testCases);

	protected abstract void getCanSimplifyTests(SimplifyTestCases testCases);

	protected abstract void getCannotSimplifyTests(List<String> testCases);

	protected static class EvaluateTestCases {
		private List<EvaluateTest> tests = new ArrayList<>();

		public void put(String input, Object expectedOutput) {
			tests.add(new EvaluateTest(input, expectedOutput));
		}
	}

	private static class EvaluateTest {
		final String input;
		final Object expectedOutput;

		EvaluateTest(String input, Object expectedOutput) {
			this.input = input;
			this.expectedOutput = expectedOutput;
		}
	}

	protected static class SimplifyTestCases {
		private List<SimplifyTest> tests = new ArrayList<>();

		public void put(String input, String expectedOutput) {
			put(input, expectedOutput, new Object[0][0]);
		}

		public void put(String input, String expectedOutput, Object[] assignedValues) {
			put(input, expectedOutput, new Object[][] { assignedValues });
		}

		public void put(String input, String expectedOutput, Object[][] assignedValues) {
			tests.add(new SimplifyTest(input, expectedOutput, assignedValues));
		}
	}

	private static class SimplifyTest {
		final String input;
		final String expectedOutput;
		final Object[][] assignedValues;

		SimplifyTest(String input, String expectedOutput, Object[][] assignedValues) {
			this.input = input;
			this.expectedOutput = expectedOutput;
			this.assignedValues = assignedValues;
		}
	}
}
