package org.oakgp.examples;

import java.util.List;
import java.util.function.Predicate;

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
      double best = t.get(0).getFitness();
      boolean finished = ctr > maxGenerations || best == 0;
      if (previousBest != best) {
         previousBest = best;
         System.out.println(ctr + " " + best);
      } else if (finished || ctr % 100 == 0) {
         System.out.println(ctr + " " + best);
      }
      return finished;
   }
}
