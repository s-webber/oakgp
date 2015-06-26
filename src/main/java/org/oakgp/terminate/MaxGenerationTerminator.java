package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.oakgp.RankedCandidate;

public class MaxGenerationTerminator implements Predicate<List<RankedCandidate>> {
   private final int maxGenerations;
   private int ctr = 0;
   private double previousBest = 0;

   public MaxGenerationTerminator(int maxGenerations) {
      this.maxGenerations = maxGenerations;
   }

   @Override
   public boolean test(List<RankedCandidate> t) {
      ctr++;
      RankedCandidate bestCandidate = t.get(0);
      double bestRank = bestCandidate.getFitness();
      boolean finished = ctr > maxGenerations || bestRank == 0;
      if (previousBest != bestRank) {
         previousBest = bestRank;
         Logger.getGlobal().info(ctr + " " + bestRank + " " + bestCandidate);
      } else if (finished || ctr % 100 == 0) {
         Logger.getGlobal().info(ctr + " " + bestRank);
      }
      return finished;
   }
}
