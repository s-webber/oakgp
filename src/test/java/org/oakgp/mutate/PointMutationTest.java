package org.oakgp.mutate;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.TestUtils.assertConstant;
import static org.oakgp.TestUtils.assertVariable;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.FunctionSet;
import org.oakgp.TerminalSet;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;
import org.oakgp.operator.Subtract;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.Random;

public class PointMutationTest {
	// TODO test mutating child nodes, not just the root node (which is all the tests currently do)

	private static final double VARIABLE_RATIO = .6;
	private static final int NUMBER_OF_VARIABLES = 9;
	private static final ConstantNode[] CONSTANTS = new ConstantNode[] { new ConstantNode(7), new ConstantNode(8), new ConstantNode(9) };
	private static final Operator[] OPERATORS = new Operator[] { new Add(), new Subtract(), new Multiply() };

	@Test
	public void testTerminalMutation() {
		int expectedVariableId = 1;
		int expectedConstantIndex = 2;
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO - .1, VARIABLE_RATIO + .1);
		given(mockRandom.nextInt(NUMBER_OF_VARIABLES)).willReturn(expectedVariableId);
		given(mockRandom.nextInt(CONSTANTS.length)).willReturn(expectedConstantIndex);

		DummyNodeSelector dummySelector = new DummyNodeSelector(new ConstantNode(9), new VariableNode(2));

		PointMutation pointMutation = createPointMutation(mockRandom);

		Node offspring1 = pointMutation.evolve(dummySelector);
		assertVariable(expectedVariableId, offspring1);

		Node offspring2 = pointMutation.evolve(dummySelector);
		assertConstant(CONSTANTS[expectedConstantIndex].evaluate(null), offspring2);
		assertTrue(dummySelector.isEmpty());
	}

	@Test
	public void testFunctionMutation() {
		int expectedOperatorIndex = 2;
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(OPERATORS.length)).willReturn(expectedOperatorIndex);

		Arguments arguments = createArguments(new ConstantNode(3), new ConstantNode(7));
		FunctionNode originalFunctionNode = new FunctionNode(OPERATORS[0], arguments);
		DummyNodeSelector dummySelector = new DummyNodeSelector(originalFunctionNode);

		PointMutation pointMutation = createPointMutation(mockRandom);

		FunctionNode offspring = (FunctionNode) pointMutation.evolve(dummySelector);
		assertSame(OPERATORS[expectedOperatorIndex], offspring.getOperator());
		assertSame(arguments, offspring.getArguments());
		assertTrue(dummySelector.isEmpty());
	}

	private PointMutation createPointMutation(Random mockRandom) {
		FunctionSet functionSet = new FunctionSet(mockRandom, OPERATORS);
		TerminalSet terminalSet = new TerminalSet(mockRandom, VARIABLE_RATIO, NUMBER_OF_VARIABLES, CONSTANTS);
		return new PointMutation(mockRandom, functionSet, terminalSet);
	}
}
