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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.oakgp.TestUtils.singletonRankedCandidates;

import java.util.Collection;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.evolve.GenerationEvolver;
import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;

public class RunnerTest {
   @SuppressWarnings("unchecked")
   @Test
   public void test() {
      // Create mock objects to pass as arguments to process method of Runner
      GenerationRanker ranker = mock(GenerationRanker.class);
      GenerationEvolver evolver = mock(GenerationEvolver.class);
      Predicate<RankedCandidates> terminator = mock(Predicate.class);
      Collection<Node> initialPopulation = mock(Collection.class);

      // create mock objects used in processing
      RankedCandidates rankedInitialPopulation = singletonRankedCandidates();
      Collection<Node> secondGeneration = mock(Collection.class);
      RankedCandidates rankedSecondGeneration = singletonRankedCandidates();
      Collection<Node> thirdGeneration = mock(Collection.class);
      RankedCandidates rankedThirdGeneration = singletonRankedCandidates();
      Collection<Node> fourthGeneration = mock(Collection.class);
      RankedCandidates rankedFourthGeneration = singletonRankedCandidates();

      // expectations for initial population
      when(ranker.rank(initialPopulation)).thenReturn(rankedInitialPopulation);
      when(evolver.process(rankedInitialPopulation)).thenReturn(secondGeneration);

      // expectations for second generation
      when(ranker.rank(secondGeneration)).thenReturn(rankedSecondGeneration);
      when(evolver.process(rankedSecondGeneration)).thenReturn(thirdGeneration);

      // expectations for third generation
      when(ranker.rank(thirdGeneration)).thenReturn(rankedThirdGeneration);
      when(evolver.process(rankedThirdGeneration)).thenReturn(fourthGeneration);

      // expectations for fourth generation
      when(ranker.rank(fourthGeneration)).thenReturn(rankedFourthGeneration);
      when(terminator.test(rankedFourthGeneration)).thenReturn(true);

      // run test
      RankedCandidate actual = Runner.process(ranker, evolver, terminator, initialPopulation);

      // confirm output matches expected behaviour
      assertSame(rankedFourthGeneration.best(), actual);
   }
}
