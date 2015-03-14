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

public class IfTest {
	private final Operator operator = new If();

	@Test
	public void testEvaluate() {
		Assignments assignments = createAssignments();
		String arg2 = "(+ 1 2)";
		String arg3 = "(* 6 3)";
		assertEquals(3, operator.evaluate(createArguments("(< 8 9)", arg2, arg3), assignments));
		assertEquals(18, operator.evaluate(createArguments("(> 8 9)", arg2, arg3), assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = operator.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(3, signature.getArgumentTypesLength());
		assertSame(BOOLEAN, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
		assertSame(INTEGER, signature.getArgumentType(2));
	}

	@Test
	public void testCanSimplify() {
		String arg2 = "p0";
		String arg3 = "p1";
		assertCanSimplify(operator, readNode(arg2), createArguments("1", arg2, arg3));
		assertCanSimplify(operator, readNode(arg3), createArguments("0", arg2, arg3));
		assertCanSimplify(operator, readNode(arg2), createArguments("p0", arg2, arg2));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(operator, createArguments("p0", "1", "2"));
	}
}
