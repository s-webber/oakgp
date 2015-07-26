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

import java.util.Collection;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.evolve.GenerationEvolver;
import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.util.RunBuilderTest;

public class RunnerTest {
   @SuppressWarnings("unchecked")
   @Test
   public void test() {
      // Create mock objects to pass as arguments to process method of Runner
      GenerationRanker ranker = mock(GenerationRanker.class);
      GenerationEvolver evolver = mock(GenerationEvolver.class);
      Predicate<RankedCandidates> terminator = mock(Predicate.class);
      Collection<Node> initialPopulation = mock(Collection.class);

      RankedCandidate expected = RunBuilderTest.createRunExpectations(ranker, evolver, terminator, initialPopulation);

      // run test
      RankedCandidate actual = Runner.process(ranker, evolver, terminator, initialPopulation);

      // confirm output matches expected behaviour
      assertSame(expected, actual);
   }
}
