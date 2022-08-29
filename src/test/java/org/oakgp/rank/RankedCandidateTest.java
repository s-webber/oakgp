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
package org.oakgp.rank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.mockNode;
import static org.oakgp.TestUtils.readNode;

import org.junit.Test;
import org.oakgp.node.Node;

public class RankedCandidateTest {
   @Test
   public void testGetters() {
      Node n = integerConstant(0);
      double f = 7.5;
      RankedCandidate a = new RankedCandidate(n, f);
      assertSame(n, a.getNode());
      assertEquals(f, a.getFitness(), 0);
   }

   @Test
   public void testCompareTo() {
      final double baseFitness = 500d;
      final int baseNodeCount = 3;
      final double betterFitness = 250d;
      final int betterNodeCount = 1;
      final double worseFitness = 5000d;
      final int worseNodeCount = 5;

      RankedCandidate candidate = mockRankedCandidate(baseFitness, baseNodeCount);

      assertEqual(candidate, candidate);

      assertBetter(candidate, mockRankedCandidate(worseFitness, worseNodeCount));
      assertBetter(candidate, mockRankedCandidate(worseFitness, betterNodeCount));
      assertBetter(candidate, mockRankedCandidate(worseFitness, baseNodeCount));

      assertWorse(candidate, mockRankedCandidate(betterFitness, worseNodeCount));
      assertWorse(candidate, mockRankedCandidate(betterFitness, betterNodeCount));
      assertWorse(candidate, mockRankedCandidate(betterFitness, baseNodeCount));

      assertBetter(candidate, mockRankedCandidate(baseFitness, worseNodeCount));
      assertWorse(candidate, mockRankedCandidate(baseFitness, betterNodeCount));
      assertEqual(candidate, mockRankedCandidate(baseFitness, baseNodeCount));
   }

   private RankedCandidate mockRankedCandidate(double fitness, int nodeCount) {
      Node mockNode = mockNode();
      given(mockNode.getNodeCount()).willReturn(nodeCount);
      return new RankedCandidate(mockNode, fitness);
   }

   public void assertBetter(RankedCandidate a, RankedCandidate b) {
      assertEquals(-1, a.compareTo(b));
      assertEquals(1, b.compareTo(a));
   }

   public void assertWorse(RankedCandidate a, RankedCandidate b) {
      assertBetter(b, a);
   }

   public void assertEqual(RankedCandidate a, RankedCandidate b) {
      assertEquals(0, a.compareTo(b));
      assertEquals(0, b.compareTo(a));
   }

   @Test
   public void testEquals() {
      double f = 7.5;
      RankedCandidate a = new RankedCandidate(integerConstant(0), f);
      RankedCandidate b = new RankedCandidate(integerConstant(0), f);
      RankedCandidate c = new RankedCandidate(integerConstant(0), f * 2);
      RankedCandidate d = new RankedCandidate(integerConstant(7), f);

      assertTrue(a.equals(a));
      assertEquals(a.hashCode(), a.hashCode());

      assertTrue(a.equals(b));
      assertEquals(a.hashCode(), b.hashCode());

      assertFalse(a.equals(c));

      assertFalse(a.equals(d));

      assertFalse(a.equals("string"));
   }

   @Test
   public void testToString() {
      RankedCandidate rankedCandidate = new RankedCandidate(readNode("(+ 2 v0)"), 85.75);
      assertEquals("[(+ 2 v0) 85.75]", rankedCandidate.toString());
   }
}
