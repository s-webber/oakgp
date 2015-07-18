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
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.mockNode;

import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

public class RankedCandidatesTest {
   private final RankedCandidate element1 = new RankedCandidate(mockNode(), -7);
   private final RankedCandidate element2 = new RankedCandidate(mockNode(), -2.25);
   private final RankedCandidate element3 = new RankedCandidate(mockNode(), 0);
   private final RankedCandidate element4 = new RankedCandidate(mockNode(), 1);
   private final RankedCandidate element5 = new RankedCandidate(mockNode(), 785.5);
   private final RankedCandidate[] input = { element5, element3, element1, element2, element4 };
   private final RankedCandidates rankedCandidates = new RankedCandidates(input);

   @Test
   public void testSize() {
      assertEquals(5, rankedCandidates.size());
   }

   @Test
   public void testGet() {
      assertSame(element1, rankedCandidates.get(0));
      assertSame(element2, rankedCandidates.get(1));
      assertSame(element3, rankedCandidates.get(2));
      assertSame(element4, rankedCandidates.get(3));
      assertSame(element5, rankedCandidates.get(4));
   }

   @Test
   public void testNext() {
      Iterator<RankedCandidate> itr = rankedCandidates.iterator();
      assertSame(element1, itr.next());
      assertSame(element2, itr.next());
      assertSame(element3, itr.next());
      assertSame(element4, itr.next());
      assertSame(element5, itr.next());
      try {
         itr.next();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         // expected
      }
   }

   @Test
   public void testHasNext() {
      Iterator<RankedCandidate> itr = rankedCandidates.iterator();
      assertTrue(itr.hasNext());
      assertTrue(itr.hasNext());
      assertSame(element1, itr.next());
      assertTrue(itr.hasNext());
      assertSame(element2, itr.next());
      assertTrue(itr.hasNext());
      assertSame(element3, itr.next());
      assertTrue(itr.hasNext());
      assertSame(element4, itr.next());
      assertTrue(itr.hasNext());
      assertSame(element5, itr.next());
      assertFalse(itr.hasNext());
      assertFalse(itr.hasNext());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testRemove() {
      Iterator<RankedCandidate> itr = rankedCandidates.iterator();
      itr.remove();
   }

   @Test
   public void testCustomComparator() {
      RankedCandidates defaultOrderedCandidates = new RankedCandidates(input);
      RankedCandidates reverseOrderedCandidates = new RankedCandidates(input, Collections.reverseOrder());
      assertEquals(5, defaultOrderedCandidates.size());
      assertEquals(5, reverseOrderedCandidates.size());
      assertSame(defaultOrderedCandidates.get(0), reverseOrderedCandidates.get(4));
      assertSame(defaultOrderedCandidates.get(1), reverseOrderedCandidates.get(3));
      assertSame(defaultOrderedCandidates.get(2), reverseOrderedCandidates.get(2));
      assertSame(defaultOrderedCandidates.get(3), reverseOrderedCandidates.get(1));
      assertSame(defaultOrderedCandidates.get(4), reverseOrderedCandidates.get(0));
   }

   @Test
   public void testBest() {
      RankedCandidates defaultOrderedCandidates = new RankedCandidates(input);
      RankedCandidates reverseOrderedCandidates = new RankedCandidates(input, Collections.reverseOrder());
      assertSame(element1, defaultOrderedCandidates.best());
      assertSame(element5, reverseOrderedCandidates.best());
   }
}
