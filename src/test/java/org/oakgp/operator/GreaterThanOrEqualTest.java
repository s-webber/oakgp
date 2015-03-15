package org.oakgp.operator;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.assertCanSimplify;
import static org.oakgp.TestUtils.assertCannotSimplify;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.Signature;

public class GreaterThanOrEqualTest {
	private final Operator greaterThanOrEqual = new GreaterThanOrEqual();

	@Test
	public void testEvaluate() {
		Assignments assignments = createAssignments();
		assertSame(FALSE, greaterThanOrEqual.evaluate(createArguments("7", "8"), assignments));
		assertSame(TRUE, greaterThanOrEqual.evaluate(createArguments("8", "8"), assignments));
		assertSame(TRUE, greaterThanOrEqual.evaluate(createArguments("9", "8"), assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = greaterThanOrEqual.getSignature();
		assertSame(BOOLEAN, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "v1";
		assertCanSimplify(greaterThanOrEqual, createConstant(TRUE), createArguments(arg, arg));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(greaterThanOrEqual, createArguments("v1", "8"));
		assertCannotSimplify(greaterThanOrEqual, createArguments("8", "v1"));
		assertCannotSimplify(greaterThanOrEqual, createArguments("v0", "v1"));
	}
}
