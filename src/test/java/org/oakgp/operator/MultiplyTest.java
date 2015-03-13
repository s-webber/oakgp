package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.ConstantNode;

public class MultiplyTest {
	private final Multiply multiply = new Multiply();

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
}
