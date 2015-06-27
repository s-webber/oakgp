package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;

import org.oakgp.RankedCandidate;

public final class CompositeTerminator implements Predicate<List<RankedCandidate>> {
   private final Predicate<List<RankedCandidate>>[] terminators;

   @SafeVarargs
   public CompositeTerminator(Predicate<List<RankedCandidate>>... terminators) {
      this.terminators = terminators;
   }

   @Override
   public boolean test(List<RankedCandidate> candidates) {
      for (Predicate<List<RankedCandidate>> t : terminators) {
         if (t.test(candidates)) {
            return true;
         }
      }
      return false;
   }
}
