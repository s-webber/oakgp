package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;

public class TestUtils {
	public static void assertVariable(int expectedId, Node node) {
		assertTrue(node instanceof VariableNode);
		assertEquals(expectedId, ((VariableNode) node).getId());
	}

	public static void assertConstant(Object expectedValue, Node node) {
		assertTrue(node instanceof ConstantNode);
		assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
	}

	public static String writeNode(Node input) {
		return new NodeWriter().writeNode(input);
	}

	public static Node readNode(String input) {
		List<Node> outputs = readNodes(input);
		assertEquals(1, outputs.size());
		return outputs.get(0);
	}

	public static List<Node> readNodes(String input) {
		List<Node> outputs = new ArrayList<>();
		try (NodeReader nr = new NodeReader(input, createTypeArray(100))) {
			while (!nr.isEndOfStream()) {
				outputs.add(nr.readNode());
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException caught reading: " + input, e);
		}
		return outputs;
	}

	public static Arguments createArguments(String... expressions) {
		Node[] args = new Node[expressions.length];
		for (int i = 0; i < expressions.length; i++) {
			args[i] = readNode(expressions[i]);
		}
		return Arguments.createArguments(args);
	}

	public static ConstantNode createConstant(int value) {
		return new ConstantNode(value, Type.INTEGER);
	}

	public static VariableNode createVariable(int id) {
		return new VariableNode(id, Type.INTEGER);
	}

	public static Type[] createTypeArray(int size) {
		Type[] a = new Type[size];
		for (int i = 0; i < size; i++) {
			a[i] = Type.INTEGER;
		}
		return a;
	}

	public static void assertRankedCandidate(RankedCandidate actual, Node expectedNode, double expectedFitness) {
		assertSame(expectedNode, actual.getNode());
		assertEquals(expectedFitness, actual.getFitness(), 0);
	}
}
