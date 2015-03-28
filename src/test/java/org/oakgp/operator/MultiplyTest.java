package org.oakgp.operator;

import java.util.List;

public class MultiplyTest extends AbstractOperatorTest {
	@Override
	protected Operator getOperator() {
		return new Multiply();
	}

	@Override
	protected void getEvaluateTests(EvaluateTestCases t) {
		t.put("(* 3 21)", 63);
	}

	@Override
	protected void getCanSimplifyTests(SimplifyTestCases t) {
		t.put("(* v1 2)", "(* 2 v1)");
		t.put("(* v1 v0)", "(* v0 v1)");

		// anything multiplied by zero is zero
		t.put("(* v1 0)", "0");
		t.put("(* 0 v1)", "0");

		// anything multiplied by one is itself
		t.put("(* v1 1)", "v1");
		t.put("(* 1 v1)", "v1");
	}

	@Override
	protected void getCannotSimplifyTests(List<String> t) {
		t.add("(* 2 v1)");
		t.add("(* -1 v1)");
		t.add("(* v1 v2)");
	}
}
