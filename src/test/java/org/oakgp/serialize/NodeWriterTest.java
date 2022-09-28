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
package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.bigDecimalConstant;
import static org.oakgp.TestUtils.bigIntegerConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.doubleConstant;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.longConstant;
import static org.oakgp.TestUtils.stringConstant;
import static org.oakgp.type.CommonTypes.integerToBooleanFunctionType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.mapType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.util.Utils.VOID_NODE;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.VariableNode;

public class NodeWriterTest {
   @Test
   public void testStringConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(stringConstant("hello, world!"));
      assertEquals("\"hello, world!\"", output);
   }

   @Test
   public void testIntegerConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(integerConstant(768));
      assertEquals("768", output);
   }

   @Test
   public void testLongConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(longConstant(768));
      assertEquals("768L", output);
   }

   @Test
   public void testDoubleConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(doubleConstant(768));
      assertEquals("768.0", output);
   }

   @Test
   public void testBigDecimalConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(bigDecimalConstant("768"));
      assertEquals("768D", output);
   }

   @Test
   public void testBigIntegerConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(bigIntegerConstant("768"));
      assertEquals("768I", output);
   }

   @Test
   public void testVoidConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(VOID_NODE);
      assertEquals("void", output);
   }

   @Test
   public void testVariableNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(createVariable(2));
      assertEquals("v2", output);
   }

   @Test
   public void testNamedVariableNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(new VariableNode(2, "qwerty", integerType()));
      assertEquals("qwerty", output);
   }

   @Test
   public void testFunctionNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerType(), integerConstant(5), createVariable(0)));
      assertEquals("(+ 5 v0)", output);
   }

   @Test
   public void testFunctionNodeWithFunctionNodeArguments() {
      NodeWriter writer = new NodeWriter();
      FunctionNode arg1 = new FunctionNode(IntegerUtils.INTEGER_UTILS.getSubtract(), integerType(), integerConstant(5), createVariable(0));
      FunctionNode arg2 = new FunctionNode(IntegerUtils.INTEGER_UTILS.getMultiply(), integerType(), createVariable(1), integerConstant(-6876));
      String output = writer.writeNode(new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerType(), arg1, arg2));
      assertEquals("(+ (- 5 v0) (* v1 -6876))", output);
   }

   @Test
   public void testMap() {
      Map<Object, Object> map = new LinkedHashMap<>();
      map.put("a", 2);
      map.put("b", 5);
      map.put("c", 9);
      ConstantNode input = new ConstantNode(map, mapType(stringType(), integerType()));
      String output = new NodeWriter().writeNode(input);
      assertEquals("{\"a\" 2 \"b\" 5 \"c\" 9}", output);
   }

   @Test
   public void testArguments() {
      ConstantNode input = new ConstantNode(Arrays.asList(integerConstant(6), integerConstant(-2), integerConstant(17)), listType(integerType()));
      String output = new NodeWriter().writeNode(input);
      assertEquals("[6 -2 17]", output);
   }

   @Test
   public void testFunctionAsArgument() {
      ConstantNode criteria = new ConstantNode(IsPositive.getSingleton(), integerToBooleanFunctionType());
      ConstantNode args = new ConstantNode(Arrays.asList(integerConstant(6), integerConstant(-2), integerConstant(17)), listType(integerType()));
      FunctionNode input = new FunctionNode(new Filter(), integerType(), criteria, args);

      String output = new NodeWriter().writeNode(input);

      assertEquals("(filter pos? [6 -2 17])", output);
   }
}
