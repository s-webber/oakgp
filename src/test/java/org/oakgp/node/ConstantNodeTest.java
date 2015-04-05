package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import org.junit.Test;

public class ConstantNodeTest {
   @Test
   public void testGetters() {
      final ConstantNode n = integerConstant(7);
      assertEquals(1, n.getNodeCount());
      assertSame(integerType(), n.getType());
   }

   @Test
   public void testToString() {
      assertEquals("5", integerConstant(5).toString());
   }

   @Test
   public void testEvaluate() {
      Integer expected = 9;
      ConstantNode n = integerConstant(expected);
      Object actual = n.evaluate(null);
      assertSame(expected, actual);
   }

   @Test
   public void testReplaceAt() {
      ConstantNode n1 = integerConstant(9);
      ConstantNode n2 = integerConstant(5);
      assertEquals(n1, n1.replaceAt(0, t -> t));
      assertEquals(n2, n1.replaceAt(0, t -> n2));
   }

   @Test
   public void testEqualsAndHashCodeIntegers() {
      final ConstantNode n1 = integerConstant(7);
      final ConstantNode n2 = integerConstant(7);
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   @Test
   public void testEqualsAndHashCodeStrings() {
      String a = new String("hello");
      String b = new String("hello");
      assertNotSame(a, b);

      final ConstantNode n1 = new ConstantNode(a, stringType());
      final ConstantNode n2 = new ConstantNode(b, stringType());
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   @Test
   public void testNotEquals() {
      final ConstantNode n = integerConstant(7);
      assertNotEquals(n, integerConstant(8));
      assertNotEquals(n, integerConstant(-7));
      assertNotEquals(n, createVariable(7));
      assertNotEquals(n, new ConstantNode("7", stringType()));
      assertNotEquals(n, new Integer(7));
   }
}
