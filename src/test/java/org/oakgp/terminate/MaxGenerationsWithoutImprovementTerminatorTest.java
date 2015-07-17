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
package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.singletonRankedCandidates;

import org.junit.Test;

public class MaxGenerationsWithoutImprovementTerminatorTest {
   @Test
   public void test() {
      assertTerminator(2, 9, 9, 9);
      assertTerminator(3, 9, 9, 9, 9);
      assertTerminator(3, 9, 9, 9, 8, 8, 7, 6, 6, 6, 6);
      assertTerminator(4, 9, 9, 9, 8, 8, 7, 6, 6, 6, 6, 5, 5, 5, 5, 5);
   }

   private void assertTerminator(int maxGenerationsWithoutImprovement, double... fitnesses) {
      MaxGenerationsWithoutImprovementTerminator t = new MaxGenerationsWithoutImprovementTerminator(maxGenerationsWithoutImprovement);
      for (int i = 0; i < fitnesses.length - 1; i++) {
         assertFalse(t.test(singletonRankedCandidates(fitnesses[i])));
      }
      assertTrue(t.test(singletonRankedCandidates(fitnesses[fitnesses.length - 1])));
   }
}
