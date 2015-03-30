package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class ArithmeticExpressionSimplifierTest {
	@Test
	public void testCreateConstant() {
		assertCreateConstant(0);
		assertCreateConstant(1);
		assertCreateConstant(-1);
		assertCreateConstant(7);
		assertCreateConstant(-7);
		assertCreateConstant(Integer.MAX_VALUE);
		assertCreateConstant(Integer.MIN_VALUE);
	}

	private void assertCreateConstant(int value) {
		ConstantNode n = ArithmeticExpressionSimplifier.createConstant(value);
		assertEquals(value, (int) n.evaluate(null));
		assertSame(Type.INTEGER, n.getType());
	}

	@Test
	public void testMultiplyByTwo() {
		assertMultiplyByTwo("v0", "(* 2 v0)");
		assertMultiplyByTwo("(+ v0 v1)", "(* 2 (+ v0 v1))");
	}

	private void assertMultiplyByTwo(String before, String after) {
		Node input = readNode(before);
		Node output = readNode(after);
		assertEquals(output, ArithmeticExpressionSimplifier.multiplyByTwo(input));
	}

	@Test
	public void testIsAdd() {
		assertIsAdd("(+ 1 2)", true);
		assertIsAdd("(- 1 2)", false);
		assertIsAdd("(* 1 2)", false);
		assertIsAdd("(if 1 2)", false);
		assertIsAdd("1", false);
		assertIsAdd("true", false);
	}

	private void assertIsAdd(String input, boolean expectedResult) {
		Node n = readNode(input);
		if (n instanceof FunctionNode) {
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isAdd((FunctionNode) n));
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isAdd(((FunctionNode) n).getOperator()));
		} else {
			assertFalse(expectedResult);
		}
	}

	@Test
	public void testIsSubtract() {
		assertIsSubtract("(- 1 2)", true);
		assertIsSubtract("(+ 1 2)", false);
		assertIsSubtract("(* 1 2)", false);
		assertIsSubtract("(if 1 2)", false);
		assertIsSubtract("1", false);
		assertIsSubtract("true", false);
	}

	private void assertIsSubtract(String input, boolean expectedResult) {
		Node n = readNode(input);
		assertEquals(expectedResult, ArithmeticExpressionSimplifier.isSubtract(n));
		if (n instanceof FunctionNode) {
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isSubtract((FunctionNode) n));
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isSubtract(((FunctionNode) n).getOperator()));
		} else {
			assertFalse(expectedResult);
		}
	}

	@Test
	public void testIsMultiply() {
		assertIsMultiply("(* 1 2)", true);
		assertIsMultiply("(+ 1 2)", false);
		assertIsMultiply("(- 1 2)", false);
		assertIsMultiply("(if 1 2)", false);
		assertIsMultiply("1", false);
		assertIsMultiply("true", false);
	}

	private void assertIsMultiply(String input, boolean expectedResult) {
		Node n = readNode(input);
		if (n instanceof FunctionNode) {
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isMultiply((FunctionNode) n));
			assertEquals(expectedResult, ArithmeticExpressionSimplifier.isMultiply(((FunctionNode) n).getOperator()));
		} else {
			assertFalse(expectedResult);
		}
	}

	@Test
	public void testIsAddOrSubtract() {
		assertTrue(ArithmeticExpressionSimplifier.isAddOrSubtract(new Add()));
		assertTrue(ArithmeticExpressionSimplifier.isAddOrSubtract(new Subtract()));
		assertFalse(ArithmeticExpressionSimplifier.isAddOrSubtract(new Multiply()));
		assertFalse(ArithmeticExpressionSimplifier.isAddOrSubtract(new If()));
	}

	@Test
	public void testNegate() {
		assertNegate("1", "-1");
		assertNegate("-1", "1");
		assertNegate("(+ v0 v1)", "(- 0 (+ v0 v1))");
	}

	private void assertNegate(String before, String after) {
		Node input = readNode(before);
		Node output = readNode(after);
		assertEquals(output, ArithmeticExpressionSimplifier.negate(input));
	}

	@Test
	public void testSimplify() {
		assertReplace("(* 3 (* v0 v1))", "(* 3 (* v0 v1))");

		assertReplace("(+ 1 1)", "(+ 1 1)");

		assertAdditionSimplification("v0", "(+ 1 v0)", "(+ 1 (* 2 v0))");

		assertAdditionSimplification("v0", "(+ v1 (+ v1 (+ v0 9)))", "(+ v1 (+ v1 (+ (* 2 v0) 9)))");

		assertAdditionSimplification("v1", "(+ v1 (+ v1 (+ v0 9)))", "(+ (* 2 v1) (+ v1 (+ v0 9)))");

		assertAdditionSimplification("v0", "(* 1 v0)", "(* 2 v0)");

		assertReplace("(- 1 1)", "(- 1 1)");

		assertReplace("(+ v0 (- 1 v0))", "(- 1 0)");

		assertReplace("(- v0 (- v1 (- v0 9)))", "(- 0 (- v1 (- (* 2 v0) 9)))");
		assertReplace("(- v0 (- v1 (- v1 (- v0 9))))", "(- 0 (- v1 (- v1 (- 0 9))))");

		assertAdditionSimplification("9", "(+ v0 3)", "(+ v0 12)");

		assertAdditionSimplification("9", "(- v0 3)", "(- v0 -6)");

		assertReplace("(- 4 (- v1 (- v0 9)))", "(- 0 (- v1 (- v0 5)))");
		assertReplace("(- 4 (- v1 (+ v0 9)))", "(- 0 (- v1 (+ v0 13)))");

		assertReplace("(- (+ 4 v0) 3)", "(+ 1 v0)");
		assertReplace("(- (- v0 1) v1)", "(- (- v0 1) v1)");

		assertReplace("(- (- v0 1) (- v0 1))", "(- (- 0 0) (- 1 1))");
		assertReplace("(- (+ v0 1) (+ v0 1))", "(- (+ 0 0) (+ -1 1))");
		assertReplace("(+ (- v0 1) (- v0 1))", "(+ (- 0 0) (- (* 2 v0) 2))");
		assertReplace("(+ (+ v0 1) (+ v0 1))", "(+ (+ 0 0) (+ (* 2 v0) 2))");
		assertReplace("(- (+ v0 1) (- v0 1))", "(- (+ 0 0) (- -1 1))");
	}

	private void assertAdditionSimplification(String firstArg, String secondArg, String expectedOutput) {
		assertReplace("(+ " + firstArg + " " + secondArg + ")", expectedOutput);
	}

	private void assertReplace(String input, String expectedOutput) {
		FunctionNode in = (FunctionNode) readNode(input);
		Arguments args = in.getArguments();
		Node simplifiedVersion = ArithmeticExpressionSimplifier.simplify(in.getOperator(), args.get(0), args.get(1)).orElse(in);
		String writeNode = writeNode(simplifiedVersion);
		assertEquals(expectedOutput, writeNode);
		if (!simplifiedVersion.equals(in)) {
			int[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };
			for (int[] assignedValue : assignedValues) {
				Assignments assignments = Assignments.createAssignments(assignedValue[0], assignedValue[1]);
				if (!in.evaluate(assignments).equals(simplifiedVersion.evaluate(assignments))) {
					throw new RuntimeException(writeNode);
				}
			}
		}
	}
}
