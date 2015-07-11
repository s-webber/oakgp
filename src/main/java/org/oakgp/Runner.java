/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.oakgp.evolve.GenerationEvolver;
import org.oakgp.node.Node;
import org.oakgp.rank.GenerationProcessor;
import org.oakgp.rank.RankedCandidate;

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
    * @param initialPopulation
    *           the initial population that will be used as a basis for generating future generations
    * @return the candidate with the best fitness that was found during this run
    */
   public static RankedCandidate process(GenerationProcessor generationProcessor, GenerationEvolver generationEvolver,
         Predicate<List<RankedCandidate>> terminator, Collection<Node> initialPopulation) {
      List<RankedCandidate> rankedCandidates = generationProcessor.process(initialPopulation);
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
