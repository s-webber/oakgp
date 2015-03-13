package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class NodeReaderTest {
	// TODO test error conditions

	@Test
	public void testZero() throws IOException {
		String input = "0";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testNegativeConstantNode() throws IOException {
		String input = "-9";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testSingleCharacterConstantNode() throws IOException {
		String input = "4";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testMulipleCharacterConstantNode() throws IOException {
		String input = "147";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testSingleDigitIdVariableNode() throws IOException {
		String input = "p1";
		Node output = readNode(input);
		assertSame(VariableNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testMultipleDigitIdVariableNode() throws IOException {
		String input = "p78";
		Node output = readNode(input);
		assertSame(VariableNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testFunctionNode() throws IOException {
		String input = "(org.oakgp.operator.Add 7 21)";
		Node output = readNode(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testFunctionNodeWithFunctionNodeArguments() throws IOException {
		String input = "(org.oakgp.operator.Add (org.oakgp.operator.Subtract p0 587) (org.oakgp.operator.Multiply 43 p1))";
		Node output = readNode(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testEmptyString() throws IOException {
		String input = "";
		List<Node> outputs = readNodes(input);
		assertTrue(outputs.isEmpty());
	}

	@Test
	public void testWhitespace() throws IOException {
		String input = " \r\n\t\t  ";
		List<Node> outputs = readNodes(input);
		assertTrue(outputs.isEmpty());
	}

	@Test
	public void testPadded() throws IOException {
		String input = " \r\n42\t\t  ";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertNotEquals(input, output.toString());
		assertEquals(input.trim(), output.toString());
	}

	@Test
	public void testMulipleNodes() throws IOException {
		String[] inputs = { "6", "(org.oakgp.operator.Add p0 p1)", "42", "p0", "(org.oakgp.operator.Add 1 2)", "p98" };
		String combinedInput = " " + inputs[0] + inputs[1] + inputs[2] + " " + inputs[3] + "\n\r\t\t\t" + inputs[4] + "       \n   " + inputs[5] + "\r\n";
		List<Node> outputs = readNodes(combinedInput);
		assertEquals(inputs.length, outputs.size());
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i].toString(), outputs.get(i).toString());
		}
	}

	private Node readNode(String input) throws IOException {
		List<Node> outputs = readNodes(input);
		assertEquals(1, outputs.size());
		return outputs.get(0);
	}

	private List<Node> readNodes(String input) throws IOException {
		List<Node> outputs = new ArrayList<>();
		try (NodeReader nr = new NodeReader(input)) {
			while (!nr.isEndOfStream()) {
				outputs.add(nr.readNode());
			}
		}
		return outputs;
	}
}
