package org.oakgp.serialize;

import static org.oakgp.Arguments.createArguments;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;

public class NodeWriter {
	public String writeNode(Node node) {
		StringBuilder sb = new StringBuilder();
		writeNode(node, sb);
		return sb.toString();
	}

	private void writeNode(Node node, StringBuilder sb) {
		if (node instanceof FunctionNode) {
			FunctionNode functionNode = (FunctionNode) node;
			Operator operator = functionNode.getOperator();
			Arguments arguments = functionNode.getArguments();
			sb.append('(');
			sb.append(operator.getClass().getName());
			for (int i = 0; i < arguments.length(); i++) {
				sb.append(' ');
				sb.append(writeNode(arguments.get(i)));
			}
			sb.append(')');
		} else {
			sb.append(node);
		}
	}

	public static void main(String[] args) {
		NodeWriter writer = new NodeWriter();
		Node node = new FunctionNode(new Add(), createArguments(new FunctionNode(new Multiply(), createArguments(new VariableNode(0), new VariableNode(0))),
				new FunctionNode(new Add(), createArguments(new VariableNode(0), new ConstantNode(1)))));
		System.out.println(writer.writeNode(node));
	}
}
