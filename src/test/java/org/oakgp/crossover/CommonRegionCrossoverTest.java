package org.oakgp.crossover;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class CommonRegionCrossoverTest {
   // TODO test mixed types

   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = nextInt(4).returns(1);
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 5 (+ 1 2))", "(* 7 (- 8 v5))");

      GeneticOperator c = new CommonRegionCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertNodeEquals("(+ 5 (+ 8 2))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }
}
