package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;

public class NodeComparatorTest {
	@Test
	public void test() {
		NodeComparator comparator = new NodeComparator();
		List<Node> nodes = new ArrayList<>();
		nodes.add(readNode("(+ 1 1)"));
		nodes.add(readNode("(- 1 1)"));
		nodes.add(readNode("-1"));
		nodes.add(readNode("v1"));
		nodes.add(readNode("3"));
		nodes.add(readNode("(* 1 1)"));
		nodes.add(readNode("v0"));
		Collections.sort(nodes, comparator);

		assertEquals("-1", nodes.get(0).toString());
		assertEquals("3", nodes.get(1).toString());
		assertEquals("v0", nodes.get(2).toString());
		assertEquals("v1", nodes.get(3).toString());
		// TODO check order of function nodes
	}
}
