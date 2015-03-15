package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.assertCanSimplify;
import static org.oakgp.TestUtils.assertCannotSimplify;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;

public class AddTest {
	private final Operator add = new Add();

	@Test
	public void testEvaluate() {
		Arguments arguments = createArguments(new ConstantNode(3), new ConstantNode(21));
		Assignments assignments = createAssignments();
		assertEquals(24, add.evaluate(arguments, assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = add.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "v1";
		// anything plus zero is itself
		assertCanSimplify(add, readNode(arg), createArguments(arg, "0"));
		assertCanSimplify(add, readNode(arg), createArguments("0", arg));
	}

	@Test
	public void testCannotSimplify() {
		String arg = "v1";
		assertCannotSimplify(add, createArguments(arg, "1"));
		assertCannotSimplify(add, createArguments(arg, "-1"));
	}
}