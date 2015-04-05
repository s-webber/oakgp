package org.oakgp.mutate;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.TestUtils.assertConstant;
import static org.oakgp.TestUtils.assertVariable;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.ConstantSet;
import org.oakgp.FunctionSet;
import org.oakgp.PrimitiveSet;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.function.Function;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.Random;

public class PointMutationTest {
   // TODO test mutating child nodes, not just the root node (which is all the tests currently do)

   private static final double VARIABLE_RATIO = .6;
   private static final Type[] VARIABLE_TYPES = createTypeArray(9);
   private static final ConstantNode[] CONSTANTS = { integerConstant(7), integerConstant(8), integerConstant(9) };
   private static final Function[] FUNCTIONS = { new Add(), new Subtract(), new Multiply() };

   @Test
   public void testTerminalMutation() {
      int expectedVariableId = 1;
      int expectedConstantIndex = 2;
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO - .1, VARIABLE_RATIO + .1);
      given(mockRandom.nextInt(VARIABLE_TYPES.length)).willReturn(expectedVariableId);
      given(mockRandom.nextInt(CONSTANTS.length)).willReturn(expectedConstantIndex);

      DummyNodeSelector dummySelector = new DummyNodeSelector(integerConstant(9), integerConstant(2));

      PointMutation pointMutation = createPointMutation(mockRandom);

      Node offspring1 = pointMutation.evolve(dummySelector);
      assertVariable(expectedVariableId, offspring1);

      Node offspring2 = pointMutation.evolve(dummySelector);
      assertConstant(CONSTANTS[expectedConstantIndex].evaluate(null), offspring2);
      assertTrue(dummySelector.isEmpty());
   }

   @Test
   public void testFunctionMutation() {
      int expectedFunctionIndex = 2;
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(FUNCTIONS.length)).willReturn(expectedFunctionIndex);

      Arguments arguments = createArguments(integerConstant(3), integerConstant(7));
      FunctionNode originalFunctionNode = new FunctionNode(FUNCTIONS[0], arguments);
      DummyNodeSelector dummySelector = new DummyNodeSelector(originalFunctionNode);

      PointMutation pointMutation = createPointMutation(mockRandom);

      FunctionNode offspring = (FunctionNode) pointMutation.evolve(dummySelector);
      assertSame(FUNCTIONS[expectedFunctionIndex], offspring.getFunction());
      assertSame(arguments, offspring.getArguments());
      assertTrue(dummySelector.isEmpty());
   }

   private PointMutation createPointMutation(Random mockRandom) {
      FunctionSet.Builder b = new FunctionSet.Builder();
      b.put("+", FUNCTIONS[0]);
      b.put("-", FUNCTIONS[1]);
      b.put("*", FUNCTIONS[2]);
      FunctionSet functions = b.build();
      ConstantSet constants = new ConstantSet(CONSTANTS);
      VariableSet variables = VariableSet.createVariableSet(VARIABLE_TYPES);
      PrimitiveSet primitiveSet = new PrimitiveSet(functions, constants, variables, mockRandom, VARIABLE_RATIO);
      return new PointMutation(mockRandom, primitiveSet);
   }
}
