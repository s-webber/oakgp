package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.readNode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oakgp.NodeSimplifier;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public abstract class AbstractOperatorTest {
	@Test
	public final void testEvaluate() {
		EvaluateTestCases testCases = new EvaluateTestCases();
		getEvaluateTests(testCases);
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
		for (SimplifyTest test : testCases.tests) {
			FunctionNode input = readInput(test.input);
			Node expectedResult = readNode(test.expectedOutput);
			Node actualResult = new NodeSimplifier().simplify(input);
			assertEquals(input.toString(), expectedResult, actualResult);
			// TODO assert evaluate() returns same result for both input and actualResult
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
		FunctionNode node = (FunctionNode) readNode(input);
		assertSame(getOperator().getClass(), node.getOperator().getClass());
		return node;
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
			tests.add(new SimplifyTest(input, expectedOutput));
		}
	}

	private static class SimplifyTest {
		final String input;
		final String expectedOutput;

		SimplifyTest(String input, String expectedOutput) {
			this.input = input;
			this.expectedOutput = expectedOutput;
		}
	}
}
