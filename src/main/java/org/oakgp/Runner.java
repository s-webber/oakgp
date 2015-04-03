package org.oakgp;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.oakgp.node.Node;

/** Performs a Genetic Programming run. */
public final class Runner {
   /** Private constructor as all methods are static. */
   private Runner() {
      // do nothing
   }

   /**
    * Performs a Genetic Programming run.
    *
    * @param generationProcessor
    *           sorts a generation by their fitness
    * @param generationEvolver
    *           creates a new generation based on the previous generation
    * @param terminator
    *           a function that determines if the run should finish
    * @param initialGeneration
    *           the initial population
    * @return the candidate with the best fitness that was found during this run
    */
   public static RankedCandidate process(GenerationProcessor generationProcessor, GenerationEvolver generationEvolver,
         Predicate<List<RankedCandidate>> terminator, Collection<Node> initialGeneration) {
      List<RankedCandidate> rankedCandidates = generationProcessor.process(initialGeneration);
      while (!terminator.test(rankedCandidates)) {
         Collection<Node> newGeneration = generationEvolver.process(rankedCandidates);
         rankedCandidates = generationProcessor.process(newGeneration);
      }
      return getBest(rankedCandidates);
   }

   private static RankedCandidate getBest(List<RankedCandidate> rankedCandidates) {
      return rankedCandidates.get(0);
   }
}
