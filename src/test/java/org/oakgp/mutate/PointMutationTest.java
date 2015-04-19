package org.oakgp.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.NodeEvolver;
import org.oakgp.PrimitiveSet;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Subtract;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.DummyPrimitiveSet;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.DummyValuesMap;
import org.oakgp.util.Random;

public class PointMutationTest {
   // TODO test mutating child nodes, not just the root node (which is all the tests currently do)

   @Test
   public void testTerminal() {
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
      NodeEvolver pointMutation = new PointMutation(DummyRandom.EMPTY, primitiveSet);

      Node offspring = pointMutation.evolve(dummySelector);

      assertSame(output, offspring);
      assertTrue(dummySelector.isEmpty());
   }

   @Test
   public void testFunctionSubNodes() {
      Random dummyRandom = new DummyRandom(3, 1, 2, 0);
      Function rootFunction = new IsZero();
      Function inputFunction = new Add();
      Function outputFunction = new Subtract();
      Node inputArg1 = integerConstant(3);
      Node inputArg2 = integerConstant(7);
      Node input = new FunctionNode(rootFunction, new FunctionNode(inputFunction, inputArg1, inputArg2));
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, input, input);
      Node outputArg1 = integerConstant(9);
      Node outputArg2 = integerConstant(2);
      DummyValuesMap<Function, Function> alternativeFunctions = new DummyValuesMap<>(inputFunction, outputFunction);
      DummyValuesMap<Node, Node> alternativeTerminals = new DummyValuesMap.Builder<Node, Node>().put(inputArg1, outputArg1).put(inputArg2, outputArg2).build();
      PrimitiveSet primitiveSet = new DummyPrimitiveSet() {
         @Override
         public Function nextAlternativeFunction(Function current) {
            return alternativeFunctions.next(current);
         }

         @Override
         public Node nextAlternativeTerminal(Node current) {
            return alternativeTerminals.next(current);
         }
      };
      NodeEvolver pointMutation = new PointMutation(dummyRandom, primitiveSet);

      assertEquals(new FunctionNode(rootFunction, new FunctionNode(inputFunction, inputArg1, outputArg2)), pointMutation.evolve(dummySelector));
      assertEquals(new FunctionNode(rootFunction, new FunctionNode(outputFunction, inputArg1, inputArg2)), pointMutation.evolve(dummySelector));
      assertEquals(new FunctionNode(rootFunction, new FunctionNode(inputFunction, outputArg1, inputArg2)), pointMutation.evolve(dummySelector));
      assertTrue(dummySelector.isEmpty());
   }
}
