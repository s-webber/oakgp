package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;

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
      List<RankedCandidate> candidates = Collections.singletonList(new RankedCandidate(mock(Node.class), fitness));
      return t.test(candidates);
   }
}
