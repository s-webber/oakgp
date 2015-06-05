package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.Type.integerType;

import java.util.function.Function;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.Type;

public class VariableNodeTest {
   @Test
   public void testGetters() {
      final int id = 7;
      final VariableNode v = createVariable(id);
      assertEquals(id, v.getId());
      assertEquals(1, v.getNodeCount());
      assertEquals(1, v.getHeight());
      assertSame(integerType(), v.getType());
   }

   @Test
   public void testToString() {
      assertEquals("v5", createVariable(5).toString());
   }

   @Test
   public void testEvaluate() {
      final Integer expected = 9;
      final VariableNode v = createVariable(0);
      final Assignments assignments = createAssignments(expected);
      final Object actual = (int) v.evaluate(assignments);
      assertSame(expected, actual);
   }

   @Test
   public void testReplaceAt() {
      final VariableNode v = createVariable(0);
      final ConstantNode c = integerConstant(Integer.MAX_VALUE);
      assertSame(v, v.replaceAt(0, t -> t));
      assertSame(c, v.replaceAt(0, t -> c));
   }

   @Test
   public void testReplaceAll() {
      final VariableNode v = createVariable(0);
      final ConstantNode c = integerConstant(Integer.MAX_VALUE);
      Function<Node, Node> replacement = n -> c;
      assertSame(c, v.replaceAll(n -> n == v, replacement));
      assertSame(v, v.replaceAll(n -> n == c, replacement));
   }

   @Test
   public void testCountStrategy() {
      final VariableNode v = createVariable(0);
      assertEquals(1, v.getNodeCount(n -> n == v));
      assertEquals(0, v.getNodeCount(n -> n != v));
   }

   @Test
   public void testEqualsAndHashCode() {
      final VariableNode n1 = new VariableNode(1, Type.integerType());
      final VariableNode n2 = new VariableNode(1, Type.integerType());
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n2, n2);
      assertEquals(n1.hashCode(), n2.hashCode());
      // NOTE: *not* over-riding equals(Object) as two VariableNode references are only "equal" if they refer to the same instance
      assertNotEquals(n1, n2);
   }

   @Test
   public void testNotEquals() {
      final VariableNode n = createVariable(1);
      assertNotEquals(n, createVariable(0));
      assertNotEquals(n, createVariable(2));
      assertNotEquals(n, integerConstant(1));
      assertNotEquals(n, Integer.valueOf(1));
   }
}
