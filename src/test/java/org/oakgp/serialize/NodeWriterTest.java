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
