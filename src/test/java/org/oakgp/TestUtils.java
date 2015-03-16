package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Operator;
import org.oakgp.serialize.NodeReader;

public class TestUtils {
	public static void assertVariable(int expectedId, Node node) {
		assertTrue(node instanceof VariableNode);
		assertEquals(expectedId, ((VariableNode) node).getId());
	}

	public static void assertConstant(Object expectedValue, Node node) {
		assertTrue(node instanceof ConstantNode);
		assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
	}

	public static Node readNode(String input) {
		List<Node> outputs = readNodes(input);
		assertEquals(1, outputs.size());
		return outputs.get(0);
	}

	public static List<Node> readNodes(String input) {
		List<Node> outputs = new ArrayList<>();
		try (NodeReader nr = new NodeReader(input)) {
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

	public static ConstantNode createConstant(Object value) {
		return new ConstantNode(value);
	}

	public static VariableNode createVariable(int id) {
		return new VariableNode(id);
	}

	public static void assertCanSimplify(Operator operator, Node expected, Arguments arguments) {
		Optional<Node> o = operator.simplify(arguments);
		assertTrue(o.isPresent());
		assertEquals(expected, o.get());
	}

	public static void assertCanSimplify(String expected, String input) {
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

	public static void assertCannotSimplify(Operator operator, Arguments arguments) {
		Optional<Node> o = operator.simplify(arguments);
		assertFalse(o.isPresent());
	}

	public static void assertRankedCandidate(RankedCandidate actual, Node expectedNode, double expectedFitness) {
		assertSame(expectedNode, actual.getNode());
		assertEquals(expectedFitness, actual.getFitness(), 0);
	}
}
