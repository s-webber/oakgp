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
package org.oakgp.evolve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.oakgp.TestUtils.mockNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.oakgp.node.Node;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.select.NodeSelector;
import org.oakgp.select.NodeSelectorFactory;

public class GenerationEvolverImplTest {
   @Test
   public void test() {
      // create mock objects to pass as arguments to constructor
      int elitismSize = 3;
      NodeSelectorFactory selectorFactory = mock(NodeSelectorFactory.class);
      Map<GeneticOperator, Integer> operators = new HashMap<>();
      GeneticOperator generator1 = mock(GeneticOperator.class);
      GeneticOperator generator2 = mock(GeneticOperator.class);
      operators.put(generator1, 3);
      operators.put(generator2, 5);

      // create mock objects to return from process method
      // 10 = 3 selected by elitism, 3 selected by first generator, 4 selected by second generator (excluding duplicate)
      Node[] expectedOutput = new Node[10];
      for (int i = 0; i < expectedOutput.length; i++) {
         expectedOutput[i] = mockNode();
      }

      // create objects to pass as argument to process method
      RankedCandidates input = new RankedCandidates(new RankedCandidate[] { new RankedCandidate(expectedOutput[0], 1),
            new RankedCandidate(expectedOutput[1], 2), new RankedCandidate(expectedOutput[2], elitismSize), new RankedCandidate(mockNode(), 4),
            new RankedCandidate(mockNode(), 5) });

      // set expectations
      NodeSelector selector = mock(NodeSelector.class);
      when(selectorFactory.getSelector(input)).thenReturn(selector);
      when(generator1.evolve(selector)).thenReturn(expectedOutput[3], expectedOutput[4], expectedOutput[5]);
      // note: although expectedOutput[7] is returned twice it should only appear once in the actual output -
      // that will be confirmed when we check actualOutput.size()
      when(generator2.evolve(selector)).thenReturn(expectedOutput[6], expectedOutput[7], expectedOutput[8], expectedOutput[7], expectedOutput[9]);

      // execute process method
      GenerationEvolverImpl evolver = new GenerationEvolverImpl(elitismSize, selectorFactory, operators);
      Collection<Node> actualOutput = evolver.process(input);

      // check output
      assertEquals(expectedOutput.length, actualOutput.size());
      for (Node n : expectedOutput) {
         assertTrue(actualOutput.contains(n));
      }
   }
}
