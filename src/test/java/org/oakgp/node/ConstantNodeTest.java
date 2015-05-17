package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.stringConstant;
import static org.oakgp.Type.integerType;

import org.junit.Test;

public class ConstantNodeTest {
   @Test
   public void testGetters() {
      final ConstantNode n = integerConstant(7);
      assertEquals(1, n.getNodeCount());
      assertEquals(1, n.getHeight());
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
   public void testCountStrategy() {
      final ConstantNode c = integerConstant(7);
      assertEquals(1, c.getNodeCount(n -> n == c));
      assertEquals(0, c.getNodeCount(n -> n != c));
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

      final ConstantNode n1 = stringConstant(a);
      final ConstantNode n2 = stringConstant(b);
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
      assertNotEquals(n, stringConstant("7"));
      assertNotEquals(n, Integer.valueOf(7));
   }
}
