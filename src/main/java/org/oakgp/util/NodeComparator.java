package org.oakgp.util;

import java.util.Comparator;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class NodeComparator implements Comparator<Node> {
	@Override
	public int compare(Node o1, Node o2) {
		if (o1.getClass() == o2.getClass()) {
			int i = o1.getType().compareTo(o2.getType());
			if (i == 0) {
				int i1 = o1.hashCode();
				int i2 = o2.hashCode();
				if (i1 == i2) {
					return 0;
				} else if (i1 < i2) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return i;
			}
		}
		if (o1 instanceof FunctionNode) {
			return 1;
		}
		if (o2 instanceof FunctionNode) {
			return -1;
		}
		if (o1 instanceof ConstantNode) {
			return -1;
		}
		if (o2 instanceof ConstantNode) {
			return 1;
		}
		throw new IllegalStateException();
	}
}
