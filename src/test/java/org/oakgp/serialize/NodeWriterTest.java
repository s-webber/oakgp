package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.TestUtils.createConstant;

import org.junit.Test;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Subtract;

public class NodeWriterTest {
	// TODO test writeNode when no symbol exists in SymbolMap for the specified Operator

	@Test
	public void testConstantNode() {
		NodeWriter writer = new NodeWriter();
		String output = writer.writeNode(createConstant(768));
		assertEquals("768", output);
	}

	@Test
	public void testVariableNode() {
		NodeWriter writer = new NodeWriter();
		String output = writer.writeNode(new VariableNode(2));
		assertEquals("v2", output);
	}

	@Test
	public void testFunctionNode() {
		NodeWriter writer = new NodeWriter();
		String output = writer.writeNode(new FunctionNode(new Add(), createArguments(createConstant(5), new VariableNode(0))));
		assertEquals("(+ 5 v0)", output);
	}

	@Test
	public void testFunctionNodeWithFunctionNodeArguments() {
		NodeWriter writer = new NodeWriter();
		FunctionNode arg1 = new FunctionNode(new Subtract(), createArguments(createConstant(5), new VariableNode(0)));
		FunctionNode arg2 = new FunctionNode(new Multiply(), createArguments(new VariableNode(1), createConstant(-6876)));
		String output = writer.writeNode(new FunctionNode(new Add(), createArguments(arg1, arg2)));
		assertEquals("(+ (- 5 v0) (* v1 -6876))", output);
	}
}
