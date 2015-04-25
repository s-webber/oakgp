package org.oakgp.crossover;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class CommonRegionTest {
   @Test
   public void test() {
      Node n1 = readNode("(+ (+ 1 2) (+ 3 4))");
      Node n2 = readNode("(+ 7 (+ 8 9))");
      int count = CommonRegion.getNodeCount(n1, n2);
      assertEquals(4, count);
      assertNodeEquals("(+ (+ 1 2) (+ 8 4))", CommonRegion.crossoverAt(n1, n2, 0));
      assertNodeEquals("(+ (+ 1 2) (+ 3 9))", CommonRegion.crossoverAt(n1, n2, 1));
      assertNodeEquals("(+ (+ 1 2) (+ 8 9))", CommonRegion.crossoverAt(n1, n2, 2));
      assertNodeEquals("(+ 7 (+ 8 9))", CommonRegion.crossoverAt(n1, n2, 3));
   }
}
