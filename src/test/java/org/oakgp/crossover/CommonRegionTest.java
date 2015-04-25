package org.oakgp.crossover;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class CommonRegionTest {
   @Test
   public void test() {
      CommonRegion cr = new CommonRegion();
      Node n1 = readNode("(+ (+ 1 2) (+ 3 4))");
      Node n2 = readNode("(+ 7 (+ 8 9))");
      int count = cr.getNodeCount(n1, n2);
      assertEquals(4, count);
      assertEquals("(+ 7 (+ 8 9))", writeNode(cr.crossoverAt(n1, n2, 0)));
      assertEquals("(+ (+ 1 2) (+ 8 9))", writeNode(cr.crossoverAt(n1, n2, 1)));
      assertEquals("(+ (+ 1 2) (+ 8 4))", writeNode(cr.crossoverAt(n1, n2, 2)));
      assertEquals("(+ (+ 1 2) (+ 3 9))", writeNode(cr.crossoverAt(n1, n2, 3)));
   }
}
