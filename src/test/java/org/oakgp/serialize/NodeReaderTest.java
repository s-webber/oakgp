package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.readNodes;

import java.util.List;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class NodeReaderTest {
	// TODO test error conditions

	@Test
	public void testZero() {
		String input = "0";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testNegativeConstantNode() {
		String input = "-9";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testSingleCharacterConstantNode() {
		String input = "4";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testMulipleCharacterConstantNode() {
		String input = "147";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testSingleDigitIdVariableNode() {
		String input = "v1";
		Node output = readNode(input);
		assertSame(VariableNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testMultipleDigitIdVariableNode() {
		String input = "v78";
		Node output = readNode(input);
		assertSame(VariableNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testFunctionNodeSpecifiedByClassName() {
		String input = "(org.oakgp.operator.Add 7 21)";
		Node output = readNode(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testFunctionNodeSpecifiedBySymbol() {
		String expected = "(org.oakgp.operator.Add 7 21)";
		Node output = readNode("(+ 7 21)");
		assertSame(FunctionNode.class, output.getClass());
		assertEquals(expected, output.toString());
	}

	@Test
	public void testFunctionNodeWithFunctionNodeArguments() {
		String input = "(org.oakgp.operator.Add (org.oakgp.operator.Subtract v0 587) (org.oakgp.operator.Multiply 43 v1))";
		Node output = readNode(input);
		assertSame(FunctionNode.class, output.getClass());
		assertEquals(input, output.toString());
	}

	@Test
	public void testEmptyString() {
		String input = "";
		List<Node> outputs = readNodes(input);
		assertTrue(outputs.isEmpty());
	}

	@Test
	public void testWhitespace() {
		String input = " \r\n\t\t  ";
		List<Node> outputs = readNodes(input);
		assertTrue(outputs.isEmpty());
	}

	@Test
	public void testPadded() {
		String input = " \r\n42\t\t  ";
		Node output = readNode(input);
		assertSame(ConstantNode.class, output.getClass());
		assertNotEquals(input, output.toString());
		assertEquals(input.trim(), output.toString());
	}

	@Test
	public void testMulipleNodes() {
		String[] inputs = { "6", "(org.oakgp.operator.Add v0 v1)", "42", "v0", "(org.oakgp.operator.Add 1 2)", "v98" };
		String combinedInput = " " + inputs[0] + inputs[1] + inputs[2] + " " + inputs[3] + "\n\r\t\t\t" + inputs[4] + "       \n   " + inputs[5] + "\r\n";
		List<Node> outputs = readNodes(combinedInput);
		assertEquals(inputs.length, outputs.size());
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i].toString(), outputs.get(i).toString());
		}
	}
}
