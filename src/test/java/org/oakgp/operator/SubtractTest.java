package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.assertCanSimplify;
import static org.oakgp.TestUtils.assertCannotSimplify;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.INTEGER;
import static org.oakgp.operator.ArithmeticOperator.ZERO;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;

public class SubtractTest {
	private final Subtract subtract = new Subtract();

	@Test
	public void testEvaluate() {
		Arguments arguments = createArguments("3", "21");
		Assignments assignments = createAssignments();
		assertEquals(-18, subtract.evaluate(arguments, assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = subtract.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "p1";
		// anything minus zero is itself
		assertCanSimplify(subtract, readNode(arg), createArguments(arg, "0"));
		// anything minus itself is zero
		assertCanSimplify(subtract, ZERO, createArguments(arg, arg));
	}

	@Test
	public void testCannotSimplify() {
		String arg = "p1";
		assertCannotSimplify(subtract, createArguments(arg, "1"));
		assertCannotSimplify(subtract, createArguments(arg, "-1"));
		assertCannotSimplify(subtract, createArguments("0", arg));
	}
}
