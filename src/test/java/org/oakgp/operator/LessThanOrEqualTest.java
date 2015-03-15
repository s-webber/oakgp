package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.assertCanSimplify;
import static org.oakgp.TestUtils.assertCannotSimplify;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.Signature;

public class LessThanOrEqualTest {
	private final Operator lessThanOrEqual = new LessThanOrEqual();

	@Test
	public void testEvaluate() {
		Assignments assignments = createAssignments();
		assertEquals(1, lessThanOrEqual.evaluate(createArguments("7", "8"), assignments));
		assertEquals(1, lessThanOrEqual.evaluate(createArguments("8", "8"), assignments));
		assertEquals(0, lessThanOrEqual.evaluate(createArguments("9", "8"), assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = lessThanOrEqual.getSignature();
		assertSame(BOOLEAN, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "v1";
		assertCanSimplify(lessThanOrEqual, readNode("1"), createArguments(arg, arg));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(lessThanOrEqual, createArguments("v1", "8"));
		assertCannotSimplify(lessThanOrEqual, createArguments("8", "v1"));
		assertCannotSimplify(lessThanOrEqual, createArguments("v0", "v1"));
	}
}
