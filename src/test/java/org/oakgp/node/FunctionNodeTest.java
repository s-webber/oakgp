package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.INTEGER;

import java.util.function.Function;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Operator;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;

public class FunctionNodeTest {
   @Test
   public void testConstructors() {
      Operator operator = new Multiply();
      ConstantNode arg1 = createConstant(42);
      VariableNode arg2 = createVariable(0);

      // construct using Node array
      FunctionNode n1 = new FunctionNode(operator, arg1, arg2);

      // Construct using Arguments
      Arguments arguments = createArguments(arg1, arg2);
      FunctionNode n2 = new FunctionNode(operator, arguments);

      // assert the result is the same
      assertEquals(n1, n2);
   }

   @Test
   public void testEvaluate() {
      Operator operator = new Multiply();
      Arguments arguments = createArguments(createConstant(42), createVariable(0));
      FunctionNode functionNode = new FunctionNode(operator, arguments);

      assertSame(operator, functionNode.getOperator());
      assertSame(arguments, functionNode.getArguments());

      Assignments assignments = createAssignments(3);
      assertEquals(126, functionNode.evaluate(assignments));
   }

   @Test
   public void testReplaceAt() {
      FunctionNode n = createFunctionNode();
      Function<Node, Node> replacement = t -> createConstant(9);

      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply 9 v1) (org.oakgp.function.math.Add v2 1))", n.replaceAt(0, replacement)
            .toString());
      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply v0 9) (org.oakgp.function.math.Add v2 1))", n.replaceAt(1, replacement)
            .toString());
      assertEquals("(org.oakgp.function.math.Add 9 (org.oakgp.function.math.Add v2 1))", n.replaceAt(2, replacement).toString());
      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply v0 v1) (org.oakgp.function.math.Add 9 1))", n.replaceAt(3, replacement)
            .toString());
      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply v0 v1) (org.oakgp.function.math.Add v2 9))", n.replaceAt(4, replacement)
            .toString());
      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply v0 v1) 9)", n.replaceAt(5, replacement).toString());
      assertEquals("9", n.replaceAt(6, replacement).toString());
   }

   @Test
   public void testGetAt() {
      FunctionNode n = createFunctionNode();

      assertEquals("v0", n.getAt(0).toString());
      assertEquals("v1", n.getAt(1).toString());
      assertEquals("(org.oakgp.function.math.Multiply v0 v1)", n.getAt(2).toString());
      assertEquals("v2", n.getAt(3).toString());
      assertEquals("1", n.getAt(4).toString());
      assertEquals("(org.oakgp.function.math.Add v2 1)", n.getAt(5).toString());
      assertEquals("(org.oakgp.function.math.Add (org.oakgp.function.math.Multiply v0 v1) (org.oakgp.function.math.Add v2 1))", n.getAt(6).toString());
   }

   @Test
   public void testGetType() {
      FunctionNode n = createFunctionNode();
      assertSame(INTEGER, n.getType());
   }

   @Test
   public void testEqualsAndHashCode() {
      final FunctionNode n1 = createFunctionNode();
      final FunctionNode n2 = createFunctionNode();
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   public void testEquals() {
      Node n1 = readNode("(org.oakgp.function.math.Multiply 288 v1)");
      Node n2 = readNode("(org.oakgp.function.math.Multiply 288 v1)");
      assertEquals(n1, n1);
      assertEquals(n1, n2);
      assertEquals(n2, n1);
   }

   @Test
   public void testNotEquals() {
      final FunctionNode n = new FunctionNode(new Add(), createVariable(0), createConstant(7));

      // verify (sanity-check) that equals will return true when it should
      assertEquals(n, new FunctionNode(new Add(), createVariable(0), createConstant(7)));

      // test different operator
      assertNotEquals(n, new FunctionNode(new Multiply(), createVariable(0), createConstant(7)));

      // test different first argument
      assertNotEquals(n, new FunctionNode(new Add(), createVariable(1), createConstant(7)));

      // test different second argument
      assertNotEquals(n, new FunctionNode(new Add(), createVariable(0), createConstant(6)));

      // test same arguments but different order
      assertNotEquals(n, new FunctionNode(new Add(), createConstant(7), createVariable(0)));

      // test wrong arguments but different order
      assertNotEquals(n, new FunctionNode(new Add(), createConstant(0), createVariable(7)));

      // test extra argument
      assertNotEquals(n, new FunctionNode(new Add(), createVariable(0), createConstant(7), createConstant(7)));

      // test one less argument
      assertNotEquals(n, new FunctionNode(new Add(), createVariable(0)));

      // test no arguments
      assertNotEquals(n, new FunctionNode(new Add()));

      // test not equal to other Node implementations
      assertNotEquals(n, createConstant(7));

      // test not equal to other non-Node instances
      assertNotEquals(n, new Object());
   }

   /** Returns representation of: {@code (x*y)+z+1} */
   private FunctionNode createFunctionNode() {
      return new FunctionNode(new Add(), new FunctionNode(new Multiply(), createVariable(0), createVariable(1)), new FunctionNode(new Add(), createVariable(2),
            createConstant(1)));
   }
}
