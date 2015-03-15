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
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.node.VariableNode;

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
		VariableNode arg2 = createVariable(0);
		VariableNode arg3 = createVariable(1);
		assertCanSimplify(operator, arg2, Arguments.createArguments(createConstant(TRUE), arg2, arg3));
		assertCanSimplify(operator, arg3, Arguments.createArguments(createConstant(FALSE), arg2, arg3));
		assertCanSimplify(operator, arg2, Arguments.createArguments(createVariable(2), arg2, arg2));
	}

	@Test
	public void testCannotSimplify() {
		assertCannotSimplify(operator, createArguments("v0", "1", "2"));
	}
}
