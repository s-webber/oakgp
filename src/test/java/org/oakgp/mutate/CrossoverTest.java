package org.oakgp.mutate;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Arguments.createArguments;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Subtract;
import org.oakgp.selector.NodeSelector;
import org.oakgp.util.Random;

public class CrossoverTest {
	@Test
	public void test() {
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(3)).willReturn(1);
		given(mockRandom.nextInt(5)).willReturn(3);

		FunctionNode parent1 = new FunctionNode(new Add(), createArguments(new ConstantNode(9), new ConstantNode(5)));
		FunctionNode parent2 = new FunctionNode(new Multiply(), createArguments(new ConstantNode(7),
				new FunctionNode(new Subtract(), createArguments(new ConstantNode(8), new VariableNode(5)))));

		NodeSelector mockSelector = mock(NodeSelector.class);
		given(mockSelector.next()).willReturn(parent1, parent2);

		SubtreeCrossover c = new SubtreeCrossover(mockRandom);

		Node result = c.evolve(mockSelector);
		assertEquals("(org.oakgp.operator.Add 9 (org.oakgp.operator.Subtract 8 p5))", result.toString());
	}
}