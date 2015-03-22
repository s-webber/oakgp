package org.oakgp.operator;

import java.util.List;

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
		Object[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

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
	}

	@Override
	protected void getCannotSimplifyTests(List<String> testCases) {
		testCases.add("(- v0 1)");
		testCases.add("(- 0 v0)");
	}
}
