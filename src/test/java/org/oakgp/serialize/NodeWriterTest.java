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
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.FunctionNode;

public class NodeWriterTest {
   @Test
   public void testConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(integerConstant(768));
      assertEquals("768", output);
   }

   @Test
   public void testVariableNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(createVariable(2));
      assertEquals("v2", output);
   }

   @Test
   public void testFunctionNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerConstant(5), createVariable(0)));
      assertEquals("(+ 5 v0)", output);
   }

   @Test
   public void testFunctionNodeWithFunctionNodeArguments() {
      NodeWriter writer = new NodeWriter();
      FunctionNode arg1 = new FunctionNode(IntegerUtils.INTEGER_UTILS.getSubtract(), integerConstant(5), createVariable(0));
      FunctionNode arg2 = new FunctionNode(IntegerUtils.INTEGER_UTILS.getMultiply(), createVariable(1), integerConstant(-6876));
      String output = writer.writeNode(new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), arg1, arg2));
      assertEquals("(+ (- 5 v0) (* v1 -6876))", output);
   }
}
