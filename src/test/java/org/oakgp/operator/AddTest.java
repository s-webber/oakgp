package org.oakgp.operator;

import java.util.List;

public class AddTest extends AbstractOperatorTest {
	@Override
	protected Operator getOperator() {
		return new Add();
	}

	@Override
	protected void getEvaluateTests(EvaluateTestCases testCases) {
		testCases.put("(+ 3 21)", 24);
	}

	@Override
	protected void getCanSimplifyTests(SimplifyTestCases testCases) {
		Object[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

		// anything plus zero is itself
		testCases.put("(+ v1 0)", "v1", assignedValues);
		testCases.put("(+ 0 v1)", "v1", assignedValues);

		testCases.put("(+ v1 v1)", "(* 2 v1)", assignedValues);

		testCases.put("(+ 1 (+ 1 v0))", "(+ 2 v0)", assignedValues);
		testCases.put("(+ 1 (- 1 v0))", "(- 2 v0)", assignedValues);
		testCases.put("(+ 6 (+ 4 v0))", "(+ 10 v0)", assignedValues);
		testCases.put("(+ 6 (- 4 v0))", "(- 10 v0)", assignedValues);
		testCases.put("(+ (+ 1 v0) (+ 2 v0))", "(+ 3 (+ v0 v0))", assignedValues);
		testCases.put("(+ (+ 3 v0) (+ 4 v0))", "(+ 7 (+ v0 v0))", assignedValues);
		testCases.put("(+ (+ v0 3) (+ v0 4))", "(+ 7 (+ v0 v0))", assignedValues);
		testCases.put("(+ (+ v0 3) (+ 4 v0))", "(+ 7 (+ v0 v0))", assignedValues);
		testCases.put("(+ (+ 3 v0) (+ v0 4))", "(+ 7 (+ v0 v0))", assignedValues);
		testCases.put("(+ v0 (* 2 v0))", "(* 3 v0)", assignedValues);
		testCases.put("(+ v0 (* 14 v0))", "(* 15 v0)", assignedValues);
		testCases.put("(+ 2 (+ (* 2 v0) 8))", "(+ 10 (* 2 v0))", assignedValues);
		testCases.put("(+ 2 (+ 8 (* 2 v0)))", "(+ 10 (* 2 v0))", assignedValues);
		testCases.put("(+ v0 (+ v0 8))", "(+ (* 2 v0) 8)", assignedValues);
		testCases.put("(+ v0 (+ 8 v0))", "(+ (* 2 v0) 8)", assignedValues);
		testCases.put("(+ v0 (- v0 2))", "(- (* 2 v0) 2)", assignedValues);
		testCases.put("(+ v0 (- 2 v0))", "(- (+ v0 2) v0)", assignedValues);
	}

	@Override
	protected void getCannotSimplifyTests(List<String> testCases) {
		testCases.add("(+ v0 1)");
		testCases.add("(+ v0 -1)");
		testCases.add("(+ v0 (* v1 v0))");
	}
}