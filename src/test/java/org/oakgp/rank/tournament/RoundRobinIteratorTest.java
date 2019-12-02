/*
 * Copyright 2019 S. Webber
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
package org.oakgp.rank.tournament;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.oakgp.rank.tournament.RoundRobinIterator.Pair;

public class RoundRobinIteratorTest {
   @Test
   public void test() {
      RoundRobinIterator<String> itr = new RoundRobinIterator<>(new String[] { "a", "b", "c", "d" });

      assertTrue(itr.hasNext());
      Pair<String> next = itr.next();
      assertEquals(next.getLeft(), "a");
      assertEquals(next.getRight(), "b");

      assertTrue(itr.hasNext());
      next = itr.next();
      assertEquals(next.getLeft(), "a");
      assertEquals(next.getRight(), "c");

      assertTrue(itr.hasNext());
      next = itr.next();
      assertEquals(next.getLeft(), "a");
      assertEquals(next.getRight(), "d");

      assertTrue(itr.hasNext());
      next = itr.next();
      assertEquals(next.getLeft(), "b");
      assertEquals(next.getRight(), "c");

      assertTrue(itr.hasNext());
      next = itr.next();
      assertEquals(next.getLeft(), "b");
      assertEquals(next.getRight(), "d");

      assertTrue(itr.hasNext());
      next = itr.next();
      assertEquals(next.getLeft(), "c");
      assertEquals(next.getRight(), "d");

      assertFalse(itr.hasNext());
   }
}
