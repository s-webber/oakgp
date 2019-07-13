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
package org.oakgp.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.type.Types.Type;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.Random;

public class PrimitiveSetImplTest {
   // TODO add extra tests for when: a) numVariables=0, b) numVariables=1 and c) constants.length=0

   private static final double VARIABLE_RATIO = .6;
   private static final ConstantNode[] CONSTANTS = { integerConstant(7), integerConstant(8), integerConstant(9) };
   private static final Type[] VARIABLE_TYPES = createIntegerTypeArray(3);
   private static final Function[] FUNCTIONS = new Function[] { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(),
         IntegerUtils.INTEGER_UTILS.getMultiply(), new If(integerType()), LessThan.create(integerType()), LessThanOrEqual.create(integerType()),
         new GreaterThan(integerType()), new GreaterThanOrEqual(integerType()), new Equal(integerType()), new NotEqual(integerType()) };

   @Test
   public void testHasFunctions() {
      PrimitiveSet p = createWithFunctions(DummyRandom.EMPTY);
      assertTrue(p.hasFunctions(integerType()));
      assertFalse(p.hasFunctions(stringType()));
   }

   @Test
   public void testHasTerminals() {
      PrimitiveSet p = createWithTerminals(DummyRandom.EMPTY);
      assertTrue(p.hasTerminals(integerType()));
      assertFalse(p.hasTerminals(stringType()));
   }

   @Test
   public void testNextFunction() {
      Random mockRandom = mock(Random.class);
      // mock randomly selecting one of the 4 functions in OPERATORS with an integer return type
      given(mockRandom.nextInt(4)).willReturn(1, 0, 2, 1, 2, 0, 3);
      // mock randomly selecting one of the 6 functions in OPERATORS with a boolean return type
      given(mockRandom.nextInt(6)).willReturn(1, 0, 5, 4);

      PrimitiveSet functionSet = createWithFunctions(mockRandom);

      // TODO test with more than just integerType()
      assertSame(FUNCTIONS[1], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[1], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[3], functionSet.nextFunction(integerType()));

      assertSame(FUNCTIONS[5], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[4], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[9], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[8], functionSet.nextFunction(booleanType()));
   }

   @Test
   public void testNextAlternativeFunction() {
      Random mockRandom = mock(Random.class);
      PrimitiveSet functionSet = createWithFunctions(mockRandom);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[1], functionSet.nextAlternativeFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternativeFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternativeFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternativeFunction(FUNCTIONS[0]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextAlternativeFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[0], functionSet.nextAlternativeFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternativeFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternativeFunction(FUNCTIONS[1]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextAlternativeFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternativeFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[0], functionSet.nextAlternativeFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternativeFunction(FUNCTIONS[2]));
   }

   @Test
   public void testNextTerminal() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(0.0, VARIABLE_RATIO, VARIABLE_RATIO + .01, .9, VARIABLE_RATIO - .01, .7);
      given(mockRandom.nextInt(3)).willReturn(1, 0, 2, 1, 0, 2);

      PrimitiveSet terminalSet = createWithTerminals(mockRandom);

      // TODO test with more than just integerType()
      assertVariable(1, terminalSet.nextTerminal(integerType()));
      assertConstant(7, terminalSet.nextTerminal(integerType()));
      assertConstant(9, terminalSet.nextTerminal(integerType()));
      assertConstant(8, terminalSet.nextTerminal(integerType()));
      assertVariable(0, terminalSet.nextTerminal(integerType()));
      assertConstant(9, terminalSet.nextTerminal(integerType()));
   }

   @Test
   public void testNextAlternativeConstant() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO); // this will force constants to be produced

      PrimitiveSet terminalSet = createWithTerminals(mockRandom);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(8, terminalSet.nextAlternativeTerminal(CONSTANTS[0]));
      assertConstant(8, terminalSet.nextAlternativeTerminal(CONSTANTS[0]));
      assertConstant(9, terminalSet.nextAlternativeTerminal(CONSTANTS[0]));
      assertConstant(9, terminalSet.nextAlternativeTerminal(CONSTANTS[0]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(7, terminalSet.nextAlternativeTerminal(CONSTANTS[1]));
      assertConstant(7, terminalSet.nextAlternativeTerminal(CONSTANTS[1]));
      assertConstant(9, terminalSet.nextAlternativeTerminal(CONSTANTS[1]));
      assertConstant(9, terminalSet.nextAlternativeTerminal(CONSTANTS[1]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(7, terminalSet.nextAlternativeTerminal(CONSTANTS[2]));
      assertConstant(8, terminalSet.nextAlternativeTerminal(CONSTANTS[2]));
      assertConstant(7, terminalSet.nextAlternativeTerminal(CONSTANTS[2]));
      assertConstant(8, terminalSet.nextAlternativeTerminal(CONSTANTS[2]));

      given(mockRandom.nextInt(3)).willReturn(2, 0, 1);
      assertConstant(9, terminalSet.nextAlternativeTerminal(createVariable(9)));
      assertConstant(7, terminalSet.nextAlternativeTerminal(createVariable(9)));
      assertConstant(8, terminalSet.nextAlternativeTerminal(createVariable(9)));
   }

   @Test
   public void testNextAlternativeVariable() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO - .01); // this will force variables to be produced

      ConstantSet constantSet = new ConstantSet(CONSTANTS);
      VariableSet variableSet = VariableSet.createVariableSet(VARIABLE_TYPES);
      PrimitiveSet terminalSet = new PrimitiveSetImpl(null, constantSet, variableSet, mockRandom, VARIABLE_RATIO);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      VariableNode firstVariable = variableSet.getById(0);
      assertVariable(1, terminalSet.nextAlternativeTerminal(firstVariable));
      assertVariable(1, terminalSet.nextAlternativeTerminal(firstVariable));
      assertVariable(2, terminalSet.nextAlternativeTerminal(firstVariable));
      assertVariable(2, terminalSet.nextAlternativeTerminal(firstVariable));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      VariableNode secondVariable = variableSet.getById(1);
      assertVariable(0, terminalSet.nextAlternativeTerminal(secondVariable));
      assertVariable(0, terminalSet.nextAlternativeTerminal(secondVariable));
      assertVariable(2, terminalSet.nextAlternativeTerminal(secondVariable));
      assertVariable(2, terminalSet.nextAlternativeTerminal(secondVariable));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      VariableNode thirdVariable = variableSet.getById(2);
      assertVariable(0, terminalSet.nextAlternativeTerminal(thirdVariable));
      assertVariable(1, terminalSet.nextAlternativeTerminal(thirdVariable));
      assertVariable(0, terminalSet.nextAlternativeTerminal(thirdVariable));
      assertVariable(1, terminalSet.nextAlternativeTerminal(thirdVariable));

      given(mockRandom.nextInt(3)).willReturn(2, 0, 1);
      ConstantNode constantNode = integerConstant(9);
      assertVariable(2, terminalSet.nextAlternativeTerminal(constantNode));
      assertVariable(0, terminalSet.nextAlternativeTerminal(constantNode));
      assertVariable(1, terminalSet.nextAlternativeTerminal(constantNode));
   }

   private PrimitiveSet createWithTerminals(Random random) {
      ConstantSet constantSet = new ConstantSet(CONSTANTS);
      VariableSet variableSet = VariableSet.createVariableSet(VARIABLE_TYPES);
      return new PrimitiveSetImpl(null, constantSet, variableSet, random, VARIABLE_RATIO);
   }

   private PrimitiveSet createWithFunctions(Random random) {
      return new PrimitiveSetImpl(new FunctionSet(FUNCTIONS), null, null, random, .1);
   }

   public static void assertVariable(int expectedId, Node node) {
      assertTrue(node instanceof VariableNode);
      assertEquals(expectedId, ((VariableNode) node).getId());
   }

   public static void assertConstant(Object expectedValue, Node node) {
      assertTrue(node instanceof ConstantNode);
      assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
   }
}
