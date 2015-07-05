/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.evolve.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.DummyPrimitiveSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.DummyValuesMap;

public class PointMutationTest {
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
      GeneticOperator pointMutation = new PointMutation(DummyRandom.EMPTY, primitiveSet);

      Node offspring = pointMutation.evolve(dummySelector);

      assertSame(output, offspring);

      dummySelector.assertEmpty();
   }

   @Test
   public void testFunctionSubNodes() {
      DummyRandom dummyRandom = nextInt(3).returns(1, 2, 0);
      Function rootFunction = new IsZero();
      Function inputFunction = IntegerUtils.INTEGER_UTILS.getAdd();
      Function outputFunction = IntegerUtils.INTEGER_UTILS.getSubtract();
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
      GeneticOperator pointMutation = new PointMutation(dummyRandom, primitiveSet);

      assertEquals(new FunctionNode(rootFunction, new FunctionNode(inputFunction, inputArg1, outputArg2)), pointMutation.evolve(dummySelector));
      assertEquals(new FunctionNode(rootFunction, new FunctionNode(outputFunction, inputArg1, inputArg2)), pointMutation.evolve(dummySelector));
      assertEquals(new FunctionNode(rootFunction, new FunctionNode(inputFunction, outputArg1, inputArg2)), pointMutation.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }
}
