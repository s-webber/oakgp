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

public class LessThanTest {
	private final Operator lessThan = new LessThan();

	@Test
	public void testEvaluate() {
		Assignments assignments = createAssignments();
		assertEquals(1, lessThan.evaluate(createArguments("7", "8"), assignments));
		assertEquals(0, lessThan.evaluate(createArguments("8", "8"), assignments));
		assertEquals(0, lessThan.evaluate(createArguments("9", "8"), assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = lessThan.getSignature();
		assertSame(BOOLEAN, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "p1";
		assertCanSimplify(lessThan, readNode("0"), createArguments(arg, arg));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(lessThan, createArguments("p1", "8"));
		assertCannotSimplify(lessThan, createArguments("8", "p1"));
		assertCannotSimplify(lessThan, createArguments("p0", "p1"));
	}
}
