package org.oakgp.operator;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.List;

public class EqualTest extends AbstractOperatorTest {
	@Override
	protected Operator getOperator() {
		return new Equal();
	}

	@Override
	protected void getEvaluateTests(EvaluateTestCases t) {
		t.put("(= 7 8)", FALSE);
		t.put("(= 8 8)", TRUE);
		t.put("(= 9 8)", FALSE);
	}

	@Override
	protected void getCanSimplifyTests(SimplifyTestCases t) {
		t.put("(= v1 v1)", "true");
		t.put("(= 8 7)", "false");
		t.put("(= 8 8)", "true");
		t.put("(= 8 9)", "false");
	}

	@Override
	protected void getCannotSimplifyTests(List<String> t) {
		t.add("(= v1 8)");
		t.add("(= 8 v1)");
		t.add("(= v0 v1)");
	}
}
