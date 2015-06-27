package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.singletonRankedCandidateList;

import org.junit.Test;

public class TargetFitnessTerminatorTest {
   @Test
   public void test() {
      TargetFitnessTerminator t = new TargetFitnessTerminator(c -> c.getFitness() == 0);

      assertTrue(shouldTerminate(t, 0));

      assertFalse(shouldTerminate(t, 1));
      assertFalse(shouldTerminate(t, -1));
      assertFalse(shouldTerminate(t, 2));
      assertFalse(shouldTerminate(t, -2));
   }

   private boolean shouldTerminate(TargetFitnessTerminator t, double fitness) {
      return t.test(singletonRankedCandidateList(fitness));
   }
}
