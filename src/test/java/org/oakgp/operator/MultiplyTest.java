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
import static org.oakgp.operator.ArithmeticOperator.ZERO;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;

public class MultiplyTest {
	private final Operator multiply = new Multiply();

	@Test
	public void testEvaluate() {
		Arguments arguments = createArguments(new ConstantNode(3), new ConstantNode(21));
		Assignments assignments = createAssignments();
		assertEquals(63, multiply.evaluate(arguments, assignments));
	}

	@Test
	public void testGetSignature() {
		Signature signature = multiply.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testCanSimplify() {
		String arg = "p1";
		// anything multiplied by zero is zero
		assertCanSimplify(multiply, ZERO, createArguments(arg, "0"));
		assertCanSimplify(multiply, ZERO, createArguments("0", arg));
		// anything multiplied by one is itself
		assertCanSimplify(multiply, readNode(arg), createArguments(arg, "1"));
		assertCanSimplify(multiply, readNode(arg), createArguments("1", arg));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(multiply, createArguments("2", "p1"));
		assertCannotSimplify(multiply, createArguments("p1", "2"));
		assertCannotSimplify(multiply, createArguments("-1", "p1"));
		assertCannotSimplify(multiply, createArguments("p1", "-1"));
		assertCannotSimplify(multiply, createArguments("p1", "p2"));
	}
}
