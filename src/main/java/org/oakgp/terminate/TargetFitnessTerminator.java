package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;

import org.oakgp.RankedCandidate;

public final class TargetFitnessTerminator implements Predicate<List<RankedCandidate>> {
   private Predicate<RankedCandidate> targetCriteira;

   public TargetFitnessTerminator(Predicate<RankedCandidate> targetCriteira) {
      this.targetCriteira = targetCriteira;
   }

   @Override
   public boolean test(List<RankedCandidate> t) {
      RankedCandidate bestCandidate = t.get(0);
      return targetCriteira.test(bestCandidate);
   }
}
