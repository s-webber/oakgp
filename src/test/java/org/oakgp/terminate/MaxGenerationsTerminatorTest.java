package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;

public class MaxGenerationsTerminatorTest {
   @Test
   public void test() {
      MaxGenerationsTerminator t = new MaxGenerationsTerminator(3);
      List<RankedCandidate> candidates = Collections.singletonList(new RankedCandidate(mock(Node.class), 1));
      assertFalse(t.test(candidates));
      assertFalse(t.test(candidates));
      assertFalse(t.test(candidates));
      assertTrue(t.test(candidates));
   }
}
