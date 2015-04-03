package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.readNodes;
import static org.oakgp.Type.integerType;

import java.util.List;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class NodeReaderTest {
   // TODO test error conditions using assetReadException

   @Test
   public void testZero() {
      assertParseLiteral(0);
   }

   @Test
   public void testNegativeConstantNode() {
      assertParseLiteral(-9);
   }

   @Test
   public void testSingleCharacterConstantNode() {
      assertParseLiteral(4);
   }

   @Test
   public void testMulipleCharacterConstantNode() {
      assertParseLiteral(147);
   }

   @Test
   public void testTrue() {
      assertParseLiteral(Boolean.TRUE);
   }

   @Test
   public void testFalse() {
      assertParseLiteral(Boolean.FALSE);
   }

   @Test
   public void testSingleWordString() {
      assertParseLiteral("\"hello\"", "hello");
   }

   @Test
   public void testMultiWordString() {
      assertParseLiteral("\"Hello, world!\"", "Hello, world!");
   }

   @Test
   public void testFunctionSymbol() {
      // TODO test functions with other types. e.g. +/Add
      assertParseFunction("pos?", IsPositive.class);
   }

   @Test
   public void testEmptyArray() {
      assertParseLiteral("[]", Arguments.createArguments());
   }

   @Test
   public void testTypeArray() {
      Arguments expected = Arguments.createArguments(new ConstantNode(9, integerType()), new ConstantNode(2, integerType()), createVariable(0),
            new ConstantNode(7, integerType()));
      assertParseLiteral("[9 2 v0 7]", expected);
   }

   @Test
   public void testMixedTypeArray() {
      assetReadException("[true 9 false v0]", "Mixed type array elements: boolean and integer");
   }

   @Test
   public void testSingleDigitIdVariableNode() {
      assertParseVariable(1);
   }

   @Test
   public void testMultipleDigitIdVariableNode() {
      assertParseVariable(78);
   }

   @Test
   public void testFunctionNodeSpecifiedBySymbol() {
      assertParseFunction("(+ 7 21)", "(org.oakgp.function.math.Add 7 21)");
   }

   @Test
   public void testFunctionNodeWithFunctionNodeArguments() {
      assertParseFunction("(+ (- v0 587) (* 43 v1))",
            "(org.oakgp.function.math.Add (org.oakgp.function.math.Subtract v0 587) (org.oakgp.function.math.Multiply 43 v1))");
   }

   @Test
   public void testEmptyString() {
      String input = "";
      List<Node> outputs = readNodes(input);
      assertTrue(outputs.isEmpty());
   }

   @Test
   public void testWhitespace() {
      String input = " \r\n\t\t  ";
      List<Node> outputs = readNodes(input);
      assertTrue(outputs.isEmpty());
   }

   @Test
   public void testPadded() {
      String input = " \r\n42\t\t  ";
      assertParseLiteral(input, 42);
   }

   @Test
   public void testMulipleNodes() {
      String[] inputs = { "6", "(+ v0 v1)", "42", "v0", "(+ 1 2)", "v98" };
      String combinedInput = " " + inputs[0] + inputs[1] + inputs[2] + " " + inputs[3] + "\n\r\t\t\t" + inputs[4] + "       \n   " + inputs[5] + "\r\n";
      List<Node> outputs = readNodes(combinedInput);
      assertEquals(inputs.length, outputs.size());
      for (int i = 0; i < inputs.length; i++) {
         assertEquals(inputs[i].toString().replace("+", "org.oakgp.function.math.Add"), outputs.get(i).toString());
      }
   }

   private void assertParseLiteral(Object expected) {
      assertParseLiteral(expected.toString(), expected);
   }

   private void assertParseLiteral(String input, Object expected) {
      Node output = readNode(input);
      assertSame(ConstantNode.class, output.getClass());
      assertEquals(expected.toString(), output.toString());
      assertEquals(expected, ((ConstantNode) output).evaluate(null));
   }

   private void assertParseFunction(String input, Class<? extends Function> expected) {
      Node output = readNode(input);
      assertSame(ConstantNode.class, output.getClass());
      // TODO assertSame(integerToBooleanFunctionType(), output.getType());
      assertEquals(expected, ((ConstantNode) output).evaluate(null).getClass());
   }

   private void assertParseVariable(int id) {
      String input = "v" + id;
      Node output = readNode(input);
      assertSame(VariableNode.class, output.getClass());
      assertEquals(id, ((VariableNode) output).getId());
      assertEquals(input, output.toString());
   }

   private void assertParseFunction(String input, String expected) {
      Node output = readNode(input);
      assertSame(FunctionNode.class, output.getClass());
      assertEquals(expected, output.toString());
   }

   private void assetReadException(String input, String expectedMessage) {
      try {
         readNode(input);
         fail();
      } catch (RuntimeException e) {
         assertEquals(expectedMessage, e.getMessage());
      }
   }
}
