package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.singletonRankedCandidateList;

import org.junit.Test;

public class MaxGenerationsWithoutImprovementTerminatorTest {
   @Test
   public void test() {
      assertTerminator(2, 9, 9, 9);
      assertTerminator(3, 9, 9, 9, 9);
      assertTerminator(3, 9, 9, 9, 8, 8, 7, 6, 6, 6, 6);
      assertTerminator(4, 9, 9, 9, 8, 8, 7, 6, 6, 6, 6, 5, 5, 5, 5, 5);
   }

   private void assertTerminator(int maxGenerationsWithoutImprovement, double... fitnesses) {
      MaxGenerationsWithoutImprovementTerminator t = new MaxGenerationsWithoutImprovementTerminator(maxGenerationsWithoutImprovement);
      for (int i = 0; i < fitnesses.length - 1; i++) {
         assertFalse(t.test(singletonRankedCandidateList(fitnesses[i])));
      }
      assertTrue(t.test(singletonRankedCandidateList(fitnesses[fitnesses.length - 1])));
   }
}
