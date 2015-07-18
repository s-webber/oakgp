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
package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Type.bigDecimalType;
import static org.oakgp.Type.bigIntegerType;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.doubleType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.longType;
import static org.oakgp.Type.stringType;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.OrElse;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;

public class TestUtils {
   public static final VariableSet VARIABLE_SET = VariableSet.createVariableSet(createIntegerTypeArray(100));
   private static final Function[] FUNCTIONS = createDefaultFunctions();

   public static void assertVariable(int expectedId, Node node) {
      assertTrue(node instanceof VariableNode);
      assertEquals(expectedId, ((VariableNode) node).getId());
   }

   public static void assertConstant(Object expectedValue, Node node) {
      assertTrue(node instanceof ConstantNode);
      assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static void assertUnmodifiable(List list) {
      assertEquals("java.util.Collections$UnmodifiableRandomAccessList", list.getClass().getName());
      try {
         list.add(new Object());
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public static String writeNode(Node input) {
      return new NodeWriter().writeNode(input);
   }

   public static FunctionNode readFunctionNode(String input) {
      return (FunctionNode) readNode(input);
   }

   public static Node readNode(String input) {
      List<Node> outputs = readNodes(input);
      assertEquals(1, outputs.size());
      return outputs.get(0);
   }

   public static List<Node> readNodes(String input) {
      return readNodes(input, FUNCTIONS, VARIABLE_SET);
   }

   private static List<Node> readNodes(String input, Function[] functions, VariableSet variableSet) {
      List<Node> outputs = new ArrayList<>();
      try (NodeReader nr = new NodeReader(input, functions, new ConstantNode[0], variableSet)) {
         while (!nr.isEndOfStream()) {
            outputs.add(nr.readNode());
         }
      } catch (IOException e) {
         throw new RuntimeException("IOException caught reading: " + input, e);
      }
      return outputs;
   }

   private static Function[] createDefaultFunctions() {
      List<Function> functions = new ArrayList<>();

      functions.add(IntegerUtils.INTEGER_UTILS.getAdd());
      functions.add(IntegerUtils.INTEGER_UTILS.getSubtract());
      functions.add(IntegerUtils.INTEGER_UTILS.getMultiply());
      functions.add(IntegerUtils.INTEGER_UTILS.getDivide());

      functions.add(new LessThan(integerType()));
      functions.add(new LessThanOrEqual(integerType()));
      functions.add(new GreaterThan(integerType()));
      functions.add(new GreaterThanOrEqual(integerType()));
      functions.add(new Equal(integerType()));
      functions.add(new NotEqual(integerType()));

      functions.add(new If(integerType()));
      functions.add(new OrElse(stringType()));
      functions.add(new OrElse(integerType()));

      functions.add(new Reduce(integerType()));
      functions.add(new Filter(integerType()));
      functions.add(new org.oakgp.function.hof.Map(integerType(), booleanType()));

      functions.add(new IsPositive());
      functions.add(new IsNegative());
      functions.add(new IsZero());

      functions.add(new Count(integerType()));
      functions.add(new Count(booleanType()));

      return functions.toArray(new Function[functions.size()]);
   }

   public static Arguments createArguments(String... expressions) {
      Node[] args = new Node[expressions.length];
      for (int i = 0; i < expressions.length; i++) {
         args[i] = readNode(expressions[i]);
      }
      return Arguments.createArguments(args);
   }

   public static ConstantNode integerConstant(int value) {
      return new ConstantNode(value, integerType());
   }

   public static ConstantNode longConstant(long value) {
      return new ConstantNode(value, longType());
   }

   public static ConstantNode doubleConstant(double value) {
      return new ConstantNode(value, doubleType());
   }

   public static ConstantNode bigIntegerConstant(String value) {
      return new ConstantNode(new BigInteger(value), bigIntegerType());
   }

   public static ConstantNode bigDecimalConstant(String value) {
      return new ConstantNode(new BigDecimal(value), bigDecimalType());
   }

   public static ConstantNode booleanConstant(Boolean value) {
      return new ConstantNode(value, Type.booleanType());
   }

   public static ConstantNode stringConstant(String value) {
      return new ConstantNode(value, Type.stringType());
   }

   public static VariableNode createVariable(int id) {
      return VARIABLE_SET.getById(id);
   }

   public static void assertRankedCandidate(RankedCandidate actual, Node expectedNode, double expectedFitness) {
      assertSame(expectedNode, actual.getNode());
      assertEquals(expectedFitness, actual.getFitness(), 0);
   }

   public static void assertNodeEquals(String expected, Node actual) {
      assertEquals(expected, writeNode(actual));
   }

   public static RankedCandidates singletonRankedCandidates() {
      return singletonRankedCandidates(1);
   }

   public static RankedCandidates singletonRankedCandidates(double fitness) {
      return new RankedCandidates(new RankedCandidate[] { new RankedCandidate(mockNode(), fitness) });
   }

   public static Node mockNode() {
      return mockNode(integerType());
   }

   public static Node mockNode(Type type) {
      Node mockNode = mock(Node.class);
      given(mockNode.getType()).willReturn(type);
      return mockNode;
   }
}
