package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;

import java.util.function.Function;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;

public class FunctionNodeTest {
	@Test
	public void testEvaluate() {
		Operator operator = (arguments1, assignments) -> arguments1.get(0).evaluate(assignments) * arguments1.get(1).evaluate(assignments);
		Arguments arguments = createArguments(new ConstantNode(42), new VariableNode(0));
		FunctionNode functionNode = new FunctionNode(operator, arguments);

		assertSame(operator, functionNode.getOperator());
		assertSame(arguments, functionNode.getArguments());

		Assignments assignments = createAssignments(3);
		assertEquals(126, functionNode.evaluate(assignments));
	}

	@Test
	public void testReplaceAt() {
		FunctionNode n = createFunctionNode();
		Function<Node, Node> replacement = t -> new ConstantNode(9);

		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply 9 p0) (org.oakgp.operator.Add p0 1))", n.replaceAt(0, replacement).toString());
		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply p0 9) (org.oakgp.operator.Add p0 1))", n.replaceAt(1, replacement).toString());
		assertEquals("(org.oakgp.operator.Add 9 (org.oakgp.operator.Add p0 1))", n.replaceAt(2, replacement).toString());
		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply p0 p0) (org.oakgp.operator.Add 9 1))", n.replaceAt(3, replacement).toString());
		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply p0 p0) (org.oakgp.operator.Add p0 9))", n.replaceAt(4, replacement).toString());
		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply p0 p0) 9)", n.replaceAt(5, replacement).toString());
		assertEquals("9", n.replaceAt(6, replacement).toString());
	}

	@Test
	public void testGetAt() {
		FunctionNode n = createFunctionNode();

		assertEquals("p0", n.getAt(0).toString());
		assertEquals("p0", n.getAt(1).toString());
		assertEquals("(org.oakgp.operator.Multiply p0 p0)", n.getAt(2).toString());
		assertEquals("p0", n.getAt(3).toString());
		assertEquals("1", n.getAt(4).toString());
		assertEquals("(org.oakgp.operator.Add p0 1)", n.getAt(5).toString());
		assertEquals("(org.oakgp.operator.Add (org.oakgp.operator.Multiply p0 p0) (org.oakgp.operator.Add p0 1))", n.getAt(6).toString());
	}

	/** Returns representation of: {@code (x*x)+x+1} */
	private FunctionNode createFunctionNode() {
		return new FunctionNode(new Add(), createArguments(new FunctionNode(new Multiply(), createArguments(new VariableNode(0), new VariableNode(0))),
				new FunctionNode(new Add(), createArguments(new VariableNode(0), new ConstantNode(1)))));
	}
}
