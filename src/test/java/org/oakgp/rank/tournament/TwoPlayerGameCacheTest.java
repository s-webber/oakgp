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
package org.oakgp.rank.tournament;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.integerConstant;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.oakgp.node.Node;

public class TwoPlayerGameCacheTest {
   @Test
   public void test() {
      // set-up expectations
      final Node n1 = integerConstant(1);
      final Node n2 = integerConstant(2);
      final Node n3 = integerConstant(3);
      final double fitness1 = 7;
      final double fitness2 = 4;
      final AtomicInteger evaluateCtr = new AtomicInteger(0);
      final TwoPlayerGame mockTwoPlayerGame = (player1, player2) -> {
         evaluateCtr.incrementAndGet();
         if (n1 == player1 && n2 == player2) {
            return fitness1;
         } else if (n1 == player1 && n3 == player2) {
            return fitness2;
         } else {
            throw new IllegalArgumentException();
         }
      };

      // create object to test
      final TwoPlayerGameCache cache = new TwoPlayerGameCache(3, mockTwoPlayerGame);

      // test evaluate is only called once per node-pair
      assertEquals(fitness1, cache.evaluate(n1, n2), 0);
      assertEquals(-fitness1, cache.evaluate(n2, n1), 0);
      assertEquals(-fitness1, cache.evaluate(n2, n1), 0);
      assertEquals(fitness1, cache.evaluate(n1, n2), 0);
      assertEquals(1, evaluateCtr.get());

      assertEquals(fitness2, cache.evaluate(n1, n3), 0);
      assertEquals(-fitness2, cache.evaluate(n3, n1), 0);
      assertEquals(fitness2, cache.evaluate(n1, n3), 0);
      assertEquals(-fitness2, cache.evaluate(n3, n1), 0);
      assertEquals(2, evaluateCtr.get());
   }
}
