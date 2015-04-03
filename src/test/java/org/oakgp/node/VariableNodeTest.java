package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Assignments;

public class VariableNodeTest {
   @Test
   public void testGetters() {
      final int id = 7;
      final VariableNode v = createVariable(id);
      assertEquals(id, v.getId());
      assertEquals(1, v.getNodeCount());
      assertSame(INTEGER, v.getType());
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
      final ConstantNode c = createConstant(Integer.MAX_VALUE);
      assertSame(v, v.replaceAt(0, t -> t));
      assertSame(c, v.replaceAt(0, t -> c));
   }

   @Test
   public void testEqualsAndHashCode() {
      final VariableNode n1 = createVariable(1);
      final VariableNode n2 = createVariable(1);
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   @Test
   public void testNotEquals() {
      final VariableNode n = createVariable(1);
      assertNotEquals(n, createVariable(0));
      assertNotEquals(n, createVariable(2));
      assertNotEquals(n, createConstant(1));
      assertNotEquals(n, new Integer(1));
   }
}
