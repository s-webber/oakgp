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
import static org.oakgp.TestUtils.integerConstant;
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
      RankedCandidate a = new RankedCandidate(null, 1);
      RankedCandidate b = new RankedCandidate(null, 2);
      RankedCandidate c = new RankedCandidate(null, 1);
      assertEquals(-1, a.compareTo(b));
      assertEquals(1, b.compareTo(a));
      assertEquals(0, a.compareTo(c));
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
   }

   @Test
   public void testToString() {
      RankedCandidate rankedCandidate = new RankedCandidate(readNode("(+ 2 v0)"), 85.75);
      assertEquals("[(+ 2 v0) 85.75]", rankedCandidate.toString());
   }
}
