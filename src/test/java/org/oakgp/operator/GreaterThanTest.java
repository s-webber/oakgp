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

public class GreaterThanTest {
	private final Operator greaterThan = new GreaterThan();

	@Test
	public void testEvaluate() {
		Assignments assignments = createAssignments();
		assertSame(FALSE, greaterThan.evaluate(createArguments("7", "8"), assignments));
		assertSame(FALSE, greaterThan.evaluate(createArguments("8", "8"), assignments));
		assertSame(TRUE, greaterThan.evaluate(createArguments("9", "8"), assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = greaterThan.getSignature();
		assertSame(BOOLEAN, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "v1";
		assertCanSimplify(greaterThan, createConstant(FALSE), createArguments(arg, arg));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(greaterThan, createArguments("v1", "8"));
		assertCannotSimplify(greaterThan, createArguments("8", "v1"));
		assertCannotSimplify(greaterThan, createArguments("v0", "v1"));
	}
}
