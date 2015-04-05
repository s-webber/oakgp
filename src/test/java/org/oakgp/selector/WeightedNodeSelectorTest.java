package org.oakgp.selector;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.integerConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;
import org.oakgp.util.Random;

public class WeightedNodeSelectorTest {
   @Test
   public void test() {
      Node first = integerConstant(0);
      Node second = integerConstant(0);
      Node third = integerConstant(0);
      List<RankedCandidate> candidates = Arrays.asList(new RankedCandidate(first, 0), new RankedCandidate(second, 0), new RankedCandidate(third, 0));

      Random mockRandom = mock(Random.class);
      given(mockRandom.nextBoolean()).willReturn(true, false, false, false, true, false, true, true, false, false);

      WeightedNodeSelectorFactory factory = new WeightedNodeSelectorFactory(mockRandom);
      WeightedNodeSelector selector = factory.getSelector(candidates);

      assertSame(first, selector.next());
      assertSame(third, selector.next());
      assertSame(second, selector.next());
      assertSame(second, selector.next());
      assertSame(first, selector.next());
      assertSame(third, selector.next());
   }
}
