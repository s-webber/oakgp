package org.oakgp.selector;

import java.util.List;

import org.oakgp.RankedCandidate;
import org.oakgp.util.Random;

/** Returns instances of {@code WeightedNodeSelector}. */
public final class WeightedNodeSelectorFactory implements NodeSelectorFactory {
   private final Random random;

   public WeightedNodeSelectorFactory(Random random) {
      this.random = random;
   }

   @Override
   public WeightedNodeSelector getSelector(List<RankedCandidate> candidates) {
      return new WeightedNodeSelector(random, candidates);
   }
}
