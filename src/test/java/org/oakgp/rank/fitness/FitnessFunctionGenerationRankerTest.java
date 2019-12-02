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
package org.oakgp.rank.fitness;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertRankedCandidate;
import static org.oakgp.TestUtils.integerConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;
import org.oakgp.rank.GenerationRanker;
import org.oakgp.rank.RankedCandidates;

public class FitnessFunctionGenerationRankerTest {
   @Test
   public void test() {
      // test data
      Node a = integerConstant(1);
      double aFitness = 9;
      Node b = integerConstant(2);
      double bFitness = 12;
      Node c = integerConstant(3);
      double cFitness = 8;
      List<Node> input = Arrays.asList(a, b, c);

      // mock
      FitnessFunction mockFitnessFunction = mock(FitnessFunction.class);
      given(mockFitnessFunction.evaluate(a)).willReturn(aFitness);
      given(mockFitnessFunction.evaluate(b)).willReturn(bFitness);
      given(mockFitnessFunction.evaluate(c)).willReturn(cFitness);

      // invoke rank method
      GenerationRanker generationRanker = new FitnessFunctionGenerationRanker(mockFitnessFunction, true);
      RankedCandidates output = generationRanker.rank(input);

      // assert output
      assertRankedCandidate(output.get(0), c, cFitness);
      assertRankedCandidate(output.get(1), a, aFitness);
      assertRankedCandidate(output.get(2), b, bFitness);
   }
}
