package org.oakgp.fitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.oakgp.GenerationProcessor;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;

/** Ranks and sorts the fitness of {@code Node} instances using a {@code FitnessFunction}. */
public final class FitnessFunctionGenerationProcessor implements GenerationProcessor {
   private final FitnessFunction fitnessFunction;

   /**
    * Constructs a {@code GenerationProcessor} with the specified {@code FitnessFunction}
    *
    * @param fitnessFunction
    *           the {@code FitnessFunction} to use when determining the fitness of candidates
    */
   public FitnessFunctionGenerationProcessor(FitnessFunction fitnessFunction) {
      this.fitnessFunction = fitnessFunction;
   }

   /**
    * Returns the sorted result of applying this object's {@code FitnessFunction} against each of the specified nodes.
    *
    * @param input
    *           the {@code Node} instances to apply this object's {@code FitnessFunction} against
    * @return a {@code List} of {@code RankedCandidate} - one for each {@code Node} specified in {@code input} - sorted by fitness
    */
   @Override
   public List<RankedCandidate> process(Collection<Node> input) {
      List<RankedCandidate> output = new ArrayList<>(input.size());
      for (Node n : input) {
         RankedCandidate rankedCandidate = rankCandidate(n);
         output.add(rankedCandidate);
      }
      Collections.sort(output);
      return output;
   }

   private RankedCandidate rankCandidate(Node n) {
      return new RankedCandidate(n, fitnessFunction.evaluate(n));
   }
}
