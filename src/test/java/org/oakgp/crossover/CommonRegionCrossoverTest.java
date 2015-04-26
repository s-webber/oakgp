package org.oakgp.crossover;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class CommonRegionCrossoverTest {
   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = nextInt(4).returns(1);
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 5 (+ 1 2))", "(* 7 (- 8 v5))");

      NodeEvolver c = new CommonRegionCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("(+ 5 (+ 8 2))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }
}