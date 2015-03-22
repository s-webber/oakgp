package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

public class NodeSimplifierTest {
	private final NodeSimplifier nodeSimplifier = new NodeSimplifier();

	@Test
	public void testConstantNode() {
		Node input = createConstant(1);
		Node output = nodeSimplifier.simplify(input);
		assertSame(input, output);
	}

	@Test
	public void testVariableNode() {
		Node input = createVariable(1);
		Node output = nodeSimplifier.simplify(input);
		assertSame(input, output);
	}

	@Test
	public void testFunctionNodeWithVariable() {
		Node input = readNode("(+ 7 v0)");
		Node output = nodeSimplifier.simplify(input);
		assertSame(input, output);
	}

	@Test
	public void testFunctionNodeNoVariables() {
		Node input = readNode("(+ 7 3)");
		Node output = nodeSimplifier.simplify(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals("10", output.toString());
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToConstant() {
		Node input = readNode("(- (+ v6 3) (* 1 (- (+ v6 3) (* v7 (- v5 v5)))))");
		Node output = nodeSimplifier.simplify(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals("0", output.toString());
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction1() {
		Node input = readNode("(+ (- 5 6) (* v0 (- (* 6 7) (+ 2 3))))");
		Node output = nodeSimplifier.simplify(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals("(org.oakgp.operator.Subtract (org.oakgp.operator.Multiply 37 v0) 1)", output.toString());
		assertEquals(73, output.evaluate(Assignments.createAssignments(2)));
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction2() {
		assertCanSimplify("(- 12 v0)", "(+ 9 (+ 3 (- 1 (+ 1 v0))))");
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction3() {
		// (- (+ v0 (+ 8 v1)) v1) = (x + (8 + y)) - y = x+8
		assertCanSimplify("(+ 8 v0)", "(- (+ v0 (+ 8 v1)) v1)");
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction4() {
		// (- (- (+ 10 (* 2 v0)) v1) v1) = ((10 + (2 * v0)) - v1) - v1 = -2y+10+2x
		assertCanSimplify("(+ (* -2 v1) (+ 10 (* 2 v0)))", "(- (- (+ 10 (* 2 v0)) v1) v1)");
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction5() {
		// (+ v0 (- (+ v0 (+ 8 v2)) v1))) = x + ((x + (8 + z)) - y) = 2x+8+z-y
		Node input = readNode("(+ v0 (- (+ v0 (+ 8 v2)) v1))");
		Node output = nodeSimplifier.simplify(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals("(org.oakgp.operator.Subtract (org.oakgp.operator.Add (org.oakgp.operator.Multiply 2 v0) (org.oakgp.operator.Add 8 v2)) v1)",
				output.toString());
	}

	@Test
	public void testDeeplyNestedTreeSimplifedToFunction6() {
		// TODO (+ 2 (+ (* 2 v0) (+ 8 v1))) = 2 + ((2 * x) + (8 + y)) = 10+2x+y
		assertCanSimplify("(+ 2 (+ (* 2 v0) (+ 8 v1)))", "(+ 2 (+ (* 2 v0) (+ 8 v1)))");
	}

	@Test
	public void testVeryDeeplyNestedTreeSimplifedByOperator1() {
		// TODO keep trying to improve until get to 4y-3x
		// note: (2+(2x+8)) = 10+2x
		Node input = readNode("(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v1)) v1))) v1) v1))) 10))");
		Node output = nodeSimplifier.simplify(input);
		Assignments assignments = Assignments.createAssignments(7, 12);
		assertEquals(input.evaluate(assignments), output.evaluate(assignments));
		assertEquals("(- v1 (- (- v0 (- v1 (+ (* -2 v1) (+ 10 (* 2 v0))))) 10))", new NodeWriter().writeNode(output));
	}

	@Test
	public void testVeryDeeplyNestedTreeSimplifedByOperator2() {
		// TODO keep trying to improve until get to 5y-3x-z
		Node input = readNode("(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v2)) v1))) v1) v1))) 10))");
		Node output = nodeSimplifier.simplify(input);
		Assignments assignments = Assignments.createAssignments(7, 12, 8);
		assertEquals(input.evaluate(assignments), output.evaluate(assignments));
		assertEquals("(- v1 (- (- v0 (- v1 (- (+ (+ 2 (+ (* 2 v0) (+ 8 v2))) (* -2 v1)) v1))) 10))", new NodeWriter().writeNode(output));
	}

	@Test
	public void testVeryDeeplyNestedTreeSimplifedByOperator3() {
		// TODO keep trying to improve
		Node input = readNode("(* 3 (* (* 3 (- 1 (- (- 2 (- 1 (- 1 (- 1 (+ 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (+ 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (- 0 (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) v2)))))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 1 (- 1 (+ 1 v2))))))))	(* 3 (- 0 (+ v2 1)))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))))) (- (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 3 (- 1 (+ 1 v2)))))))) -3))");
		Node output = nodeSimplifier.simplify(input);
		Assignments assignments = Assignments.createAssignments(0, 1, 2, 3, 1);
		assertEquals(input.evaluate(assignments), output.evaluate(assignments));
		assertEquals(
				"(* 3 (* -3 (* 3 (- 1 (- (- 2 (- (+ (* 3 (* (* 3 (- -1 v2)) (* 3 (- 1 (- (- 3 (- (* 3 (* -3 (* 3 (- 1 (- (- 3 (- (- (- 5 (- (- (+ (* 2 v3) (* 2 v2)) 1) (- v2 2))) (+ 1 (* 2 v2))) (- 7 (* 2 v2)))) 1))))) (- (- (- 7 (- (+ v2 (+ (* 3 (* -3 (* 3 (- 1 (- (- 3 (- (- (- 5 (- (- (+ (* 2 v3) (* 2 v2)) 1) (- v2 2))) (+ 1 (* 2 v2))) (- 7 (* 2 v2)))) 1))))) (* 2 v2))) 2)) (+ 3 (* 2 v2))) v2))) (+ (- 10 v2) (+ 1 v2))))))) (* 2 v2)) 4)) (- (* 2 v2) 11))))))",
				new NodeWriter().writeNode(output));
	}

	@Test
	public void testVeryDeeplyNestedTreeSimplifedByOperator4() {
		// TODO keep trying to improve
		Node input = readNode("(+ (- v4 (- 3 v4)) (if (- 4 (+ v4 v2)) (+ (+ 1 (+ (- v1 (- v1 (- v1 (* v3 (* v0 v2))))) (* v4 (+ v3 v1)))) (if (- v4 (* 3 (- (if (- v3 (+ v3 (- v2 (* 3 v3)))) (+ (* (- (+ v3 (* v1 v2)) (if (- v3 (if (- (- v1 (- (- v1 4) (if (- 3 v4) v4 0))) (- (- v1 (- (- v1 1) v4)) (- v3 3))) (- v1 v4) v1)) (- (- v2 (- v3 (* v1 v2))) v4) v1)) (if (- v3 (+ 1 (+ v3 (* (+ v1 1) v2)))) 3 v1)) (if v1 v2 v1)) v4) (* 3 v2)))) 3 (+ (- 0 (- (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2))))) (* v4 (- v3 v1)))) v1))) (+ (- v1 (- (- v1 v2) (if v4 v4 v1))) (if (* (- v3 (if (- v3 (- (if (* v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (+ v4 (* (+ v1 (- v1 v4)) (if (- (* v4 (- v3 v1)) (- (- v3 (- v3 (if (- (* v1 (if (- v3 (+ v3 (- (if (* (- v3 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2)))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (- (* v1 (+ v3 (+ v1 1))) (* (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 1) (if (- (if (- v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4)))) (- (+ v1 (- (- v1 v4) (- v3 (* v0 v2)))) (if (- (* v4 (- v3 v1)) (* (- v3 (- (+ (- v1 (- (- v1 v2) (if v4 v4 v1))) (if (* (- v3 (if (- v3 (- (if (* v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (* v4 (* (+ v1 (- v1 v4)) (if (- (* v4 (- v3 v1)) (* (- v3 (- v3 (if (- (* v1 (if (- v3 (+ v3 (- (if (* (- v2 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2)))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (- (* 3 (+ v3 v1)) (* (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 1) (if (- (if (- v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4)))) (- (+ v1 (- 0 (- v3 (* v0 v2)))) (if (- (* 2 (- v3 v1)) (* (- v3 (- v3 (if (+ (* v1 (- (- v1 1) v3)) (- v3 (* 3 (* v1 v2)))) (* v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ v3 3))) v3))) (* v2 (if v1 v2 v1)) v4) v4) v4 v1))) (- v3 3))) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (* v1 v4) v1))) v2)) v2 v4))))) (* (+ v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- 3 (* (- v3 (- v3 (if (- (+ v1 (- (- v1 1) (if (- 1 v4) 1 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3))) (* 3 v3)))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4) (+ v3 (* (- v1 1) 3)))) 3 v1)) (if v1 v2 v1)) (- v1 (* v1 v2)) v1)) (if (+ (* v1 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ v3 3))) v3))) (* v2 (if v1 v4 v1)) v4) v4) v4 v1))) (- v3 3))) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (+ v1 v4) v1))) v2)) v2 v4))))) (* (* v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- v3 (* (- v3 (- v3 (if (- (+ v1 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3))) (* 3 v3)))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4) (+ v3 (* (- v1 1) 3)))) 3 v1)) (if v1 v2 v1)) (- v1 (* v1 v2)) v1))))");
		Node output = nodeSimplifier.simplify(input);
		assertEquals(
				"(+ (- v4 (- 3 v4)) (if (- 4 (+ v2 v4)) (+ (+ 1 (+ (- v1 (- v1 (- v1 (* v3 (* v0 v2))))) (* v4 (+ v1 v3)))) (if (- v4 (* 3 (- (if (- v3 (- (+ v2 v3) (* 3 v3))) (+ (if v1 v2 v1) (* (if (- v3 (+ 1 (+ v3 (* v2 (+ 1 v1))))) 3 v1) (- (+ v3 (* v1 v2)) (if (- v3 (if (- (- v1 (- (- v1 4) (if (- 3 v4) v4 0))) (- (- v1 (- (- v1 1) v4)) (- v3 3))) (- v1 v4) v1)) (- (- v2 (- v3 (* v1 v2))) v4) v1)))) v4) (* 3 v2)))) 3 (- v1 (- (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2))))) (* v4 (- v3 v1)))))) (- (+ v1 (if (* (if v1 v2 v1) (- v3 (if (- v3 (- (if (* v3 (- (+ v3 (if (- (- (+ v3 (* v1 v4)) (+ v4 (* (if (- (* v4 (- v3 v1)) (- (- v3 (- v3 (if (- (* v1 (if (- v3 (if (* (- (+ v1 v3) (* 4 (* 3 v2))) (- v3 (+ v1 v2))) (* (- (- (+ v0 v1) 3) (* v2 v3)) (if (- (* v1 (+ v3 (+ 1 v1))) (* v2 (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))))) v2 v1)) (- (- v1 (- (- v1 1) (if (- (if (- v3 (if (- (- (+ v3 (+ v1 v4)) (* 4 (* v1 v4))) (+ v1 v2)) (- (- (- (* 2 v1) v4) (- v3 (* v0 v2))) (if (- (* v4 (- v3 v1)) (* v2 (- v3 (- (- (+ v1 (if (* (if v1 v2 v1) (- v3 (if (- v3 (- (if (* v3 (- (+ v3 (if (- (- (+ v3 (* v1 v4)) (* v4 (* (if (- (* v4 (- v3 v1)) (* v2 (- v3 (- v3 (if (- (* v1 (if (- v3 (if (* (- (+ v1 v3) (* 4 (* 3 v2))) (- v2 (+ v1 v2))) (* (if (- (* 3 (+ v1 v3)) (* v2 (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))))) v2 v1) (- (- (+ v0 v1) 3) (* v2 v3))) (- (- v1 (- (- v1 1) (if (- (if (- v3 (if (- (- (+ v3 (+ v1 v4)) (* 4 (* v1 v4))) (+ v1 v2)) (- (- v1 (- v3 (* v0 v2))) (if (- (* 2 (- v3 v1)) (* v2 (- v3 (- v3 (if (- (+ v3 (* v1 (- (- v1 1) v3))) (* 3 (* v1 v2))) (* v1 v4) v1))))) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ 3 v3)))) (* v2 (if v1 v2 v1)) v4) v4) v4 v1))) (- v3 3)))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (* v1 v4) v1))))) v2 v4) (- (* 2 v1) v4)))) (+ v1 v2)) (* (- (- (* 2 v1) v2) (* v3 (* v0 v2))) (if (- 3 (* v2 (- v3 (- v3 (if (- (- (- (* 2 v1) 1) (if (- 1 v4) 1 v1)) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))))) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3)))) (* 3 v3))) (* (if v4 v2 v1) (if (- 1 v4) v4 0)) v4) (+ v3 (* 3 (- v1 1))))) 3 v1))) (- v1 (* v1 v2)) v1)) (- (- v1 v2) (if v4 v4 v1))) (if (+ (- v3 (* 3 (* v1 v2))) (* v1 (- (- v1 1) (if (- 1 v4) v4 v1)))) (- v1 v4) v1))))) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ 3 v3)))) (* v2 (if v1 v4 v1)) v4) v4) v4 v1))) (- v3 3)))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (+ v1 v4) v1))) v2)) v2 v4) (- (* 2 v1) v4)))) (+ v1 v2)) (* (* v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- v3 (* v2 (- v3 (- v3 (if (- (- (- (* 2 v1) 1) (if (- 1 v4) v4 v1)) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))))) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3)))) (* 3 v3))) (* (if v4 v2 v1) (if (- 1 v4) v4 0)) v4) (+ v3 (* 3 (- v1 1))))) 3 v1))) (- v1 (* v1 v2)) v1)) (- (- v1 v2) (if v4 v4 v1)))))",
				new NodeWriter().writeNode(output));
	}

	private static void assertCanSimplify(String expected, String input) {
		Node inputNode = readNode(input);
		Node expectedNode = readNode(expected);
		Node simpliedVersion = new NodeSimplifier().simplify(inputNode);
		assertEquals(expectedNode.toString(), simpliedVersion.toString());
		assertEquals(expectedNode, simpliedVersion);
		int[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };
		for (int[] assignedValue : assignedValues) {
			Assignments assignments = Assignments.createAssignments(assignedValue[0], assignedValue[1]);
			assertEquals(inputNode.evaluate(assignments), simpliedVersion.evaluate(assignments));
		}
	}
}
