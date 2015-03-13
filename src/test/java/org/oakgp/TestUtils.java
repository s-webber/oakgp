package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.serialize.NodeReader;

public class TestUtils {
	public static void assertVariable(int expectedId, Node node) {
		assertTrue(node instanceof VariableNode);
		assertEquals(expectedId, ((VariableNode) node).getId());
	}

	public static void assertConstant(int expectedValue, Node node) {
		assertTrue(node instanceof ConstantNode);
		assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
	}

	public static Node readNode(String input) throws IOException {
		List<Node> outputs = readNodes(input);
		assertEquals(1, outputs.size());
		return outputs.get(0);
	}

	public static List<Node> readNodes(String input) throws IOException {
		List<Node> outputs = new ArrayList<>();
		try (NodeReader nr = new NodeReader(input)) {
			while (!nr.isEndOfStream()) {
				outputs.add(nr.readNode());
			}
		}
		return outputs;
	}
}
