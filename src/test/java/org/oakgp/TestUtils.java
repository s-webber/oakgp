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
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.type.CommonTypes.bigDecimalType;
import static org.oakgp.type.CommonTypes.bigIntegerType;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.longType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;

public class TestUtils {
   public static final VariableSet VARIABLE_SET = VariableSet.createVariableSet(createIntegerTypeArray(100));
   private static final FunctionSet FUNCTION_SET = createDefaultFunctions();
   private static final AtomicLong TYPE_CTR = new AtomicLong();

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
      List<Node> outputs = new ArrayList<>();
      try (NodeReader nr = new NodeReader(input, FUNCTION_SET, new ConstantNode[0], VARIABLE_SET)) {
         while (!nr.isEndOfStream()) {
            outputs.add(nr.readNode());
         }
      } catch (IOException e) {
         throw new RuntimeException("IOException caught reading: " + input, e);
      }
      return outputs;
   }

   private static FunctionSet createDefaultFunctions() {
      FunctionSetBuilder builder = new FunctionSetBuilder();

      builder.add(IntegerUtils.INTEGER_UTILS.getAdd());
      builder.add(IntegerUtils.INTEGER_UTILS.getSubtract());
      builder.add(IntegerUtils.INTEGER_UTILS.getMultiply());
      builder.add(IntegerUtils.INTEGER_UTILS.getDivide());

      builder.add(LessThan.getSingleton(), integerType());
      builder.add(LessThanOrEqual.getSingleton(), integerType());
      builder.add(new GreaterThan(), integerType());
      builder.add(new GreaterThanOrEqual(), integerType());
      builder.add(new Equal(), integerType());
      builder.add(new NotEqual(), integerType());

      builder.add(new If(), integerType());

      Function orElse = new OrElse();
      builder.add(orElse, stringType());
      builder.add(orElse, integerType());

      builder.add(new Reduce(integerType()));

      Filter filter = new Filter();
      builder.add(filter, integerType());

      org.oakgp.function.hof.Map transform = new org.oakgp.function.hof.Map();
      builder.add(transform, integerType(), booleanType());

      builder.add(new IsPositive());
      builder.add(new IsNegative());
      builder.add(new IsZero());

      Count count = new Count();
      builder.add(count, integerType());
      builder.add(count, booleanType());

      return builder.build();
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
      return new ConstantNode(value, booleanType());
   }

   public static ConstantNode stringConstant(String value) {
      return new ConstantNode(value, stringType());
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

   @SafeVarargs
   public static <T> Set<T> asSet(T... values) { // TODO unit test
      Set<T> result = new HashSet<>(Arrays.asList(values));
      if (result.size() != values.length) {
         throw new RuntimeException();
      }
      return Collections.unmodifiableSet(result);
   }

   public static Type uniqueType() { // TODO unit test
      // TODO confirm name really is unique by first calling Types.type
      return Types.declareType(uniqueTypeName());
   }

   public static String uniqueTypeName() { // TODO unit test
      return "TestUtils-uniqueTypeName-" + TYPE_CTR.incrementAndGet();
   }

   public static FunctionSet createFunctionSet(Function... functions) {
      return new FunctionSetBuilder().addAll(functions).build();
   }
}
