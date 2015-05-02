package org.oakgp.function.math;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class NumberUtilsTest {
   @Test
   public void testNegate() {
      assertNegate("1", "-1");
      assertNegate("-1", "1");
      assertNegate("(+ v0 v1)", "(- 0 (+ v0 v1))");
   }

   private void assertNegate(String before, String after) {
      Node input = readNode(before);
      Node output = readNode(after);
      assertEquals(output, IntegerUtils.INTEGER_UTILS.negate(input));
   }

   @Test
   public void testMultiplyByTwo() {
      assertMultiplyByTwo("v0", "(* 2 v0)");
      assertMultiplyByTwo("(+ v0 v1)", "(* 2 (+ v0 v1))");
   }

   private void assertMultiplyByTwo(String before, String after) {
      Node input = readNode(before);
      Node output = readNode(after);
      assertEquals(output, IntegerUtils.INTEGER_UTILS.multiplyByTwo(input));
   }
}
