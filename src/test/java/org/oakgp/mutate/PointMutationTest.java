package org.oakgp.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.PrimitiveSet;
import org.oakgp.function.Function;
import org.oakgp.function.math.Add;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.DummyPrimitiveSet;
import org.oakgp.util.DummyValuesMap;
import org.oakgp.util.Random;

public class PointMutationTest {
   // TODO test mutating child nodes, not just the root node (which is all the tests currently do)

   @Test
   public void testTerminalMutation() {
      Random mockRandom = mock(Random.class);
      Node input = integerConstant(9);
      Node output = integerConstant(7);
      DummyNodeSelector dummySelector = new DummyNodeSelector(input);
      DummyValuesMap<Node, Node> alternativeTerminals = new DummyValuesMap.Builder<Node, Node>().put(input, output).build();
      PrimitiveSet primitiveSet = new DummyPrimitiveSet() {
         @Override
         public Node nextAlternativeTerminal(Node current) {
            return alternativeTerminals.next(current);
         }
      };
      PointMutation pointMutation = new PointMutation(mockRandom, primitiveSet);

      Node offspring = pointMutation.evolve(dummySelector);

      assertSame(output, offspring);
      assertTrue(dummySelector.isEmpty());
   }

   @Test
   public void testFunctionMutation() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      Function function = new Add();
      Node inputArg1 = integerConstant(3);
      Node inputArg2 = integerConstant(7);
      Node input = new FunctionNode(function, inputArg1, inputArg2);
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, input);
      Node outputArg1 = integerConstant(9);
      Node outputArg2 = integerConstant(2);
      DummyValuesMap<Node, Node> alternativeTerminals = new DummyValuesMap.Builder<Node, Node>().put(inputArg1, outputArg1).put(inputArg2, outputArg2).build();
      PrimitiveSet primitiveSet = new DummyPrimitiveSet() {
         @Override
         public Node nextAlternativeTerminal(Node current) {
            return alternativeTerminals.next(current);
         }
      };
      PointMutation pointMutation = new PointMutation(mockRandom, primitiveSet);

      assertEquals(new FunctionNode(function, outputArg1, inputArg2), pointMutation.evolve(dummySelector));
      assertEquals(new FunctionNode(function, inputArg1, outputArg2), pointMutation.evolve(dummySelector));
      assertTrue(dummySelector.isEmpty());
   }
}
