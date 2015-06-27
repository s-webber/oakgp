package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.oakgp.RankedCandidate;

public final class MaxGenerationsTerminator implements Predicate<List<RankedCandidate>> {
   private final int maxGenerations;
   private int ctr = 0;

   public MaxGenerationsTerminator(int maxGenerations) {
      this.maxGenerations = maxGenerations;
   }

   @Override
   public boolean test(List<RankedCandidate> t) {
      ctr++;
      boolean finished = ctr > maxGenerations;
      if (finished || ctr % 100 == 0) {
         Logger.getGlobal().info(ctr + " " + t.get(0).getFitness());
      }
      return finished;
   }
}
