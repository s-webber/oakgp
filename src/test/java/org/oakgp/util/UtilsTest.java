package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class UtilsTest {
	@Test
	public void testEvaluateToSameResultSuccess() {
		Node a = readNode("(* 7 (+ 1 2))");
		Node b = readNode("(+ 9 12)");
		Utils.assertEvaluateToSameResult(a, b);
	}

	@Test
	public void testEvaluateToSameResultFailure() {
		Node a = readNode("(* 7 (- 1 2))");
		Node b = readNode("(+ 9 12)");
		try {
			Utils.assertEvaluateToSameResult(a, b);
		} catch (IllegalArgumentException e) {
			assertEquals("(* 7 (- 1 2)) = -7 (+ 9 12) = 21", e.getMessage());
		}
	}
}
