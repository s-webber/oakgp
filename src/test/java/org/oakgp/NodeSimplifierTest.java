package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class NodeSimplifierTest {
	private final NodeSimplifier nodeSimplifier = new NodeSimplifier();

	@Test
	public void testConstantNode() {
		Node input = new ConstantNode(1);
		Node output = nodeSimplifier.simplify(input);
		assertSame(input, output);
	}

	@Test
	public void testVariableNode() {
		Node input = new VariableNode(1);
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
	public void testDeeplyNestedTree() {
		Node input = readNode("(+ (- 5 6) (* v0 (- (* 6 7) (+ 2 3))))");
		Node output = nodeSimplifier.simplify(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals("(org.oakgp.operator.Add -1 (org.oakgp.operator.Multiply v0 37))", output.toString());
	}

	@Test
	public void testDeeplyNestedTreeSimplifedByOperator() {
		Node input = readNode("(- (+ v6 3) (* 1 (- (+ v6 3) (* v7 (- v5 v5)))))");
		Node output = nodeSimplifier.simplify(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals("0", output.toString());
	}
}
