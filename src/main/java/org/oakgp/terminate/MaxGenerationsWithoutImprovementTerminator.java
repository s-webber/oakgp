package org.oakgp.terminate;

import java.util.List;
import java.util.function.Predicate;

import org.oakgp.RankedCandidate;

public final class MaxGenerationsWithoutImprovementTerminator implements Predicate<List<RankedCandidate>> {
   private final int maxGenerationsWithoutImprovement;
   private int currentGenerationsWithoutImprovement;
   private double currentBest;

   public MaxGenerationsWithoutImprovementTerminator(int maxGenerationsWithoutImprovement) {
      this.maxGenerationsWithoutImprovement = maxGenerationsWithoutImprovement;
   }

   @Override
   public boolean test(List<RankedCandidate> t) {
      double best = t.get(0).getFitness();
      if (best != currentBest) {
         currentGenerationsWithoutImprovement = 0;
         currentBest = best;
         return false;
      } else {
         return ++currentGenerationsWithoutImprovement >= maxGenerationsWithoutImprovement;
      }
   }
}
