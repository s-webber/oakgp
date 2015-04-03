package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;

import org.junit.Test;
import org.oakgp.node.FunctionNode;
import org.oakgp.operator.math.Add;
import org.oakgp.operator.math.Multiply;
import org.oakgp.operator.math.Subtract;

public class NodeWriterTest {
   // TODO test writeNode when no symbol exists in SymbolMap for the specified Operator

   @Test
   public void testConstantNode() {
      NodeWriter writer = new NodeWriter();
      String output = writer.writeNode(createConstant(768));
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
      String output = writer.writeNode(new FunctionNode(new Add(), createConstant(5), createVariable(0)));
      assertEquals("(+ 5 v0)", output);
   }

   @Test
   public void testFunctionNodeWithFunctionNodeArguments() {
      NodeWriter writer = new NodeWriter();
      FunctionNode arg1 = new FunctionNode(new Subtract(), createConstant(5), createVariable(0));
      FunctionNode arg2 = new FunctionNode(new Multiply(), createVariable(1), createConstant(-6876));
      String output = writer.writeNode(new FunctionNode(new Add(), arg1, arg2));
      assertEquals("(+ (- 5 v0) (* v1 -6876))", output);
   }
}
